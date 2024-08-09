package assessment.parkinglot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  UNKNOWN_PARKING_SPOT(4001, "The requested Parking Spot Type is unknown"),
  UNKNOWN_VEHICLE_TYPE(4002, "The requested Vehicle Type is unknown"),
  VEHICLE_NOT_FOUND(4401, "The requested vehicle to leave is not parked"),
  UNKNOWN_ERROR(5000, "An unknown error has occurred"),
  NO_SPACE_TO_PARK(5001, "There is no space to park this vehicle"),
  UNABLE_TO_PARK(5002, "There was an error at parking. Vehicle not parked"),
  UNABLE_TO_LEAVE_PARK(5003, "There was an error at leaving park lot. Vehicle is still parked");

  private Integer code;
  private String message;
}
