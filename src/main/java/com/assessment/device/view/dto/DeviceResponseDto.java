package com.assessment.device.view.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDto {

    @JsonFormat(shape = STRING)
    private UUID id;

    private String name;

    private String brand;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss Z")
    private ZonedDateTime createdAt;
}
