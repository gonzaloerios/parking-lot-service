package assessment.parkinglot.UnitTests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Vehicle;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.exception.PklErrorException;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import assessment.parkinglot.service.ParkingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ParkingServiceImplTest {

    @Mock private ParkBehavior parkBehavior;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks private ParkingServiceImpl parkingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void parkVehicleSuccessfully() {
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(List.of(new ParkingSpotEntity()));
        when(vehicle.park(parkBehavior)).thenReturn(1L);
        when(parkBehavior.park(any(Car.class))).thenReturn(1L);

        Long vehicleId = parkingService.parkVehicle(VehicleType.CAR);

        assertNotNull(vehicleId);
        assertEquals(1L, vehicleId);
    }

    @Test
    void parkVehicleWithMultipleSpotTypes() {
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getParkingSpotUsageByTypes())
                .thenReturn(Map.of(ParkingSpotType.COMPACT, 1, ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.COMPACT))
                .thenReturn(List.of(new ParkingSpotEntity()));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(List.of(new ParkingSpotEntity()));
        when(vehicle.park(parkBehavior)).thenReturn(2L);
        when(parkBehavior.park(any(Car.class))).thenReturn(2L);

        Long vehicleId = parkingService.parkVehicle(VehicleType.CAR);

        assertNotNull(vehicleId);
        assertEquals(2L, vehicleId);
    }

    @Test
    void parkVehicleNoAvailableSpots() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(Collections.emptyList());

        PklErrorException exception = assertThrows(PklErrorException.class, () -> {
            parkingService.parkVehicle(VehicleType.CAR);
        });

        ErrorCode error = exception.getError();
        assertEquals(ErrorCode.NO_SPACE_TO_PARK, error);

    }

    @Test
    void removeVehicleSuccessfully() {
        VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.CAR).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(parkingSpotRepository.findByVehicleId(1L))
                .thenReturn(List.of(new ParkingSpotEntity()));

        boolean result = parkingService.removeVehicle(1L);

        assertTrue(result);
        verify(vehicleRepository).delete(vehicleEntity);
    }

    @Test
    void removeVehicleWithMultipleParkingSpots() {
        VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.VAN).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(parkingSpotRepository.findByVehicleId(1L))
                .thenReturn(List.of(new ParkingSpotEntity(), new ParkingSpotEntity(), new ParkingSpotEntity()));

        boolean result = parkingService.removeVehicle(1L);

        assertTrue(result);
        verify(parkingSpotRepository, times(3)).save(any(ParkingSpotEntity.class));
        verify(vehicleRepository).delete(vehicleEntity);
    }

    @Test
    void removeVehicleNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        PklBadRequestException exception = assertThrows(PklBadRequestException.class, () -> {
            parkingService.removeVehicle(1L);
        });

        ErrorCode error = exception.getError();
        assertEquals(ErrorCode.VEHICLE_NOT_FOUND, error);
    }

    @Test
    void countAvailableSpots() {
        when(parkingSpotRepository.countByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR)).thenReturn(5L);

        long availableSpots = parkingService.countAvailableSpots(ParkingSpotType.REGULAR);

        assertEquals(5L, availableSpots);
    }

    @Test
    void areAllSpotsTaken() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(Collections.emptyList());

        boolean result = parkingService.areAllSpotsTaken(VehicleType.CAR);

        assertTrue(result);
    }

    @Test
    void getAllParkedVehicles() {
        List<VehicleEntity> vehicles = List.of(new VehicleEntity());
        when(vehicleRepository.findAll()).thenReturn(vehicles);

        List<VehicleEntity> result = parkingService.getAllParkedVehicles();

        assertEquals(vehicles, result);
    }

    @Test
    void areAllSpotsTakenWhenAvailable() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(List.of(new ParkingSpotEntity()));

        boolean result = parkingService.areAllSpotsTaken(VehicleType.CAR);

        assertFalse(result);
    }
}