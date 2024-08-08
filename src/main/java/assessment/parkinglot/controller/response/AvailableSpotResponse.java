package assessment.parkinglot.controller.response;

import assessment.parkinglot.enums.ParkingSpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AvailableSpotResponse {
  private ParkingSpotType type;
  private Integer freeSpots;
}
