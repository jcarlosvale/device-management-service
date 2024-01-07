package com.assessment.device.view.mapper;

import com.assessment.device.domain.model.Device;
import com.assessment.device.view.dto.DeviceRequestDto;
import com.assessment.device.view.dto.DeviceResponseDto;
import com.assessment.device.view.dto.PartialDeviceDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DeviceMapper {
    public DeviceResponseDto toDto(final Device device) {
        Objects.requireNonNull(device, "device must be not null");

        return DeviceResponseDto.builder()
                .id(device.getId())
                .name(device.getName())
                .brand(device.getBrand())
                .createdAt(device.getCreatedAt())
                .build();
    }

    public Device toModel(final DeviceRequestDto deviceDto) {
        Objects.requireNonNull(deviceDto, "deviceDto must be not null");

        return Device.builder()
                .name(deviceDto.getName())
                .brand(deviceDto.getBrand())
                .build();
    }

    public Device toModel(final PartialDeviceDto partialDeviceDto) {
        Objects.requireNonNull(partialDeviceDto, "partialDeviceDto must be not null");

        return Device.builder()
                .brand(partialDeviceDto.getBrand())
                .build();
    }
}
