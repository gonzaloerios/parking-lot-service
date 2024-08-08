package assessment.parkinglot.dto;

import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.entities.VehicleEntity;
import org.springframework.stereotype.Service;

@Service
public class Translator {

  public VehicleDTO toDTO(VehicleEntity entity) {
    return VehicleDTO.builder().vehicleId(entity.getId()).type(entity.getType()).build();
  }

  public ParkingSpotDTO toDTO(ParkingSpotEntity entity) {
    return ParkingSpotDTO.builder().parkingSpotId(entity.getId()).type(entity.getType()).build();
  }
}
