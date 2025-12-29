package com.booking.movieticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "screens",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "theatre_id"})
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer  totalRows;

    @Column(nullable = false)
    private Integer totalColumns;

    @Column(nullable = false)
    private Integer capacity;   // rows * col

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;


    @Column(nullable = false)
    private Boolean active= true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    private void calculateCapacity(){
        this.capacity=this.totalRows * this.totalColumns;
    }
}
