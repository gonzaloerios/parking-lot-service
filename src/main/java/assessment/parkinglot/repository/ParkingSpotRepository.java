package assessment.parkinglot.repository;

import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Long> {
  List<ParkingSpotEntity> findByVehicleId(Long vehicleId);

  List<ParkingSpotEntity> findByTypeAndVehicleIdIsNull(ParkingSpotType type);

  long countByTypeAndVehicleIdIsNull(ParkingSpotType type);
}
