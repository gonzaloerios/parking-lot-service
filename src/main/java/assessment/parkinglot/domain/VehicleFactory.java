package assessment.parkinglot.domain;

import assessment.parkinglot.enums.VehicleType;

public class VehicleFactory {

  public static Vehicle create(VehicleType type) {
    switch (type) {
      case CAR -> {
        return Car.builder().build();
      }
      case VAN -> {
        return Van.builder().build();
      }
      case MOTORCYCLE -> {
        return Motorcycle.builder().build();
      }
      default -> throw new IllegalArgumentException("Unknown VehicleType");
    }
  }
}
