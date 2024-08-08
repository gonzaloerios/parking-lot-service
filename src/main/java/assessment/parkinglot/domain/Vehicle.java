package assessment.parkinglot.domain;

import assessment.parkinglot.behavior.ParkBehavior;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import java.util.Map;

public abstract class Vehicle {

  public abstract VehicleDTO park(ParkBehavior parkBehavior);

  public abstract Map<ParkingSpotType, Integer> getParkingSpotUsageByTypes();

  public abstract VehicleType getType();
}
