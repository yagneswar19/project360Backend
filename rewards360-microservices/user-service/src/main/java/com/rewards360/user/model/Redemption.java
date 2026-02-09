package com.rewards360.user.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "redemption")
public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "confirmation_code", unique = true, nullable = false, length = 50)
    private String confirmationCode;

    @Column(name = "transaction_id", nullable = false, length = 50)
    private String transactionId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "cost_points", nullable = false)
    private int costPoints;

    @Column(name = "offer_title", nullable = false, length = 200)
    private String offerTitle;

    @Column(name = "store", length = 100)
    private String store;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Redemption() {
    }

    public Redemption(Long id, String confirmationCode, String transactionId, LocalDate date,
                      int costPoints, String offerTitle, String store, Long userId) {
        this.id = id;
        this.confirmationCode = confirmationCode;
        this.transactionId = transactionId;
        this.date = date;
        this.costPoints = costPoints;
        this.offerTitle = offerTitle;
        this.store = store;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCostPoints() {
        return costPoints;
    }

    public void setCostPoints(int costPoints) {
        this.costPoints = costPoints;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
