package com.backbase.repository;

import com.backbase.entity.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {
}
