package com.rewards360.user.dto;

/**
 * DTO for requesting auth-service to update user points.
 */
public class UpdatePointsRequest {

    private String email;
    private int pointsToAdd;
    private int pointsToDeduct;

    public UpdatePointsRequest() {
    }

    public UpdatePointsRequest(String email, int pointsToAdd, int pointsToDeduct) {
        this.email = email;
        this.pointsToAdd = pointsToAdd;
        this.pointsToDeduct = pointsToDeduct;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPointsToAdd() {
        return pointsToAdd;
    }

    public void setPointsToAdd(int pointsToAdd) {
        this.pointsToAdd = pointsToAdd;
    }

    public int getPointsToDeduct() {
        return pointsToDeduct;
    }

    public void setPointsToDeduct(int pointsToDeduct) {
        this.pointsToDeduct = pointsToDeduct;
    }
}
