package assessment.parkinglot.behavior;

import assessment.parkinglot.domain.Car;
import assessment.parkinglot.domain.Motorcycle;
import assessment.parkinglot.domain.Van;

public interface ParkBehavior {

    Long park(Car car);
    Long park(Motorcycle motorcycle);
    Long park (Van van);
}
