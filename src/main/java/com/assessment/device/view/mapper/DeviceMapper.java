package com.assessment.device.view.mapper;

import com.assessment.device.domain.model.Device;
import com.assessment.device.view.dto.DeviceRequestDto;
import com.assessment.device.view.dto.DeviceResponseDto;
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

        return new Device(deviceDto.getName(), deviceDto.getBrand());
    }
}
