package com.believer.licar.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nurses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nurse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nurse_id")
    private int nurseId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "department", length = 50)
    private String department;
}
