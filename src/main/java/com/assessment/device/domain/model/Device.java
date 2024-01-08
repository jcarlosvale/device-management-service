package com.assessment.device.domain.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
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



    public Device(final String name, final String brand) {
        this.name = name;
        this.brand = brand;
    }

    public Device() {

    }

    @PrePersist
    void onCreate() {
        id = UUID.randomUUID();
        createdAt = ZonedDateTime.now();
    }
}
