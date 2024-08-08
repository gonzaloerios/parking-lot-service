package assessment.parkinglot.exception;

import assessment.parkinglot.enums.ErrorCode;

public class PklBadRequestException extends ParkingLotException{
    public PklBadRequestException(ErrorCode error) {
        super(error);
    }
}
