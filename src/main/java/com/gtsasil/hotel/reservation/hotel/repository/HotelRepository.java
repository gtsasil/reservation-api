package com.gtsasil.hotel.reservation.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gtsasil.hotel.reservation.hotel.domain.Hotel;
import org.springframework.lang.NonNull;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    // 🚀 The trick: @EntityGraph
    // The problem: By default, “rooms” is LAZY. Listing hotels would make 1 query for hotels + N queries for rooms.
    // The solution: attributePaths = “rooms” forces JPA to do a LEFT JOIN FETCH in a single query.
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"rooms"})
    List<Hotel> findAll();
    
}
