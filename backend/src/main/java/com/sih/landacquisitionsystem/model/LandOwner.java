package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "land_owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;
    private String email;

    @Column(name = "state")
    private String state;

    @Column(name = "district")
    private String district;

    private String village;

}