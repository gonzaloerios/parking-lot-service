package assessment.parkinglot.dto;

import assessment.parkinglot.enums.VehicleType;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTO {

    private Long vehicleId;
    private VehicleType type;
    private Boolean parked;
    @Builder.Default
    private List<ParkingSpotDTO> parkedOn= new ArrayList<>();
}
