package assessment.parkinglot.domain;

import assessment.parkinglot.behaviors.ParkBehavior;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import java.util.Map;

public abstract class Vehicle {

    public abstract Long park(ParkBehavior parkBehavior);

    public abstract Map<ParkingSpotType, Integer> getParkingSpotUsageByTypes();

    public abstract VehicleType getType();
}
