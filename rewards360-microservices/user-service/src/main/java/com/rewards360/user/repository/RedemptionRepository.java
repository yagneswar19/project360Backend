package com.rewards360.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rewards360.user.model.Redemption;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByUserIdOrderByDateDesc(Long userId);
}
