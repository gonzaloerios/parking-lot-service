package assessment.parkinglot.exception;

import assessment.parkinglot.enums.ErrorCode;

public class PklErrorException extends ParkingLotException {
  public PklErrorException(ErrorCode error) {
    super(error);
  }
}
