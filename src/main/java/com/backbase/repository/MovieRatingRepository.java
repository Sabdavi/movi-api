package com.backbase.repository;

import com.backbase.entity.MovieRating;
import com.backbase.projection.MovieAverageRatingProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {

    @Query(value = "SELECT r.title AS title, AVG(r.rate) AS averageRating " +
            "FROM MovieRating r " +
            "GROUP BY r.title " +
            "ORDER BY averageRating DESC")
    List<MovieAverageRatingProjection> findAverageRatingsByTitle(Pageable pageable);
}
