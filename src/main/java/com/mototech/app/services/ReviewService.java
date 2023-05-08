package com.mototech.app.services;

import com.mototech.app.models.Review;

import java.util.List;

public interface ReviewService {

    Review persistReview(Review review);

    List<Review> getReviewsForBook(Long bookId);

    Double getAverageRatingForBook(Long bookId);

}
