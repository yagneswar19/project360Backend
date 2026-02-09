package com.rewards360.user.dto;

import java.time.LocalDate;

/**
 * DTO to receive full user + profile data from auth-service.
 * Maps to auth-service's User + CustomerProfile entities.
 */
public class UserProfileDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;

    // Profile fields
    private String loyaltyTier;
    private int pointsBalance;
    private int lifetimePoints;
    private LocalDate nextExpiry;
    private String preferences;
    private String communication;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long id, String name, String email, String phone, String role,
                          String loyaltyTier, int pointsBalance, int lifetimePoints,
                          LocalDate nextExpiry, String preferences, String communication) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.loyaltyTier = loyaltyTier;
        this.pointsBalance = pointsBalance;
        this.lifetimePoints = lifetimePoints;
        this.nextExpiry = nextExpiry;
        this.preferences = preferences;
        this.communication = communication;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public int getLifetimePoints() {
        return lifetimePoints;
    }

    public void setLifetimePoints(int lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }

    public LocalDate getNextExpiry() {
        return nextExpiry;
    }

    public void setNextExpiry(LocalDate nextExpiry) {
        this.nextExpiry = nextExpiry;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }
}
