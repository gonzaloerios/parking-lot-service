package assessment.parkinglot.dto;

import assessment.parkinglot.enums.ParkingSpotType;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParkingSpotDTO {

  private Long parkingSpotId;
  private ParkingSpotType type;
}
