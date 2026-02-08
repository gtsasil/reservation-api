package com.gtsasil.hotel.reservation.reservation.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gtsasil.hotel.reservation.reservation.api.dto.ReservationRequest;
import com.gtsasil.hotel.reservation.reservation.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> createReservation(@RequestBody @Valid ReservationRequest request) {
        // Calls the service that handles the Pessimistic Lock logic
        Long reservationId = reservationService.createReservation(request);
        
        // Returns HTTP 201 (Created) with the new Reservation ID
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationId);
    }
}