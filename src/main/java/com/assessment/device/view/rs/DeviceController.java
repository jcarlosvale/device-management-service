package com.assessment.device.view.rs;

import com.assessment.device.domain.service.DeviceService;
import com.assessment.device.view.dto.DeviceRequestDto;
import com.assessment.device.view.dto.DeviceResponseDto;
import com.assessment.device.view.dto.PartialDeviceDto;
import com.assessment.device.view.mapper.DeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/devices")
public class DeviceController {

    private final DeviceMapper mapper;

    private final DeviceService service;

    @PostMapping
    public ResponseEntity<DeviceResponseDto> save(@Valid @RequestBody final DeviceRequestDto request) {
        log.info("Add device");
        final var deviceToSave = mapper.toModel(request);
        final var entity = service.save(deviceToSave);
        final var dto = mapper.toDto(entity);

        final var uri =
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/{id}")
                        .buildAndExpand(dto.getId())
                        .toUriString();

        final var location = URI.create(uri);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> findById(@PathVariable final UUID id) {
        log.info("Get device by id {}", id);
        final var entityOptional = service.findById(id);
        return entityOptional
                .map(device -> ResponseEntity.ok(mapper.toDto(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> update(@PathVariable final UUID id,
                                                    @Valid @RequestBody final DeviceRequestDto request) {
        log.info("Update device id {}", id);
        final var deviceValue = mapper.toModel(request);
        final var entityOptional = service.update(id, deviceValue);
        return entityOptional
                .map(device -> ResponseEntity.ok(mapper.toDto(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updatePartially(@PathVariable final UUID id,
                                                             @Valid @RequestBody final PartialDeviceDto request) {
        log.info("Update device partially id {}", id);
        final var entityOptional = service.updateBrand(id, request.getBrand());
        return entityOptional
                .map(device -> ResponseEntity.ok(mapper.toDto(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID id) {
        log.info("Delete device by id {}", id);
        service.deleteById(id);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getDevices(
            @RequestParam(value = "brand", required = false) final String brand) {
        return brand != null ? findByBrand(brand) : findAll();
    }

    private ResponseEntity<List<DeviceResponseDto>> findAll() {
        log.info("Listing all devices");
        final var entities = service.findAll();
        final var response = entities.stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<List<DeviceResponseDto>> findByBrand(final String brandName) {
        log.info("Listing devices by brand {}", brandName);
        final var entities = service.findByBrandName(brandName);
        final var response = entities.stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(response);
    }
}
