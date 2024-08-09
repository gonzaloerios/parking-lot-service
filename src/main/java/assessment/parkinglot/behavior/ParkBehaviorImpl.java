package assessment.parkinglot.behavior;

import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Motorcycle;
import assessment.parkinglot.domain.Van;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing vehicle parking behavior.
 * Handles parking operations for different types of vehicles.
 */
@Service
@Slf4j
public class ParkBehaviorImpl implements ParkBehavior {
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ParkingSpotRepository parkingSpotRepository;
  @Autowired Translator translator;

  /**
   * Parks a {@link Car} in the appropriate parking spots.
   *
   * @param car The {@link Car} to be parked.
   * @return The parked car's {@link VehicleDTO}.
   */
  @Override
  public VehicleDTO park(Car car) {
    Map<ParkingSpotType, Integer> spotTypes = car.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.CAR).build();
    return parkVehicle(vehicleEntity, spotTypes);
  }

  /**
   * Parks a {@link Motorcycle} in the appropriate parking spots.
   *
   * @param motorcycle The {@link Motorcycle} to be parked.
   * @return The parked motorcycle's {@link VehicleDTO}.
   */
  @Override
  public VehicleDTO park(Motorcycle motorcycle) {
    Map<ParkingSpotType, Integer> spotTypes = motorcycle.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.MOTORCYCLE).build();
    return parkVehicle(vehicleEntity, spotTypes);
  }

  /**
   * Parks a {@link Van} in the appropriate parking spots.
   *
   * @param van The {@link Van} to be parked.
   * @return The parked van's {@link VehicleDTO}.
   */
  @Override
  public VehicleDTO park(Van van) {
    Map<ParkingSpotType, Integer> spotTypes = van.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.VAN).build();
    return parkVehicle(vehicleEntity, spotTypes);
  }

/**
 * Attempts to park a vehicle in the appropriate spots.
 *
 * @param vehicleEntity The {@link VehicleEntity} to be parked.
 * @param spotTypes A Map with the types, and its amoutns, of parking spots required.
 * @return The parked vehicle's {@link VehicleDTO}.
 * @throws PklErrorException if the vehicle cannot be parked.
 */
  private VehicleDTO parkVehicle(
      VehicleEntity vehicleEntity, Map<ParkingSpotType, Integer> spotTypes) {

    try {
      return this.doPark(vehicleEntity, spotTypes);
    } catch (Exception e) {
      log.error("Error at parking a vehicle: " + e.getMessage(), e.getStackTrace());
      throw new PklErrorException(ErrorCode.UNABLE_TO_PARK);
    }
  }

  /**
   * Performs the actual parking operation transactionally, updating the parking spots.
   *
   * @param vehicleEntity The {@link VehicleEntity} being parked.
   * @param spotTypes The types of parking spots required.
   * @return The parked vehicle's {@link VehicleDTO}.
   */
  @Transactional
  private VehicleDTO doPark(VehicleEntity vehicleEntity, Map<ParkingSpotType, Integer> spotTypes) {
    vehicleEntity = this.vehicleRepository.save(vehicleEntity);
    final Long vehicleId = vehicleEntity.getId();

    VehicleDTO vehicleDTO = translator.toDTO(vehicleEntity);

    for (Map.Entry<ParkingSpotType, Integer> entry : spotTypes.entrySet()) {

      List<ParkingSpotEntity> freeSpots =
          this.parkingSpotRepository.findByTypeAndVehicleIdIsNull(entry.getKey());
      if (freeSpots.size() >= entry.getValue()) {
        freeSpots = freeSpots.subList(0, entry.getValue());

        freeSpots.forEach(
            s -> {
              s.setVehicleId(vehicleId);
              this.parkingSpotRepository.save(s);
              vehicleDTO.getParkedOn().add(translator.toDTO(s));
            });

        break;
      }
    }

    vehicleDTO.setParked(Boolean.TRUE);
    return vehicleDTO;
  }
}
