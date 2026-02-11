package com.gtsasil.hotel.reservation.hotel.service;
import com.gtsasil.hotel.reservation.hotel.api.dto.HotelResponse;
import com.gtsasil.hotel.reservation.hotel.api.dto.RoomResponse;
import com.gtsasil.hotel.reservation.hotel.domain.Hotel;
import com.gtsasil.hotel.reservation.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
 private final HotelRepository hotelRepository;


/**
     * Fetches hotels using @EntityGraph defined in the Repository.
     * Caching: @Cacheable saves the result in Redis. The next calls won't hit the database.
     * Transactional(readOnly = true) improves Hibernate performance (prevents dirty checking).
     */
    @Cacheable(cacheNames = "hotels")
    @Transactional(readOnly = true)
    public List<HotelResponse> findAllHotels() {
        // 1. Fetch from database (A single query with LEFT JOIN FETCH)
        List<Hotel> hotels = hotelRepository.findAll();

        // 2. Convert to DTO (In-memory)
        return hotels.stream()
                .map(this::toHotelResponse)
                .toList();
    }

    // Manual mapper (Simple and efficient for this scope)
    private HotelResponse toHotelResponse(Hotel hotel) {
        List<RoomResponse> roomResponses = hotel.getRooms().stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        room.getBasePrice()))
                .toList();

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getCity(),
                roomResponses
        );
    }
}