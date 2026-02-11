package com.gtsasil.hotel.reservation.hotel.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record RoomResponse(Long id, String roomNumber, String type, BigDecimal price) implements Serializable {
    
}
