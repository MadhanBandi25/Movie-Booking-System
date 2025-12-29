package com.booking.movieticket.entity;

import com.booking.movieticket.entity.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title","language"})
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, length =200)
    private String language;

    @Column(nullable = false, length = 200)
    private String  genre;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(length = 200)
    private String  director;

    @Column(length = 500)
    private String cast;

    private String posterUrl;
    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieStatus status;
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
