package com.gtsasil.hotel.reservation.hotel.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String type; // STANDARD, DELUXE, SUITE

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    // FetchType.LAZY is mandatory!
    // If you leave the default (EAGER), when searching for a Room, JPA would retrieve the entire Hotel.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}
