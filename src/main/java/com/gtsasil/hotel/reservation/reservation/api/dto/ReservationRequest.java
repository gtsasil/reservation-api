package com.gtsasil.hotel.reservation.reservation.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record ReservationRequest(
    @NotNull(message = "Hotel ID is Mandatory")
    Long hotelId,

    @NotNull(message = "Room type is mandatory")
    String roomType,

    @Email(message = "Invalid email")
    String email,

    @NotNull @Future(message = "Check-in must be in the future")
    LocalDate checkIn,

    @NotNull @Future(message = "Check-out must be in the future")
    LocalDate checkOut
) {}