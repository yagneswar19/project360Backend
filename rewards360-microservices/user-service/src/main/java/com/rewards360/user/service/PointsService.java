package com.rewards360.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rewards360.user.dto.ClaimRequest;
import com.rewards360.user.dto.RedeemRequest;
import com.rewards360.user.dto.UpdatePointsRequest;
import com.rewards360.user.dto.UserProfileDTO;
import com.rewards360.user.exception.InsufficientPointsException;
import com.rewards360.user.exception.OfferNotFoundException;
import com.rewards360.user.feign.AuthServiceClient;
import com.rewards360.user.model.Offer;
import com.rewards360.user.model.Redemption;
import com.rewards360.user.model.Transaction;
import com.rewards360.user.repository.OfferRepository;
import com.rewards360.user.repository.RedemptionRepository;
import com.rewards360.user.repository.TransactionRepository;

@Service
public class PointsService {

    private final OfferRepository offerRepository;
    private final TransactionRepository transactionRepository;
    private final RedemptionRepository redemptionRepository;
    private final AuthServiceClient authServiceClient;

    public PointsService(OfferRepository offerRepository,
                         TransactionRepository transactionRepository,
                         RedemptionRepository redemptionRepository,
                         AuthServiceClient authServiceClient) {
        this.offerRepository = offerRepository;
        this.transactionRepository = transactionRepository;
        this.redemptionRepository = redemptionRepository;
        this.authServiceClient = authServiceClient;
    }

    @Transactional
    public Transaction claimPoints(UserProfileDTO userProfile, ClaimRequest request) {
        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setExternalId("CLAIM-" + System.currentTimeMillis());
        transaction.setType("CLAIM");
        transaction.setPointsEarned(request.points());
        transaction.setPointsRedeemed(0);
        transaction.setStore("Online");
        transaction.setDate(LocalDate.now());
        transaction.setExpiry(LocalDate.now().plusMonths(3));
        transaction.setNote(request.note());
        transaction.setUserId(userProfile.getId());

        // Save transaction
        transactionRepository.save(transaction);

        // Call auth-service to update points balance
        UpdatePointsRequest pointsUpdate = new UpdatePointsRequest(
                userProfile.getEmail(), request.points(), 0);
        authServiceClient.updateUserPoints(pointsUpdate);

        return transaction;
    }

    @Transactional
    public Redemption redeemOffer(UserProfileDTO userProfile, RedeemRequest request) {
        // Find offer
        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new OfferNotFoundException("Offer not found with id: " + request.offerId()));

        // Check if user has enough points
        int userPoints = userProfile.getPointsBalance();
        int offerCost = offer.getCostPoints();

        if (userPoints < offerCost) {
            throw new InsufficientPointsException("Not enough points. You have " + userPoints
                    + " but need " + offerCost + " points");
        }

        // Create redemption transaction
        Transaction transaction = new Transaction();
        transaction.setExternalId("RED-" + System.currentTimeMillis());
        transaction.setType("REDEMPTION");
        transaction.setPointsEarned(0);
        transaction.setPointsRedeemed(offerCost);
        transaction.setStore(request.store());
        transaction.setDate(LocalDate.now());
        transaction.setNote(offer.getTitle());
        transaction.setUserId(userProfile.getId());

        transactionRepository.save(transaction);

        // Create redemption record
        Redemption redemption = new Redemption();
        redemption.setTransactionId(transaction.getExternalId());
        redemption.setConfirmationCode("CONF-" + System.currentTimeMillis());
        redemption.setDate(LocalDate.now());
        redemption.setCostPoints(offerCost);
        redemption.setOfferTitle(offer.getTitle());
        redemption.setStore(request.store());
        redemption.setUserId(userProfile.getId());

        redemptionRepository.save(redemption);

        // Call auth-service to deduct points
        UpdatePointsRequest pointsUpdate = new UpdatePointsRequest(
                userProfile.getEmail(), 0, offerCost);
        authServiceClient.updateUserPoints(pointsUpdate);

        return redemption;
    }
}
