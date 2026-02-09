package com.rewards360.user.dto;

public record ClaimRequest(String activityCode, int points, String note) {
}
