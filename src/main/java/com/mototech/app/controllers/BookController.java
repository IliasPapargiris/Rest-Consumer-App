package com.mototech.app.controllers;

import com.mototech.app.dto.BookDto;
import com.mototech.app.dto.GutendexResponse;
import com.mototech.app.dto.ReviewDto;
import com.mototech.app.exceptions.BookNotFoundException;
import com.mototech.app.exceptions.ResponseErrorException;
import com.mototech.app.mapper.ReviewMapper;
import com.mototech.app.models.Review;
import com.mototech.app.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final RestTemplate restTemplate;

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;


    private final static String GUTENDEX_API_URL_SEARCH_TERM = "https://gutendex.com/books?search={searchTerm}";

    private final static String GUTENDEX_API_URL_BOOK_ID = "https://gutendex.com/books/{bookId}";

    @GetMapping("/{searchTerm}")
    public ResponseEntity<List<BookDto>> searchBooks(@PathVariable String searchTerm) throws BookNotFoundException, ResponseErrorException {
        try {
            ResponseEntity<GutendexResponse> responseEntity = restTemplate.getForEntity(
                    GUTENDEX_API_URL_SEARCH_TERM, GutendexResponse.class, searchTerm);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                List<BookDto> bookDtos = Objects.requireNonNull(responseEntity.getBody()).getResults()
                        .stream()
                        .filter(bookDto -> bookDto.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList());

                if (bookDtos.isEmpty()) {
                    throw new BookNotFoundException("Book with title, author, or subject containing " + searchTerm + " not found.");
                }

                return ResponseEntity.ok(bookDtos);
            } else {
                throw new ResponseErrorException("Failed to get books from Gutendex API.");
            }

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BookNotFoundException("Book with title, author, or subject containing " + searchTerm + " not found.");
            } else {
                throw new ResponseErrorException("Failed to get books from Gutendex API.");
            }
        } catch (ResourceAccessException ex) {
            throw new ResponseErrorException("Failed to connect to Gutendex API.");
        }
    }

    @PostMapping("review/{bookId}")
    public ResponseEntity<Object> reviewBook(@PathVariable Long bookId, @RequestBody @Valid ReviewDto reviewDto, BindingResult ignoredBindingResult) throws BookNotFoundException, ResponseErrorException {
        try {
            ResponseEntity<BookDto> responseEntity = restTemplate.getForEntity(
                    GUTENDEX_API_URL_BOOK_ID, BookDto.class, bookId);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Review review = reviewMapper.toEntity(reviewDto);
                review.setBookId(bookId);
                Review persistedReview = reviewService.persistReview(review);
                ReviewDto savedReviewDto = reviewMapper.toDto(persistedReview);
                return ResponseEntity.ok(savedReviewDto);
            } else {
                throw new ResponseErrorException("Failed to get book details from Gutendex API.");
            }

        } catch (
                HttpClientErrorException.NotFound ex) {
            throw new BookNotFoundException("Book with book id: " + bookId + " not found.");
        } catch (ResourceAccessException ex) {
            throw new ResponseErrorException("Failed to connect to Gutendex API.");
        }
    }

    @GetMapping("/{bookId}/details")
    public ResponseEntity<BookDto> getBookDetails(@PathVariable Long bookId) throws BookNotFoundException, ResponseErrorException {
        try {
            ResponseEntity<BookDto> responseEntity = restTemplate.getForEntity(
                    GUTENDEX_API_URL_BOOK_ID, BookDto.class, bookId);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {

                BookDto bookDto = responseEntity.getBody();

                List<Review> reviews = reviewService.getReviewsForBook(bookId);

                List<String> textReviews = reviews.stream()
                        .map(Review::getReviewText)
                        .collect(Collectors.toList());

                if (reviews.isEmpty()) {

                    assert bookDto != null;
                    bookDto.setRating(0.0);
                    bookDto.setReviews(Collections.emptyList());
                } else {

                    Double averageRating = reviewService.getAverageRatingForBook(bookId);
                    assert bookDto != null;
                    bookDto.setRating(averageRating);
                    bookDto.setReviews(textReviews);
                }

                return ResponseEntity.ok(bookDto);
            } else {
                throw new ResponseErrorException("Failed to get book details from Gutendex API.");
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new BookNotFoundException("Book with book id: " + bookId + " not found.");
        } catch (ResourceAccessException ex) {
            throw new ResponseErrorException("Failed to connect to Gutendex API.");
        }
    }
}






