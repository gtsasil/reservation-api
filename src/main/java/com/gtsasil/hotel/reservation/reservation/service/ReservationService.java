package com.gtsasil.hotel.reservation.reservation.service;


import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.gtsasil.hotel.reservation.hotel.domain.Hotel;
import com.gtsasil.hotel.reservation.hotel.domain.Room;
import com.gtsasil.hotel.reservation.hotel.repository.HotelRepository;
import com.gtsasil.hotel.reservation.reservation.api.dto.ReservationRequest;
import com.gtsasil.hotel.reservation.reservation.domain.Reservation;
import com.gtsasil.hotel.reservation.reservation.domain.RoomAvailability;
import com.gtsasil.hotel.reservation.reservation.repository.ReservationRepository;
import com.gtsasil.hotel.reservation.reservation.repository.RoomAvailabilityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RoomAvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;

    /**
     * THE CORE OF THE SYSTEM
     * @Transactional is mandatory here. The Pessimistic Lock only lasts
     * while the transaction is open. As soon as the method ends, the database unlocks.
     * @CacheEvict clears the "hotels" cache in Redis whenever a new reservation is successfully created,
     * ensuring the next hotel listing fetches fresh data from the database
     */
    @SuppressWarnings("null") // Suppress warnings related to effectively final variables in the loop
    @CacheEvict(cacheNames = "hotels", allEntries = true) // Invalidate hotel cache after reservation changes availability
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long createReservation(ReservationRequest request) {
        
        // 1. Validate dates (Check-out > Check-in)
        if (!request.checkOut().isAfter(request.checkIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 2. Critical Loop: Lock day by day
        LocalDate currentDate = request.checkIn();
        
        while (currentDate.isBefore(request.checkOut())) {
            
            // FIX for "Effectively Final": 
            // We create a final copy of the variable to be used inside the lambda expression below.
            final LocalDate targetDate = currentDate;

            // LOCK FETCH (SELECT ... FOR UPDATE)
            // If the record doesn't exist, we assume the hotel is closed or has no inventory set up.
            RoomAvailability availability = availabilityRepository.findByHotelIdAndRoomTypeAndDateLocked(
                    request.hotelId(),
                    request.roomType(),
                    targetDate // <--- Using the final copy here
            ).orElseThrow(() -> new IllegalStateException("Hotel closed or no inventory for date: " + targetDate));

            // 3. Decrement Availability
            availability.decrementAvailability();
            
            // 4. Save inventory update
            availabilityRepository.save(availability);

            // Move to next day
            currentDate = currentDate.plusDays(1);
        }

        // 5. If reached here, successfully locked and decremented ALL days.
        // Now we can create the reservation receipt.
        long days = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
                
        BigDecimal roomPrice = hotel.getRooms().stream()
                .filter(room -> room.getType().equals(request.roomType()))
                .findFirst()
                .map(Room::getBasePrice)
                .orElseThrow(() -> new IllegalArgumentException("Room type not found for this hotel"));

        BigDecimal calculatedTotalPrice = roomPrice.multiply(BigDecimal.valueOf(days));

        // Now we can create the reservation receipt.
        Reservation reservation = Reservation.builder()
                .hotelId(request.hotelId())
                .userEmail(request.email())
                .roomType(request.roomType())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .status("CONFIRMED")
                .totalPrice(calculatedTotalPrice)
                .build();

        return reservationRepository.save(reservation).getId();
    }
}