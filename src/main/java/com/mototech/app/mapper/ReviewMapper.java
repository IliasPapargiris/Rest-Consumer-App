package com.mototech.app.mapper;


import com.mototech.app.dto.ReviewDto;
import com.mototech.app.models.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setBookingId(review.getBookId());
        reviewDto.setRating(review.getRating());
        reviewDto.setReview(review.getReviewText());
        return reviewDto;
    }

    public Review toEntity(ReviewDto reviewDto) {
        Review review = new Review();
        review.setBookId(reviewDto.getBookingId());
        review.setRating(reviewDto.getRating());
        review.setReviewText(reviewDto.getReview());
        return review;
    }
}