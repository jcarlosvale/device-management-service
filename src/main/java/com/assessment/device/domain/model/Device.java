package com.assessment.device.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
public class Device {

    @Id
    private UUID id;

    @NotBlank(message = "name must not be blank")
    @Column(nullable = false)
    @Setter
    private String name;

    @NotBlank(message = "brand must not be blank")
    @Column(nullable = false)
    @Setter
    private String brand;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    protected Device() {

    }

    public Device(final String name, final String brand) {
        this.name = name;
        this.brand = brand;
    }

    @PrePersist
    void onCreate() {
        id = UUID.randomUUID();
        createdAt = ZonedDateTime.now();
    }
}
