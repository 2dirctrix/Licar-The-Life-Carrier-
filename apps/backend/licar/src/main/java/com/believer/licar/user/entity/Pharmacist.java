package com.believer.licar.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pharmacists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pharmacist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacist_id")
    private int pharmacistId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
