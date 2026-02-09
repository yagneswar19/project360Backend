package com.rewards360.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "offer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "cost_points", nullable = false)
    private int costPoints;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "tier_level", length = 20)
    private String tierLevel;

    public Offer() {
    }

    public Offer(Long id, String title, String category, String description,
                 int costPoints, String imageUrl, boolean active, String tierLevel) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.costPoints = costPoints;
        this.imageUrl = imageUrl;
        this.active = active;
        this.tierLevel = tierLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCostPoints() {
        return costPoints;
    }

    public void setCostPoints(int costPoints) {
        this.costPoints = costPoints;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(String tierLevel) {
        this.tierLevel = tierLevel;
    }
}
