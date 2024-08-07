package assessment.parkinglot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assessment.parkinglot.behaviors.ParkBehavior;
import assessment.parkinglot.domain.Vehicle;
import assessment.parkinglot.domain.VehicleFactory;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Long vehicleId = parkingService.parkVehicle(VehicleType.CAR);

        assertNotNull(vehicleId);
        assertEquals(0L, vehicleId);
    }

    @Test
    void parkVehicleNoAvailableSpots() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
        when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
                .thenReturn(Collections.emptyList());

        Long vehicleId = parkingService.parkVehicle(VehicleType.CAR);

        assertNull(vehicleId);
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
    void removeVehicleNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingService.removeVehicle(1L);
        });

        assertEquals("Unknown Vehicle: 1", exception.getMessage());
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
}