package assessment.parkinglot.exception;

import assessment.parkinglot.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class ParkingLotException extends RuntimeException{
    protected ErrorCode error;
}
