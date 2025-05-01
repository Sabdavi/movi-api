package com.backbase.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table
public class MovieRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column
    private int rate;

    @Column
    private Instant createdAt;

    public MovieRating(String title, int rate) {
        this.title = title;
        this.rate = rate;
        this.createdAt = Instant.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
