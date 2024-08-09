package assessment.parkinglot.service;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.domain.Vehicle;
import assessment.parkinglot.domain.VehicleFactory;
import assessment.parkinglot.dto.Translator;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklErrorException;
import assessment.parkinglot.exception.PklNotFoundException;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service implementation for managing parking operations.
 * Handles vehicle parking, removal, and parking spot availability.
 */
@Service
public class ParkingServiceImpl implements ParkingService {

  @Autowired ParkBehavior parkBehavior;
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ParkingSpotRepository parkingSpotRepository;
  @Autowired Translator translator;

    /**
     * Parks a vehicle based on its type, if there is available space.
     *
     * @param vehicleType The type of the vehicle to be parked.
     * @return The parked vehicle's {@link VehicleDTO}.
     * @throws PklErrorException if there is no space to park the vehicle.
     */
  @Override
  public VehicleDTO parkVehicle(VehicleType vehicleType) {
    Vehicle vehicle = VehicleFactory.create(vehicleType);
    if (this.canPark(vehicle)) {
      return vehicle.park(parkBehavior);
    }

    throw new PklErrorException(ErrorCode.NO_SPACE_TO_PARK);
  }

    /**
     * Removes a vehicle from the parking lot by its ID.
     *
     * @param vehicleId The ID of the vehicle to be removed.
     * @return The removed vehicle's {@link VehicleDTO}.
     * @throws PklNotFoundException if the vehicle is not found.
     */
  @Override
  @Transactional
  public VehicleDTO removeVehicle(Long vehicleId) {
    VehicleEntity vehicle = vehicleRepository.findById(vehicleId).orElse(null);
    if (vehicle == null) {
      throw new PklNotFoundException(ErrorCode.VEHICLE_NOT_FOUND);
    }

    List<ParkingSpotEntity> occupiedSpots = parkingSpotRepository.findByVehicleId(vehicle.getId());

    occupiedSpots.forEach(
        s -> {
          s.setVehicleId(null);
          parkingSpotRepository.save(s);
        });

    vehicleRepository.delete(vehicle);

    return VehicleDTO.builder().vehicleId(vehicleId).type(vehicle.getType()).parked(Boolean.FALSE).build();
  }

    /**
     * Counts the number of available parking spots of a specific type.
     *
     * @param type The type of parking spot.
     * @return The number of available spots.
     */
  @Override
  public long countAvailableSpots(ParkingSpotType type) {
    return this.parkingSpotRepository.countByTypeAndVehicleIdIsNull(type);
  }

    /**
     * Checks if all parking spots are taken for a specific vehicle type.
     *
     * @param type The type of vehicle.
     * @return {@code true} if all spots are taken, {@code false} otherwise.
     */
  @Override
  public boolean areAllSpotsTaken(VehicleType type) {
    Vehicle vehicle = VehicleFactory.create(type);
    return !this.canPark(vehicle);
  }

    /**
     * Retrieves all currently parked vehicles.
     *
     * @return A list of {@link VehicleDTO} representing the parked vehicles.
     */
  @Override
  public List<VehicleDTO> getAllParkedVehicles() {

    return this.vehicleRepository.findAll().stream()
        .map(
            v -> {
              VehicleDTO dto = translator.toDTO(v);
              dto.setParked(Boolean.TRUE);
              dto.setParkedOn(
                  parkingSpotRepository.findByVehicleId(v.getId()).stream()
                      .map(pse -> translator.toDTO(pse))
                      .collect(Collectors.toList()));
              return dto;
            })
        .collect(Collectors.toList());
  }

    /**
     * Determines if a vehicle can be parked based on available spots.
     *
     * @param vehicle The vehicle to be checked.
     * @return {@code true} if the vehicle can be parked, {@code false} otherwise.
     */
  private boolean canPark(Vehicle vehicle) {

    Map<ParkingSpotType, Integer> spotTypes = vehicle.getParkingSpotUsageByTypes();

    return spotTypes.entrySet().stream()
        .anyMatch(
            entry -> {
              List<ParkingSpotEntity> freeSpots =
                  this.parkingSpotRepository.findByTypeAndVehicleIdIsNull(entry.getKey());
              return freeSpots.size() >= entry.getValue();
            });
  }
}
