package assessment.parkinglot.UnitTests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Vehicle;
import assessment.parkinglot.dto.Translator;
import assessment.parkinglot.dto.VehicleDTO;
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
  @Mock private Translator translator;

  @InjectMocks private ParkingServiceImpl parkingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void parkVehicleSuccessfully() {
    Vehicle vehicle = mock(Vehicle.class);
    VehicleDTO dto =
        VehicleDTO.builder().vehicleId(1L).parked(Boolean.TRUE).type(VehicleType.CAR).build();

    when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
        .thenReturn(List.of(new ParkingSpotEntity()));
    when(vehicle.park(parkBehavior)).thenReturn(dto);
    when(parkBehavior.park(any(Car.class))).thenReturn(dto);

    VehicleDTO response = parkingService.parkVehicle(VehicleType.CAR);

    assertNotNull(vehicle);
    assertEquals(1L, response.getVehicleId());
    assertTrue(response.getParked());
  }

  @Test
  void parkVehicleWithMultipleSpotTypes() {
    Vehicle vehicle = mock(Vehicle.class);
    VehicleDTO dto =
        VehicleDTO.builder().vehicleId(2L).parked(Boolean.TRUE).type(VehicleType.CAR).build();

    when(vehicle.getParkingSpotUsageByTypes())
        .thenReturn(Map.of(ParkingSpotType.COMPACT, 1, ParkingSpotType.REGULAR, 1));
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.COMPACT))
        .thenReturn(List.of(new ParkingSpotEntity()));
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
        .thenReturn(List.of(new ParkingSpotEntity()));
    when(vehicle.park(parkBehavior)).thenReturn(dto);
    when(parkBehavior.park(any(Car.class))).thenReturn(dto);

    VehicleDTO response = parkingService.parkVehicle(VehicleType.CAR);

    assertNotNull(response);
    assertEquals(2L, response.getVehicleId());
    assertTrue(response.getParked());
  }

  @Test
  void parkVehicleNoAvailableSpots() {
    Vehicle vehicle = mock(Vehicle.class);
    when(vehicle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 1));
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
        .thenReturn(Collections.emptyList());

    PklErrorException exception =
        assertThrows(
            PklErrorException.class,
            () -> {
              parkingService.parkVehicle(VehicleType.CAR);
            });

    ErrorCode error = exception.getError();
    assertEquals(ErrorCode.NO_SPACE_TO_PARK, error);
  }

  @Test
  void removeVehicleSuccessfully() {
    VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.CAR).build();
    when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
    when(parkingSpotRepository.findByVehicleId(1L)).thenReturn(List.of(new ParkingSpotEntity()));

    VehicleDTO vehicleDTO = parkingService.removeVehicle(1L);

    assertNotNull(vehicleDTO);
    assertEquals(1L, vehicleDTO.getVehicleId());
    assertFalse(vehicleDTO.getParked());
    verify(vehicleRepository).delete(vehicleEntity);
  }

  @Test
  void removeVehicleWithMultipleParkingSpots() {
    VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.VAN).build();
    when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
    when(parkingSpotRepository.findByVehicleId(1L))
        .thenReturn(
            List.of(new ParkingSpotEntity(), new ParkingSpotEntity(), new ParkingSpotEntity()));

    VehicleDTO vehicleDTO = parkingService.removeVehicle(1L);

    assertNotNull(vehicleDTO);
    assertEquals(1L, vehicleDTO.getVehicleId());
    assertFalse(vehicleDTO.getParked());
    verify(parkingSpotRepository, times(3)).save(any(ParkingSpotEntity.class));
    verify(vehicleRepository).delete(vehicleEntity);
  }

  @Test
  void removeVehicleNotFound() {
    when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

    PklBadRequestException exception =
        assertThrows(
            PklBadRequestException.class,
            () -> {
              parkingService.removeVehicle(1L);
            });

    ErrorCode error = exception.getError();
    assertEquals(ErrorCode.VEHICLE_NOT_FOUND, error);
  }

  @Test
  void countAvailableSpots() {
    when(parkingSpotRepository.countByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
        .thenReturn(5L);

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
    List<VehicleEntity> vehicles =
        List.of(VehicleEntity.builder().id(1L).type(VehicleType.CAR).build());
    when(vehicleRepository.findAll()).thenReturn(vehicles);
    when(translator.toDTO(any(VehicleEntity.class)))
        .thenReturn(VehicleDTO.builder().vehicleId(1L).type(VehicleType.CAR).build());

    List<VehicleDTO> result = parkingService.getAllParkedVehicles();

    assertNotNull(result);
    assertEquals(vehicles.size(), result.size());
    for (int i = 0; i < result.size(); i++) {
      assertEquals(vehicles.get(i).getId(), result.get(i).getVehicleId());
      assertEquals(vehicles.get(i).getType().name(), result.get(i).getType().name());
    }
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
