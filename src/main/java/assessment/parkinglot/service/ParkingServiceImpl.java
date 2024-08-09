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

@Service
public class ParkingServiceImpl implements ParkingService {

  @Autowired ParkBehavior parkBehavior;
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ParkingSpotRepository parkingSpotRepository;
  @Autowired Translator translator;

  @Override
  public VehicleDTO parkVehicle(VehicleType vehicleType) {
    Vehicle vehicle = VehicleFactory.create(vehicleType);
    if (this.canPark(vehicle)) {
      return vehicle.park(parkBehavior);
    }

    throw new PklErrorException(ErrorCode.NO_SPACE_TO_PARK);
  }

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
