package assessment.parkinglot.UnitTests.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import assessment.parkinglot.controller.ParkingController;
import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.controller.response.AvailableSpotResponse;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.service.ParkingService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class ParkingControllerTest {
    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private ParkingController parkingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParkedVehicles() {

        List<VehicleDTO> parkedVehicles = Arrays.asList(new VehicleDTO(), new VehicleDTO());
        when(parkingService.getAllParkedVehicles()).thenReturn(parkedVehicles);

        ResponseEntity<List<VehicleDTO>> response = parkingController.parkedVehicles();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(parkedVehicles, response.getBody());
        verify(parkingService, times(1)).getAllParkedVehicles();
    }

    @Test
    void testParkVehicle_Success() {

        ParkRequest parkRequest = new ParkRequest();
        parkRequest.setVehicleType("CAR");
        VehicleDTO vehicleDTO= VehicleDTO.builder().vehicleId(1L).parked(Boolean.TRUE).build();
        when(parkingService.parkVehicle(VehicleType.CAR)).thenReturn(vehicleDTO);

        ResponseEntity<VehicleDTO> response = parkingController.parkVehicle(parkRequest);
        VehicleDTO vehicle= response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, vehicle.getVehicleId());
        assertTrue(vehicle.getParked());
        verify(parkingService, times(1)).parkVehicle(VehicleType.CAR);
    }

    @Test
    void testParkVehicle_BadRequestException() {
        ParkRequest parkRequest = new ParkRequest();
        parkRequest.setVehicleType("UNKNOWN_TYPE");

        PklBadRequestException exception = assertThrows(PklBadRequestException.class, () ->
                parkingController.parkVehicle(parkRequest)
        );

        assertEquals(ErrorCode.UNKNOWN_VEHICLE_TYPE, exception.getError());
        verify(parkingService, never()).parkVehicle(any(VehicleType.class));
    }

    @Test
    void testRemoveVehicle() {
        VehicleDTO vehicleDTO= VehicleDTO.builder().vehicleId(1L).parked(false).build();
        when(parkingService.removeVehicle(1L)).thenReturn(vehicleDTO);

        ResponseEntity<VehicleDTO> response = parkingController.removeVehicle(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().getParked());
        verify(parkingService, times(1)).removeVehicle(1L);
    }

    @Test
    void testCountAvailableSpots_Success() {

        when(parkingService.countAvailableSpots(ParkingSpotType.REGULAR)).thenReturn(5L);

        ResponseEntity<AvailableSpotResponse> response = parkingController.countAvailableSpots("REGULAR");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody().getFreeSpots().longValue());
        verify(parkingService, times(1)).countAvailableSpots(ParkingSpotType.REGULAR);
    }

    @Test
    void testCountAvailableSpots_BadRequestException() {

        PklBadRequestException exception = assertThrows(PklBadRequestException.class, () ->
                parkingController.countAvailableSpots("UNKNOWN_TYPE")
        );

        assertEquals(ErrorCode.UNKNOWN_PARKING_SPOT, exception.getError());
        verify(parkingService, never()).countAvailableSpots(any(ParkingSpotType.class));
    }

    @Test
    void testAreAllSpotsTaken_Success() {

        when(parkingService.areAllSpotsTaken(VehicleType.CAR)).thenReturn(true);

        ResponseEntity<Boolean> response = parkingController.areAllSpotsTaken("CAR");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
        verify(parkingService, times(1)).areAllSpotsTaken(VehicleType.CAR);
    }

    @Test
    void testAreAllSpotsTaken_BadRequestException() {

        PklBadRequestException exception = assertThrows(PklBadRequestException.class, () ->
                parkingController.areAllSpotsTaken("UNKNOWN_TYPE")
        );

        assertEquals(ErrorCode.UNKNOWN_VEHICLE_TYPE, exception.getError());
        verify(parkingService, never()).areAllSpotsTaken(any(VehicleType.class));
    }
}
