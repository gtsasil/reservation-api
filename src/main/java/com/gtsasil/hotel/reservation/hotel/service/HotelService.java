package com.gtsasil.hotel.reservation.hotel.service;
import com.gtsasil.hotel.reservation.hotel.api.dto.HotelResponse;
import com.gtsasil.hotel.reservation.hotel.api.dto.RoomResponse;
import com.gtsasil.hotel.reservation.hotel.domain.Hotel;
import com.gtsasil.hotel.reservation.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
 private final HotelRepository hotelRepository;

    /**
     * Busca hotéis usando @EntityGraph definido no Repository.
     * Transactional(readOnly = true) ajuda na performance do Hibernate (evita dirty checking).
     */
    @Transactional(readOnly = true)
    public List<HotelResponse> findAllHotels() {
        // 1. Busca no banco (Uma única query gorda com JOIN)
        List<Hotel> hotels = hotelRepository.findAll();

        // 2. Converte para DTO (Memória)
        return hotels.stream()
                .map(this::toHotelResponse)
                .toList();
    }

    // Mapper manual (Simples e eficiente para este escopo)
    private HotelResponse toHotelResponse(Hotel hotel) {
        List<RoomResponse> roomResponses = hotel.getRooms().stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        room.getBasePrice()))
                .toList();

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getCity(),
                roomResponses
        );
    }
}