package com.gtsasil.hotel.reservation.reservation.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record ReservationRequest(
    @NotNull(message = "Hotel ID é obrigatório")
    Long hotelId,

    @NotNull(message = "Tipo do quarto é obrigatório")
    String roomType,

    @Email(message = "Email inválido")
    String email,

    @NotNull @Future(message = "Check-in deve ser no futuro")
    LocalDate checkIn,

    @NotNull @Future(message = "Check-out deve ser no futuro")
    LocalDate checkOut
) {}