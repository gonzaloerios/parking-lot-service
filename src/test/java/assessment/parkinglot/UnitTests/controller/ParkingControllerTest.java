package assessment.parkinglot.UnitTests.controller;

import assessment.parkinglot.controller.ParkingController;
import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

        List<VehicleEntity> parkedVehicles = Arrays.asList(new VehicleEntity(), new VehicleEntity());
        when(parkingService.getAllParkedVehicles()).thenReturn(parkedVehicles);

        ResponseEntity<List<VehicleEntity>> response = parkingController.parkedVehicles();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(parkedVehicles, response.getBody());
        verify(parkingService, times(1)).getAllParkedVehicles();
    }

    @Test
    void testParkVehicle_Success() {

        ParkRequest parkRequest = new ParkRequest();
        parkRequest.setVehicleType("CAR");
        when(parkingService.parkVehicle(VehicleType.CAR)).thenReturn(1L);

        ResponseEntity<Long> response = parkingController.parkVehicle(parkRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody());
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

        when(parkingService.removeVehicle(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = parkingController.removeVehicle(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
        verify(parkingService, times(1)).removeVehicle(1L);
    }

    @Test
    void testCountAvailableSpots_Success() {

        when(parkingService.countAvailableSpots(ParkingSpotType.REGULAR)).thenReturn(5L);

        ResponseEntity<Long> response = parkingController.countAvailableSpots("REGULAR");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody());
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
