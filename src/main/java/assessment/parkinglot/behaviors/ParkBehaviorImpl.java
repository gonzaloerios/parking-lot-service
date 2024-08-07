package assessment.parkinglot.behaviors;

import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Motorcycle;
import assessment.parkinglot.domain.Van;
import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.repository.ParkingSpotRepository;
import assessment.parkinglot.repository.VehicleRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkBehaviorImpl implements ParkBehavior {
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ParkingSpotRepository parkingSpotRepository;

  @Override
  public Long park(Car car) {
    Map<ParkingSpotType, Integer> spotTypes = car.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.CAR).build();
    final Long vehicleId = parkVehicle(vehicleEntity, spotTypes);

    return vehicleId;
  }

  @Override
  public Long park(Motorcycle motorcycle) {
    Map<ParkingSpotType, Integer> spotTypes = motorcycle.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.MOTORCYCLE).build();
    final Long vehicleId = parkVehicle(vehicleEntity, spotTypes);

    return vehicleId;
  }

  @Override
  @Transactional
  public Long park(Van van) {
    Map<ParkingSpotType, Integer> spotTypes = van.getParkingSpotUsageByTypes();

    VehicleEntity vehicleEntity = VehicleEntity.builder().type(VehicleType.VAN).build();
    final Long vehicleId = parkVehicle(vehicleEntity, spotTypes);

    return vehicleId;
  }

  @Transactional
  private Long parkVehicle(VehicleEntity vehicleEntity, Map<ParkingSpotType, Integer> spotTypes) {
    vehicleEntity = this.vehicleRepository.save(vehicleEntity);
    final Long vehicleId = vehicleEntity.getId();

    for (Map.Entry<ParkingSpotType, Integer> entry : spotTypes.entrySet()) {

      List<ParkingSpotEntity> freeSpots =
          this.parkingSpotRepository.findByTypeAndVehicleIdIsNull(entry.getKey());
      if (freeSpots.size() >= entry.getValue()) {
        freeSpots = freeSpots.subList(0, entry.getValue());

        freeSpots.forEach(
            s -> {
              s.setVehicleId(vehicleId);
              this.parkingSpotRepository.save(s);
            });

        break;
      }
    }
    return vehicleId;
  }
}
