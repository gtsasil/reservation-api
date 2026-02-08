package com.gtsasil.hotel.reservation.hotel.api.dto;

import java.util.List;
public record HotelResponse(Long id, String name, String address, String city, List<RoomResponse> rooms) {

}
