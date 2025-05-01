package com.backbase.repository;

import com.backbase.entity.MovieAward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieAwardRepository extends JpaRepository<MovieAward, Long> {
    boolean existsByTitle(String title);
}
