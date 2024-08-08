package assessment.parkinglot.service;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.domain.Vehicle;
import assessment.parkinglot.domain.VehicleFactory;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.exception.PklErrorException;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingServiceImpl implements ParkingService {

  @Autowired ParkBehavior parkBehavior;
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ParkingSpotRepository parkingSpotRepository;

  @Override
  public Long parkVehicle(VehicleType vehicleType) {
    Vehicle vehicle = VehicleFactory.create(vehicleType);
    if (this.canPark(vehicle)) {
      return vehicle.park(parkBehavior);
    }

    throw new PklErrorException(ErrorCode.NO_SPACE_TO_PARK);
  }

  @Override
  @Transactional
  public boolean removeVehicle(Long vehicleId) {
    VehicleEntity vehicle = vehicleRepository.findById(vehicleId).orElse(null);
    if (vehicle == null) {
      throw new PklBadRequestException(ErrorCode.VEHICLE_NOT_FOUND);
    }

    List<ParkingSpotEntity> occupiedSpots = parkingSpotRepository.findByVehicleId(vehicle.getId());

    occupiedSpots.forEach(
        s -> {
          s.setVehicleId(null);
          parkingSpotRepository.save(s);
        });

    vehicleRepository.delete(vehicle);

    return true;
  }

  @Override
  public long countAvailableSpots(ParkingSpotType type) {
    return this.parkingSpotRepository.countByTypeAndVehicleIdIsNull(type);
  }

  @Override
  public boolean areAllSpotsTaken(VehicleType type) {
    Vehicle vehicle = VehicleFactory.create(type);
    return !this.canPark(vehicle);
  }

  @Override
  public List<VehicleEntity> getAllParkedVehicles() {
    return this.vehicleRepository.findAll();
  }

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
