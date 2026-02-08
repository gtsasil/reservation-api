package com.gtsasil.hotel.reservation.hotel.api.controller;


import com.gtsasil.hotel.reservation.hotel.api.dto.HotelResponse;
import com.gtsasil.hotel.reservation.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
 private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelResponse>> listAll() {
        return ResponseEntity.ok(hotelService.findAllHotels());
    }
}