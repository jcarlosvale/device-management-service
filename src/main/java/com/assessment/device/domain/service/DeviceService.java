package com.assessment.device.domain.service;

import com.assessment.device.domain.model.Device;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    public List<Device> findAll() {
        return List.of(
                simpleDeviceBuilder()
        );
    }

    public Device save(final Device device) {
        Objects.requireNonNull(device, "device must not be null");
        return simpleDeviceBuilder();
    }

    public Optional<Device> findById(final String id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.of(simpleDeviceBuilder());
    }

    public List<Device> findByBrandName(final String brandName) {
        Objects.requireNonNull(brandName, "brandName must not be null");
        return List.of(
                simpleDeviceBuilder()
        );
    }

    public Optional<Device> update(final String id, final Device device) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(device, "device must not be null");
        return Optional.of(simpleDeviceBuilder());
    }

    public void deleteById(final String id) {
        Objects.requireNonNull(id, "id must not be null");
    }

    private static Device simpleDeviceBuilder() {
        return Device.builder()
                .id(UUID.randomUUID())
                .name("device name")
                .brand("device brand")
                .createdAt(ZonedDateTime.now())
                .build();
    }
}
