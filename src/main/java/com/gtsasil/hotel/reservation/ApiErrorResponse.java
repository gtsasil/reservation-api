package com.gtsasil.hotel.reservation;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timeStamp, int status, String error, String message) {
    
}
