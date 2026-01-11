package com.formation.task.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UUID public, utilisé côté client pour rejoindre
    @Column(unique = true, nullable = false)
    private String publicId;

    // Optional name
    private String name;

    // association optionnelle avec une Task
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Instant createdAt = Instant.now();

    private boolean persistent = false; // si true, conserve en base
}
