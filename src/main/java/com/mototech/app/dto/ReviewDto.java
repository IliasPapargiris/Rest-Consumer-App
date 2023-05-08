package com.mototech.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDto {
    @NotNull(message = "Title is mandatory")
    private Long bookingId;
    @Min(0)
    @Max(5)
    @NotNull(message = "Rating cannot be null")
    private Integer rating;
    @NotBlank(message = "Review text is mandatory")
    private String review;

}