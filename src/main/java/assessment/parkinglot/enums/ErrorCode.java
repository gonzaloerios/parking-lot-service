package assessment.parkinglot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    VEHICLE_NOT_FOUND( 4001, "The requested vehicle to leave is not parked"),
    UNKNOWN_PARKING_SPOT( 4002, "The requested Parking Spot Type is unknown"),
    UNKNOWN_VEHICLE_TYPE( 4003, "The requested Vehicle Type is unknown"),
    NO_SPACE_TO_PARK(5001, "There is no space to park this vehicle"),
    UNABLE_TO_PARK(5002, "There was an error at parking. Vehicle not parked"),
    UNKNOWN_ERROR(5000, "An unknown error has occurred");


    private Integer code;
    private String message;
}
