package com.backbase.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "best_picture_winner")
public class MovieAward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String title;

    public MovieAward(long id) {
        this.id = id;
    }

    public MovieAward(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
