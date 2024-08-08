package assessment.parkinglot.UnitTests.behavior;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assessment.parkinglot.behavior.ParkBehaviorImpl;
import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Motorcycle;
import assessment.parkinglot.domain.Van;
import assessment.parkinglot.dto.ParkingSpotDTO;
import assessment.parkinglot.dto.Translator;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklErrorException;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ParkBehaviorImplTest {

  @Mock private VehicleRepository vehicleRepository;
  @Mock private ParkingSpotRepository parkingSpotRepository;
  @Mock private Translator translator;

  @InjectMocks private ParkBehaviorImpl parkBehavior;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void parkCarSuccessfully() {
    Car car = mock(Car.class);
    when(car.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.COMPACT, 1));
    VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.CAR).build();
    when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
    when(translator.toDTO(any(VehicleEntity.class)))
        .thenReturn(VehicleDTO.builder().vehicleId(1L).type(VehicleType.CAR).build());
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.COMPACT))
        .thenReturn(List.of(new ParkingSpotEntity()));
    when(translator.toDTO(any(ParkingSpotEntity.class)))
        .thenReturn(
            ParkingSpotDTO.builder().parkingSpotId(33L).type(ParkingSpotType.COMPACT).build());

    VehicleDTO vehicle = parkBehavior.park(car);

    assertNotNull(vehicle);
    assertNotNull(vehicle.getVehicleId());
    assertTrue(vehicle.getParked());
    verify(parkingSpotRepository, times(1)).save(any(ParkingSpotEntity.class));
    verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
  }

  @Test
  void parkMotorcycleSuccessfully() {
    Motorcycle motorcycle = mock(Motorcycle.class);
    when(motorcycle.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.MOTORCYCLE, 1));
    VehicleEntity vehicleEntity =
        VehicleEntity.builder().id(1L).type(VehicleType.MOTORCYCLE).build();
    when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
    when(translator.toDTO(any(VehicleEntity.class)))
        .thenReturn(VehicleDTO.builder().vehicleId(1L).type(VehicleType.CAR).build());
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.MOTORCYCLE))
        .thenReturn(List.of(new ParkingSpotEntity()));
    when(translator.toDTO(any(ParkingSpotEntity.class)))
        .thenReturn(
            ParkingSpotDTO.builder().parkingSpotId(33L).type(ParkingSpotType.MOTORCYCLE).build());

    VehicleDTO vehicle = parkBehavior.park(motorcycle);

    assertNotNull(vehicle);
    assertNotNull(vehicle.getVehicleId());
    assertTrue(vehicle.getParked());
    verify(parkingSpotRepository, times(1)).save(any(ParkingSpotEntity.class));
    verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
  }

  @Test
  void parkVanSuccessfully() {
    Van van = mock(Van.class);
    when(van.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.REGULAR, 3));
    VehicleEntity vehicleEntity = VehicleEntity.builder().id(1L).type(VehicleType.VAN).build();
    when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
    when(translator.toDTO(any(VehicleEntity.class)))
        .thenReturn(VehicleDTO.builder().vehicleId(1L).type(VehicleType.VAN).build());
    when(parkingSpotRepository.findByTypeAndVehicleIdIsNull(ParkingSpotType.REGULAR))
        .thenReturn(
            List.of(new ParkingSpotEntity(), new ParkingSpotEntity(), new ParkingSpotEntity()));
    when(translator.toDTO(any(ParkingSpotEntity.class)))
        .thenReturn(
            ParkingSpotDTO.builder().parkingSpotId(33L).type(ParkingSpotType.REGULAR).build());

    VehicleDTO vehicle = parkBehavior.park(van);

    assertNotNull(vehicle);
    assertNotNull(vehicle.getVehicleId());
    assertTrue(vehicle.getParked());
    verify(parkingSpotRepository, times(3)).save(any(ParkingSpotEntity.class));
    verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
  }

  @Test
  void parkVehicleThrowsException() {
    Car car = mock(Car.class);
    when(car.getParkingSpotUsageByTypes()).thenReturn(Map.of(ParkingSpotType.COMPACT, 1));
    when(vehicleRepository.save(any(VehicleEntity.class)))
        .thenThrow(new RuntimeException("Simulated failure"));

    PklErrorException exception =
        assertThrows(
            PklErrorException.class,
            () -> {
              parkBehavior.park(car);
            });

    assertEquals(ErrorCode.UNABLE_TO_PARK, exception.getError());
    verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
    verify(parkingSpotRepository, never()).save(any(ParkingSpotEntity.class));
  }
}
