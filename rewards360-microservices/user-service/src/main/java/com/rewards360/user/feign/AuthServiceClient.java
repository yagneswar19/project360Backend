package com.rewards360.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rewards360.user.dto.UpdatePointsRequest;
import com.rewards360.user.dto.UserDTO;
import com.rewards360.user.dto.UserProfileDTO;

/**
 * Feign Client to communicate with auth-service.
 * 
 * auth-service endpoints used:
 *   GET  /api/auth/validate?email=       → validates user, returns UserDTO
 *   GET  /api/auth/internal/profile?email= → returns full UserProfileDTO
 *   PUT  /api/auth/internal/update-points → updates user points balance
 */
@FeignClient(name = "auth-service", url = "${feign.auth-service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/auth/validate")
    UserDTO validateUser(@RequestParam("email") String email);

    @GetMapping("/api/auth/internal/profile")
    UserProfileDTO getUserProfile(@RequestParam("email") String email);

    @PutMapping("/api/auth/internal/update-points")
    UserProfileDTO updateUserPoints(@RequestBody UpdatePointsRequest request);
}
