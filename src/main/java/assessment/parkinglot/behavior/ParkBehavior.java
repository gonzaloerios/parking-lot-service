package assessment.parkinglot.behavior;

import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Motorcycle;
import assessment.parkinglot.domain.Van;
import assessment.parkinglot.dto.VehicleDTO;

public interface ParkBehavior {

  VehicleDTO park(Car car);

  VehicleDTO park(Motorcycle motorcycle);

  VehicleDTO park(Van van);
}
