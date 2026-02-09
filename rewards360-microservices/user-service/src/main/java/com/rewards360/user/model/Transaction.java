package com.rewards360.user.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, nullable = false, length = 50)
    private String externalId;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "points_earned", nullable = false)
    private int pointsEarned;

    @Column(name = "points_redeemed", nullable = false)
    private int pointsRedeemed;

    @Column(name = "store", length = 100)
    private String store;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "expiry")
    private LocalDate expiry;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Transaction() {
    }

    public Transaction(Long id, String externalId, String type, int pointsEarned, int pointsRedeemed,
                       String store, LocalDate date, LocalDate expiry, String note, Long userId) {
        this.id = id;
        this.externalId = externalId;
        this.type = type;
        this.pointsEarned = pointsEarned;
        this.pointsRedeemed = pointsRedeemed;
        this.store = store;
        this.date = date;
        this.expiry = expiry;
        this.note = note;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
