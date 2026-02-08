package com.gtsasil.hotel.reservation.reservation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.gtsasil.hotel.reservation.reservation.api.dto.ReservationRequest;
import com.gtsasil.hotel.reservation.reservation.domain.Reservation;
import com.gtsasil.hotel.reservation.reservation.domain.RoomAvailability;
import com.gtsasil.hotel.reservation.reservation.repository.ReservationRepository;
import com.gtsasil.hotel.reservation.reservation.repository.RoomAvailabilityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RoomAvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;

    /**
     * THE CORE OF THE SYSTEM
     * @Transactional is mandatory here. The Pessimistic Lock only lasts
     * while the transaction is open. As soon as the method ends, the database unlocks.
     */
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
        Reservation reservation = Reservation.builder()
                .hotelId(request.hotelId())
                .userEmail(request.email())
                .roomType(request.roomType())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .status("CONFIRMED")
                .totalPrice(BigDecimal.ZERO) // TODO: Calculate real price by fetching from Hotel entity
                .build();

        return reservationRepository.save(reservation).getId();
    }
}