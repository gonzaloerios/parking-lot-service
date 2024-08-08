package assessment.parkinglot.exception;

import assessment.parkinglot.enums.ErrorCode;

public class PklNotFoundException extends ParkingLotException{
    public PklNotFoundException(ErrorCode error) {
        super(error);
    }
}
