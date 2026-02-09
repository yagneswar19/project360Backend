package com.rewards360.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rewards360.auth.dto.AuthResponse;
import com.rewards360.auth.dto.LoginRequest;
import com.rewards360.auth.dto.RegisterRequest;
import com.rewards360.auth.dto.UpdatePointsRequest;
import com.rewards360.auth.dto.UserDTO;
import com.rewards360.auth.dto.UserProfileDTO;
import com.rewards360.auth.exception.UserAlreadyExistsException;
import com.rewards360.auth.exception.UserNotFoundException;
import com.rewards360.auth.model.CustomerProfile;
import com.rewards360.auth.model.Role;
import com.rewards360.auth.model.User;
import com.rewards360.auth.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * POST /api/auth/register
     * Register a new user with profile.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        // Check if email already exists
        if (userRepository.existsByEmail(req.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.email());
        }

        // Create user
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.valueOf(req.role().toUpperCase()));

        // Create customer profile
        CustomerProfile profile = new CustomerProfile();
        profile.setLoyaltyTier("Bronze");
        profile.setPointsBalance(2000);
        profile.setLifetimePoints(2000);
        profile.setPreferences(req.preferences());
        profile.setCommunication(req.communication());
        profile.setUser(user);

        // Set profile to user
        user.setProfile(profile);

        // Save user (cascades to profile)
        userRepository.save(user);

        AuthResponse response = new AuthResponse("User registered successfully", user.getRole().name(), user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Authenticate user with email and password.
     * No JWT — just validates credentials and returns success response.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        // Authenticate using Spring Security AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        // If authentication passed, fetch the user
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + req.email()));

        AuthResponse response = new AuthResponse("Login successful", user.getRole().name(), user.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/validate?email={email}
     * Internal endpoint called by other microservices (user-service, admin-service) via Feign.
     * Returns basic user info to verify user exists.
     */
    @GetMapping("/validate")
    public ResponseEntity<UserDTO> validateUser(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        UserDTO dto = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(dto);
    }

    // =============================================
    // INTERNAL ENDPOINTS (called by user-service via Feign)
    // =============================================

    /**
     * GET /api/auth/internal/profile?email={email}
     * Returns full user + profile data to user-service.
     */
    @GetMapping("/internal/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        CustomerProfile profile = user.getProfile();

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getRole().name(),
                profile != null ? profile.getLoyaltyTier() : "Bronze",
                profile != null ? profile.getPointsBalance() : 0,
                profile != null ? profile.getLifetimePoints() : 0,
                profile != null ? profile.getNextExpiry() : null,
                profile != null ? profile.getPreferences() : null,
                profile != null ? profile.getCommunication() : null
        );

        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/auth/internal/update-points
     * Called by user-service when claiming points or redeeming offers.
     * Updates the user's points balance and lifetime points.
     */
    @PutMapping("/internal/update-points")
    public ResponseEntity<UserProfileDTO> updateUserPoints(@RequestBody UpdatePointsRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getEmail()));

        CustomerProfile profile = user.getProfile();

        if (profile == null) {
            throw new UserNotFoundException("Profile not found for user: " + request.getEmail());
        }

        // Add points
        if (request.getPointsToAdd() > 0) {
            profile.setPointsBalance(profile.getPointsBalance() + request.getPointsToAdd());
            profile.setLifetimePoints(profile.getLifetimePoints() + request.getPointsToAdd());
        }

        // Deduct points
        if (request.getPointsToDeduct() > 0) {
            profile.setPointsBalance(profile.getPointsBalance() - request.getPointsToDeduct());
        }

        userRepository.save(user);

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getRole().name(),
                profile.getLoyaltyTier(), profile.getPointsBalance(), profile.getLifetimePoints(),
                profile.getNextExpiry(), profile.getPreferences(), profile.getCommunication()
        );

        return ResponseEntity.ok(dto);
    }
}
