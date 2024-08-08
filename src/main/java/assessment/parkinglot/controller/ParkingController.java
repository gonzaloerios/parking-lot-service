package assessment.parkinglot.controller;


import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.service.ParkingService;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/parking")
@Slf4j
public class ParkingController {

    @Autowired
    ParkingService parkingService;

    @GetMapping("/status")
    public ResponseEntity<List<VehicleEntity>> parkedVehicles() {

        List<VehicleEntity> parkedVehicles= parkingService.getAllParkedVehicles();

        return ResponseEntity.ok(parkedVehicles);

    }

    @PostMapping("/park")
    public ResponseEntity<Long> parkVehicle(@RequestBody ParkRequest vehicle) {

        VehicleType vehicleType;

        try{
            vehicleType= VehicleType.valueOf(vehicle.getVehicleType());
        }catch (Exception e){
            throw new PklBadRequestException(ErrorCode.UNKNOWN_VEHICLE_TYPE);
        }

        Long vehicleId= parkingService.parkVehicle(vehicleType);

        if( Objects.nonNull(vehicleId)){
            return ResponseEntity.ok(vehicleId);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/leave/{vehicleId}")
    public ResponseEntity<Boolean> removeVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(parkingService.removeVehicle(vehicleId));
    }

    @GetMapping("/available-spots/{type}")
    public ResponseEntity<Long> countAvailableSpots(@PathVariable String type) {

        ParkingSpotType sportType;

        try{
            sportType= ParkingSpotType.valueOf(type);
        }catch (Exception e){
            throw new PklBadRequestException(ErrorCode.UNKNOWN_PARKING_SPOT);
        }

        return ResponseEntity.ok(parkingService.countAvailableSpots(sportType));
    }

    @GetMapping("/all-spots-taken/{type}")
    public ResponseEntity<Boolean> areAllSpotsTaken(@PathVariable String type) {

        VehicleType vehicleType;

        try{
            vehicleType= VehicleType.valueOf(type);
        }catch (Exception e){
            throw new PklBadRequestException(ErrorCode.UNKNOWN_VEHICLE_TYPE);
        }

        return ResponseEntity.ok(parkingService.areAllSpotsTaken(vehicleType));
    }

}
