package com.gtsasil.hotel.reservation;

import com.gtsasil.hotel.reservation.reservation.api.dto.ReservationRequest;
import com.gtsasil.hotel.reservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class ReservationApiApplicationTests {
// 1. Spinups a real, disposable PostgreSQL database via Docker for this test
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // 2. Overrides the application.yml database URL to point to this temporary Testcontainer
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ReservationService reservationService;

    @Test
    void shouldPreventDoubleBookingWhenMultipleUsersTryToBookSimultaneously() throws InterruptedException {
        // Arrange
        int totalConcurrentUsers = 10;
        int expectedSuccessfulBookings = 5; // Because our DataSeeder creates exactly 5 DELUXE rooms per day
        int expectedFailedBookings = 5;

        ExecutorService executor = Executors.newFixedThreadPool(totalConcurrentUsers);
        
        // Latches to synchronize threads (Start them all at the exact same time)
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalConcurrentUsers);

        AtomicInteger successfulReservations = new AtomicInteger(0);
        AtomicInteger failedReservations = new AtomicInteger(0);

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(15);

        // Act - Prepare 10 concurrent requests
        for (int i = 0; i < totalConcurrentUsers; i++) {
            String userEmail = "user" + i + "@test.com";
            
            executor.submit(() -> {
                try {
                    startLatch.await(); // All threads wait here until the latch is released
                    
                    ReservationRequest request = new ReservationRequest(
                            1L, "DELUXE", userEmail, checkIn, checkOut);
                    
                    reservationService.createReservation(request);
                    successfulReservations.incrementAndGet(); // If no exception, it was a success
                    
                } catch (Exception e) {
                    failedReservations.incrementAndGet(); // If out of stock, it falls here
                } finally {
                    doneLatch.countDown(); // Signals this thread is done
                }
            });
        }

        // Release the hounds! Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all 10 threads to finish processing
        doneLatch.await();
        executor.shutdown();

        // Assert
        System.out.println("✅ Successful Reservations: " + successfulReservations.get());
        System.out.println("❌ Failed Reservations (Out of Stock): " + failedReservations.get());

        assertEquals(expectedSuccessfulBookings, successfulReservations.get(), 
            "Exactly 5 reservations should succeed due to inventory limits.");
            
        assertEquals(expectedFailedBookings, failedReservations.get(), 
            "Exactly 5 reservations should fail due to Pessimistic Lock preventing Double Booking.");
    }
}