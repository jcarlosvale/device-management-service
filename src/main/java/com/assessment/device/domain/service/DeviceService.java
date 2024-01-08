package com.assessment.device.domain.service;

import com.assessment.device.domain.model.Device;
import com.assessment.device.domain.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository repository;

    public List<Device> findAll() {
        return repository.findAll();
    }

    public Device save(@Valid final Device device) {
        Objects.requireNonNull(device, "device must not be null");
        return repository.save(device);
    }

    public Optional<Device> findById(final UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id);
    }

    public List<Device> findByBrandName(final String brand) {
        Objects.requireNonNull(brand, "brand must not be null");
        return repository.findDevicesByBrand(brand);
    }

    public Optional<Device> update(final UUID id, @Valid final Device deviceValue) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(deviceValue, "deviceValue must not be null");

        return repository
                .findById(id)
                .map(device -> {
                    device.setName(deviceValue.getName());
                    device.setBrand(deviceValue.getBrand());
                    return repository.save(device);
                });
    }

    public Optional<Device> updateBrand(final UUID id, final String brand) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(brand, "deviceValue must not be null");

        return repository
                .findById(id)
                .map(device -> {
                    device.setBrand(brand);
                    return repository.save(device);
                });
    }

    public void deleteById(final UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        repository.deleteById(id);
    }
}
