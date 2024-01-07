package com.assessment.device.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    private UUID id;

    private String name;

    private String brand;

    private ZonedDateTime createdAt;
}
