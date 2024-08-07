package assessment.parkinglot.controller.requests;

import assessment.parkinglot.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ParkRequest {

  private VehicleType vehicleType;
}
