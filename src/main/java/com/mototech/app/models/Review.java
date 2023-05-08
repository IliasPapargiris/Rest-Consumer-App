package com.mototech.app.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long bookId;


    @Column(nullable = false)
    private Integer rating;


    @Column(name = "review_text", nullable = false, length = 1000)
    private String reviewText;

}
