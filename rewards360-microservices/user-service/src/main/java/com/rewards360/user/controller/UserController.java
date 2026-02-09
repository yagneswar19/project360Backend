package com.rewards360.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rewards360.user.dto.ClaimRequest;
import com.rewards360.user.dto.RedeemRequest;
import com.rewards360.user.dto.UserDTO;
import com.rewards360.user.dto.UserProfileDTO;
import com.rewards360.user.exception.UserNotFoundException;
import com.rewards360.user.feign.AuthServiceClient;
import com.rewards360.user.model.Offer;
import com.rewards360.user.model.Redemption;
import com.rewards360.user.model.Transaction;
import com.rewards360.user.repository.OfferRepository;
import com.rewards360.user.repository.RedemptionRepository;
import com.rewards360.user.repository.TransactionRepository;
import com.rewards360.user.service.PointsService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final OfferRepository offerRepository;
    private final TransactionRepository transactionRepository;
    private final RedemptionRepository redemptionRepository;
    private final PointsService pointsService;
    private final AuthServiceClient authServiceClient;

    public UserController(OfferRepository offerRepository,
                          TransactionRepository transactionRepository,
                          RedemptionRepository redemptionRepository,
                          PointsService pointsService,
                          AuthServiceClient authServiceClient) {
        this.offerRepository = offerRepository;
        this.transactionRepository = transactionRepository;
        this.redemptionRepository = redemptionRepository;
        this.pointsService = pointsService;
        this.authServiceClient = authServiceClient;
    }

    // =============================================
    // HELPER: Get user profile from auth-service via Feign
    // =============================================

    private UserProfileDTO getValidatedProfile(String email) {
        if (email == null || email.isBlank()) {
            throw new UserNotFoundException("Email header (X-User-Email) is required");
        }
        return authServiceClient.getUserProfile(email);
    }

    // =============================================
    // USER ENDPOINTS (all user data comes from auth-service via Feign)
    // Frontend must send X-User-Email header after login
    // =============================================

    // Get current user profile (full profile from auth-service)
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        return ResponseEntity.ok(profile);
    }

    // Validate user via Feign (lightweight check)
    @GetMapping("/validate")
    public ResponseEntity<UserDTO> validateUserViaAuthService(@RequestHeader("X-User-Email") String email) {
        UserDTO userDTO = authServiceClient.validateUser(email);
        return ResponseEntity.ok(userDTO);
    }

    // Get all offers
    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
        return ResponseEntity.ok(offers);
    }

    // Get offers based on user tier (tier fetched from auth-service)
    @GetMapping("/offers/my-tier")
    public ResponseEntity<List<Offer>> getOffersForMyTier(@RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        List<Offer> offers = offerRepository.findAvailableOffersForTier(profile.getLoyaltyTier());
        return ResponseEntity.ok(offers);
    }

    // Claim points
    @PostMapping("/claim")
    public ResponseEntity<Transaction> claimPoints(@RequestBody ClaimRequest request,
                                                   @RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        Transaction transaction = pointsService.claimPoints(profile, request);
        return ResponseEntity.ok(transaction);
    }

    // Redeem offer
    @PostMapping("/redeem")
    public ResponseEntity<Redemption> redeemOffer(@RequestBody RedeemRequest request,
                                                  @RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        Redemption redemption = pointsService.redeemOffer(profile, request);
        return ResponseEntity.ok(redemption);
    }

    // Get user transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getMyTransactions(@RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(profile.getId());
        return ResponseEntity.ok(transactions);
    }

    // Get user redemptions
    @GetMapping("/redemptions")
    public ResponseEntity<List<Redemption>> getMyRedemptions(@RequestHeader("X-User-Email") String email) {
        UserProfileDTO profile = getValidatedProfile(email);
        List<Redemption> redemptions = redemptionRepository.findByUserIdOrderByDateDesc(profile.getId());
        return ResponseEntity.ok(redemptions);
    }
}
