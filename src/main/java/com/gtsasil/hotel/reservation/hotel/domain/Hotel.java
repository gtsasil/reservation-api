package com.gtsasil.hotel.reservation.hotel.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;
    
    // Translate the comments to english  
    // FetchType.LAZY is not used here because usually when fetching a Hotel, we want its Rooms too.
    // cascade = CascadeType.ALL: Any operation on Hotel cascades to its Rooms.
    // mappedBy = “hotel”: Indicates that the “Room” side (foreign key there) controls the relationship.
    // orphanRemoval = true: If we remove a room from the Java list, JPA deletes it from the database.
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

}