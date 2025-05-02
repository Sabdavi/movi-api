package com.backbase;

import com.backbase.dto.MovieAverageRating;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class MovieRatingServiceTest {

    @Test
    void testSorting() {
        List<MovieAverageRating> movieAverageRatings = new ArrayList<>();
        movieAverageRatings.add(new MovieAverageRating("A", 5.0, 200L));
        movieAverageRatings.add(new MovieAverageRating("B", 5.0, 100L));
        movieAverageRatings.add(new MovieAverageRating("C", 6.0, 50L));


        Comparator<MovieAverageRating> comparator =
                Comparator.comparingDouble(MovieAverageRating::averageRate)
                        .thenComparingLong(MovieAverageRating::boxOffice);


        movieAverageRatings.sort(comparator.reversed());

        movieAverageRatings.forEach(m -> System.out.printf("Title: %s, Rate: %.1f, Box: %d%n",
                m.title(), m.averageRate(), m.boxOffice()));
    }
}
