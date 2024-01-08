package com.assessment.device.view.rs;

import com.assessment.device.domain.model.Device;
import com.assessment.device.domain.repository.DeviceRepository;
import com.assessment.device.view.dto.DeviceRequestDto;
import com.assessment.device.view.dto.DeviceResponseDto;
import com.assessment.device.view.dto.ErrorDto;
import com.assessment.device.view.dto.PartialDeviceDto;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DeviceRepository repository;

    private String URL;

    @BeforeEach
    void setup() {
        URL = "http://localhost:" + port + "/v1/devices";

        final var entities = List.of(
                new Device("name 1", "brand 1"),
                new Device("name 2", "brand 2"),
                new Device("name 3", "brand 1")
        );

        repository.saveAll(entities);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
    }

    @Test
    void getAll() {
        //GIVEN
        final var expectedSize = repository.count();

        //WHEN
        final var response = restTemplate.getForEntity(URL, DeviceResponseDto[].class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody())).hasSize((int) expectedSize);
    }

    @Test
    void getById() {
        //GIVEN
        final var entity = repository.findAll().get(0);
        final var id = entity.getId();
        final var expected = DeviceResponseDto.builder()
                .id(id)
                .name(entity.getName())
                .brand(entity.getBrand())
                .createdAt(entity.getCreatedAt())
                .build();

        //WHEN
        final var response = restTemplate.getForEntity(URL + "/{id}", DeviceResponseDto.class, id);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expected);
    }

    @Test
    void getByIdNotFound() {
        //GIVEN

        //WHEN
        final var response = restTemplate.getForEntity(URL + "/{id}", DeviceResponseDto.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByIdInvalidIdShouldFail() {
        //GIVEN
        final var id = "1";

        //WHEN
        final var response = restTemplate.getForEntity(URL + "/{id}", ErrorDto.class, id);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @Test
    void getByBrand() {
        //GIVEN
        final var brand = "brand 1";
        final var expectedSize = 2;

        //WHEN
        final var response =
                restTemplate.getForEntity(URL + "?brand={brand}", DeviceResponseDto[].class, Map.of("brand", brand));

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var devices = response.getBody();
        assertThat(Objects.requireNonNull(devices)).hasSize(expectedSize);
        Arrays.stream(devices).forEach(deviceResponseDto -> assertThat(deviceResponseDto.getBrand()).isEqualTo(brand));
    }

    @Test
    void save() {
        //GIVEN
        final var previousCount = repository.count();
        final var requestNewDto = DeviceRequestDto.builder()
                .name("new name")
                .brand("new brand")
                .build();

        //WHEN
        final var response = restTemplate.postForEntity(URL, requestNewDto, DeviceResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.getId()).isNotNull();
        assertThat(body.getCreatedAt()).isNotNull();
        assertThat(body.getName()).isEqualTo("new name");
        assertThat(body.getBrand()).isEqualTo("new brand");

        // verify database
        assertThat(repository.count()).isEqualTo(previousCount + 1);
        final var entity = repository.findById(body.getId()).orElseThrow();
        assertThat(entity.getName()).isEqualTo("new name");
        assertThat(entity.getBrand()).isEqualTo("new brand");
    }

    @ParameterizedTest
    @MethodSource("invalidDeviceRequestDtoArguments")
    void saveShouldFail(final DeviceRequestDto request, final int expectedHttpCode) {
        //GIVEN

        //WHEN
        final var response = restTemplate.postForEntity(URL, request, ErrorDto.class);

        //THEN
        assertThat(response.getStatusCode().value()).isEqualTo(expectedHttpCode);

        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @Test
    void update() {
        //GIVEN
        final var entity = repository.findAll().get(0);
        final var previousCount = repository.count();
        final var request = DeviceRequestDto.builder()
                .name("new name")
                .brand("new brand")
                .build();

        final var expectedResponse = DeviceResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brand(entity.getBrand())
                .createdAt(entity.getCreatedAt())
                .build();

        expectedResponse.setName("new name");
        expectedResponse.setBrand("new brand");

        //WHEN
        final var response = restTemplate.exchange(URL + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(request), DeviceResponseDto.class, entity.getId());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        assertThat(repository.count()).isEqualTo(previousCount);
        final var entityUpdated = repository.findById(entity.getId()).orElseThrow();
        assertThat(entityUpdated.getName()).isEqualTo("new name");
        assertThat(entityUpdated.getBrand()).isEqualTo("new brand");
    }

    @Test
    void updateNotFound() {
        //GIVEN
        final var previousCount = repository.count();
        final var request = DeviceRequestDto.builder()
                .name("new name")
                .brand("new brand")
                .build();

        //WHEN
        final var response = restTemplate.exchange(URL + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(request), Void.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(repository.count()).isEqualTo(previousCount);
    }

    @Test
    void updateInvalidIdShouldFail() {
        //GIVEN
        final var request = DeviceRequestDto.builder()
                .name("new name")
                .brand("new brand")
                .build();

        //WHEN
        final var response = restTemplate.exchange(URL + "/1", HttpMethod.PUT,
                new HttpEntity<>(request), ErrorDto.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("invalidDeviceRequestDtoArguments")
    void updateShouldFail(final DeviceRequestDto request, final int expectedHttpCode) {
        //GIVEN

        //WHEN
        final var response = restTemplate.exchange(URL + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(request), ErrorDto.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode().value()).isEqualTo(expectedHttpCode);

        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @Test
    void partialUpdate() {
        //GIVEN
        final var patchRestTemplate = getPatchRestTemplate();

        final var entity = repository.findAll().get(0);
        final var previousCount = repository.count();
        final var request = PartialDeviceDto.builder()
                .brand("new brand")
                .build();

        final var expectedResponse = DeviceResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brand(entity.getBrand())
                .createdAt(entity.getCreatedAt())
                .build();

        expectedResponse.setName(entity.getName());
        expectedResponse.setBrand("new brand");

        //WHEN
        final var response = patchRestTemplate.exchange(URL + "/{id}", HttpMethod.PATCH,
                new HttpEntity<>(request), DeviceResponseDto.class, entity.getId());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        assertThat(repository.count()).isEqualTo(previousCount);
        final var entityUpdated = repository.findById(entity.getId()).orElseThrow();
        assertThat(entityUpdated.getBrand()).isEqualTo("new brand");
    }

    @Test
    void partialUpdateNotFound() {
        //GIVEN
        final var patchRestTemplate = getPatchRestTemplate();

        final var previousCount = repository.count();
        final var request = PartialDeviceDto.builder()
                .brand("new brand")
                .build();

        //WHEN
        final var response = patchRestTemplate.exchange(URL + "/{id}", HttpMethod.PATCH,
                new HttpEntity<>(request), Void.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(repository.count()).isEqualTo(previousCount);
    }

    @Test
    void partialUpdateInvalidIdShouldFail() {
        //GIVEN
        final var patchRestTemplate = getPatchRestTemplate();

        final var request = PartialDeviceDto.builder()
                .brand("new brand")
                .build();

        //WHEN
        final var response = patchRestTemplate.exchange(URL + "/1", HttpMethod.PATCH,
                new HttpEntity<>(request), ErrorDto.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @Test
    void partialUpdateShouldFail() {
        //GIVEN
        final var patchRestTemplate = getPatchRestTemplate();

        final var request = PartialDeviceDto.builder()
                .brand("")
                .build();

        //WHEN
        final var response = patchRestTemplate.exchange(URL + "/{id}", HttpMethod.PATCH,
                new HttpEntity<>(request), ErrorDto.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThat(Objects.requireNonNull(response.getBody()).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotBlank();
    }

    @Test
    void delete() {
        //GIVEN
        final var entity = repository.findAll().get(0);
        final var id = entity.getId();

        //WHEN
        final var response = restTemplate.exchange(URL + "/{id}", HttpMethod.DELETE, null, Void.class, id);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void delete_NotFound() {
        //GIVEN

        //WHEN
        final var response =
                restTemplate.exchange(URL + "/{id}", HttpMethod.DELETE, null, Void.class, UUID.randomUUID());

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private static Stream<Arguments> invalidDeviceRequestDtoArguments() {
        return Stream.of(
                Arguments.of(
                        DeviceRequestDto.builder().name("").brand("brand").build(), HttpStatus.BAD_REQUEST.value(),
                        DeviceRequestDto.builder().name("name").brand("").build(), HttpStatus.BAD_REQUEST.value(),
                        DeviceRequestDto.builder().name("").brand("").build(), HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    private RestTemplate getPatchRestTemplate() {
        final HttpClient httpClient = HttpClientBuilder.create().build();
        final RestTemplate patchRestTemplate = restTemplate.getRestTemplate();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return patchRestTemplate;
    }
}