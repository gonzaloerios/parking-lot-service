package assessment.parkinglot.domain;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class Motorcycle extends Vehicle{

    private static final VehicleType type = VehicleType.MOTORCYCLE;
    private static final Map<ParkingSpotType, Integer>  parkingSpotUsageByTypes= Map.of(ParkingSpotType.MOTORCYCLE, 1);


    @Override
    public Long park(ParkBehavior parkBehavior) {
        return parkBehavior.park(this);
    }

    @Override
    public Map<ParkingSpotType, Integer> getParkingSpotUsageByTypes() {
        return parkingSpotUsageByTypes;
    }


    @Override
    public VehicleType getType() {
        return type;
    }
}
