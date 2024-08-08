package assessment.parkinglot.service;

import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import java.util.List;

public interface ParkingService {

    VehicleDTO parkVehicle(VehicleType vehicleType);
    VehicleDTO removeVehicle(Long vehicleId);

    long countAvailableSpots(ParkingSpotType type);

    boolean areAllSpotsTaken(VehicleType type);

    List<VehicleDTO> getAllParkedVehicles();
}
