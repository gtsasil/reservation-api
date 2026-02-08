package com.gtsasil.hotel.reservation.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gtsasil.hotel.reservation.reservation.domain.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Agora o .save(), .findById(), etc. existem magicamente!
}