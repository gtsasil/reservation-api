package com.gtsasil.hotel.reservation.config;

import com.gtsasil.hotel.reservation.hotel.domain.Hotel;
import com.gtsasil.hotel.reservation.hotel.domain.Room;
import com.gtsasil.hotel.reservation.hotel.repository.HotelRepository;
import com.gtsasil.hotel.reservation.reservation.domain.RoomAvailability;
import com.gtsasil.hotel.reservation.reservation.repository.RoomAvailabilityRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Profile("!test") // Do not run during unit tests
    CommandLineRunner initDatabase(HotelRepository hotelRepository, 
                                   RoomAvailabilityRepository availabilityRepository) {
        return args -> {
            // Check if data already exists to prevent duplication
            if (hotelRepository.count() > 0) {
                System.out.println("🌱 Database already seeded. Skipping...");
                return;
            }

            System.out.println("🌱 Seeding database...");

            // 1. Create Hotel
            Hotel copacabanaPalace = Hotel.builder()
                    .name("Copacabana Palace")
                    .address("Av. Atlântica, 1702")
                    .city("Rio de Janeiro")
                    .build();

            // 2. Create Rooms
            Room room101 = Room.builder()
                    .roomNumber("101")
                    .type("DELUXE")
                    .basePrice(new BigDecimal("1500.00"))
                    .hotel(copacabanaPalace)
                    .build();

            Room room102 = Room.builder()
                    .roomNumber("102")
                    .type("STANDARD")
                    .basePrice(new BigDecimal("800.00"))
                    .hotel(copacabanaPalace)
                    .build();

            copacabanaPalace.getRooms().add(room101);
            copacabanaPalace.getRooms().add(room102);

            // Save Hotel (Cascades to Rooms) - We need the ID generated here for the availability
            Hotel savedHotel = hotelRepository.save(copacabanaPalace);

            // 3. Create Availability for the next 30 days
            List<RoomAvailability> availabilityList = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (int i = 0; i < 30; i++) {
                LocalDate date = today.plusDays(i);

                // Inventory for DELUXE rooms
                availabilityList.add(RoomAvailability.builder()
                        .hotelId(savedHotel.getId())
                        .roomType("DELUXE")
                        .date(date)
                        .availableCount(5) // 5 rooms available per day
                        .build());

                // Inventory for STANDARD rooms
                availabilityList.add(RoomAvailability.builder()
                        .hotelId(savedHotel.getId())
                        .roomType("STANDARD")
                        .date(date)
                        .availableCount(10) // 10 rooms available per day
                        .build());
            }

            availabilityRepository.saveAll(availabilityList);

            System.out.println("🌱 Database seeded with Hotels and 30 days of Availability!");
        };
    }
}