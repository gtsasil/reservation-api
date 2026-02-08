package com.gtsasil.hotel.reservation.reservation.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gtsasil.hotel.reservation.reservation.domain.RoomAvailability;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    // 🔒 PESSIMISTIC_WRITE:
    //This generates a SQL "SELECT ... FOR UPDATE".
    // It means that when a transaction reads this row, NO ONE else can read or write to it.
    // Other transactions will wait in line until this one finishes. This is crucial to prevent
    // two users from booking the last available room at the same time, which could lead to overbooking.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RoomAvailability r WHERE r.hotelId = :hotelId AND r.roomType = :roomType AND r.date = :date")
    Optional<RoomAvailability> findByHotelIdAndRoomTypeAndDateLocked(
            @Param("hotelId") Long hotelId,
            @Param("roomType") String roomType,
            @Param("date") LocalDate date
    );
}