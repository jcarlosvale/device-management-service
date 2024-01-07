package com.assessment.device.view.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

}
