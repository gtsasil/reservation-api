package com.gtsasil.hotel.reservation.reservation.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "room_availability")
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "available_count", nullable = false)
    private Integer availableCount;

    // Method to decrement availability when a reservation is made
    public void decrementAvailability() {
        if (this.availableCount <= 0) {
            throw new IllegalStateException("Not available for date " + this.date);
        }
        this.availableCount--;
    }
}
