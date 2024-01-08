# Device Management Service

## Running the Application

### Step 1: Create an Executable JAR

In the terminal, navigate to your project directory and execute the following Maven command to create an executable JAR:

```bash
mvnw clean package
```

This command will compile the code, run tests, and create an executable JAR in the target/ directory.

### Step 2: Run the JAR

```bash
java -jar target/device-management-service-0.0.1-SNAPSHOT.jar
```
### Swagger-UI

http://localhost:8080/swagger-ui/index.html


### Swagger documentation - YAML
http://localhost:8080/v3/api-docs.yaml

### H2 UI
http://localhost:8080/h2-console

## Paths

### /v1/devices/{id}

#### GET /v1/devices/{id}

- **Tags**: device-controller
- **Operation ID**: findById
- **Parameters**:
    - **Name**: id
        - **In**: path
        - **Required**: true
        - **Schema**: string
- **Responses**:
    - **200**:
        - **Description**: OK
        - **Content**:
            - `*/*`:
                - **Schema**: [DeviceResponseDto](#components/schemas/deviceresponsedto)

#### PUT /v1/devices/{id}

- **Tags**: device-controller
- **Operation ID**: update
- **Parameters**:
    - **Name**: id
        - **In**: path
        - **Required**: true
        - **Schema**: string
- **Request Body**:
    - **Content**:
        - application/json:
            - **Schema**: [DeviceRequestDto](#components/schemas/devicerequestdto)
        - **Required**: true
- **Responses**:
    - **200**:
        - **Description**: OK
        - **Content**:
            - `*/*`:
                - **Schema**: [DeviceResponseDto](#components/schemas/deviceresponsedto)

#### DELETE /v1/devices/{id}

- **Tags**: device-controller
- **Operation ID**: delete
- **Parameters**:
    - **Name**: id
        - **In**: path
        - **Required**: true
        - **Schema**: string
- **Responses**:
    - **204**:
        - **Description**: No Content

#### PATCH /v1/devices/{id}

- **Tags**: device-controller
- **Operation ID**: updatePartially
- **Parameters**:
    - **Name**: id
        - **In**: path
        - **Required**: true
        - **Schema**: string
- **Request Body**:
    - **Content**:
        - application/json:
            - **Schema**: [PartialDeviceDto](#components/schemas/partialdevicedto)
        - **Required**: true
- **Responses**:
    - **200**:
        - **Description**: OK
        - **Content**:
            - `*/*`:
                - **Schema**: [DeviceResponseDto](#components/schemas/deviceresponsedto)

### /v1/devices

#### GET /v1/devices

- **Tags**: device-controller
- **Operation ID**: findAll
- **Parameters**:
    - **Name**: brandName
        - **In**: query
        - **Required**: false
        - **Schema**: string
- **Responses**:
    - **200**:
        - **Description**: OK
        - **Content**:
            - `*/*`:
                - **Schema**: array, items: [DeviceResponseDto](#components/schemas/deviceresponsedto)

#### POST /v1/devices

- **Tags**: device-controller
- **Operation ID**: save
- **Request Body**:
    - **Content**:
        - application/json:
            - **Schema**: [DeviceRequestDto](#components/schemas/devicerequestdto)
        - **Required**: true
- **Responses**:
    - **200**:
        - **Description**: OK
        - **Content**:
            - `*/*`:
                - **Schema**: [DeviceResponseDto](#components/schemas/deviceresponsedto)

## Components

### Schemas

#### DeviceRequestDto

- **Required**:
    - brand
    - name
- **Type**: object
- **Properties**:
    - name: string
    - brand: string

#### DeviceResponseDto

- **Type**: object
- **Properties**:
    - id: string (uuid)
    - name: string
    - brand: string
    - createdAt: string (date-time)

#### PartialDeviceDto

- **Required**:
    - brand
- **Type**: object
- **Properties**:
    - brand: string