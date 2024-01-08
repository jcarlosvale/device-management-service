package com.assessment.device.view.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartialDeviceDto {

    @NotBlank(message = "brand must not be blank")
    private String brand;
}
