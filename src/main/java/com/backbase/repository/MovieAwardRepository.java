package com.backbase.repository;

import com.backbase.entity.MovieAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieAwardRepository extends JpaRepository<MovieAward, Long> {
    boolean existsByTitle(String title);

    @Query("SELECT LOWER(m.title) FROM MovieAward m")
    List<String> findAllTitlesLowerCase();
}
