package com.projects.streamer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName;

    private String fileType;

    @Column(nullable = false, updatable = false, columnDefinition = "timestamp(6) without time zone")
    private Instant createdAt = Instant.now();

    @Column(nullable = false, columnDefinition = "timestamp(6) without time zone")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
