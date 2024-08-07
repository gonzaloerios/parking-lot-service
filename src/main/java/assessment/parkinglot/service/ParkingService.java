package assessment.parkinglot.service;

import assessment.parkinglot.domain.ParkingStatus;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import java.util.List;

public interface ParkingService {

    Long parkVehicle(VehicleType vehicleType);
    boolean removeVehicle(Long vehicleId);

    long countAvailableSpots(ParkingSpotType type);

    boolean areAllSpotsTaken(VehicleType type);

    List<VehicleEntity> getAllParkedVehicles();
}
