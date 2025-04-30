package com.backbase.repository;

import com.backbase.entity.BestPictureWinner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestPictureWinnerRepository extends JpaRepository<BestPictureWinner, Long> {
    boolean existsByTitle(String title);
}
