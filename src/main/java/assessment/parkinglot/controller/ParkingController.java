package assessment.parkinglot.controller;


import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.controller.response.AvailableSpotResponse;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.service.ParkingService;
import java.util.List;
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
    public ResponseEntity<List<VehicleDTO>> parkedVehicles() {

        List<VehicleDTO> parkedVehicles= parkingService.getAllParkedVehicles();

        return ResponseEntity.ok(parkedVehicles);

    }

    @PostMapping("/park")
    public ResponseEntity<VehicleDTO> parkVehicle(@RequestBody ParkRequest parkRequest) {

        VehicleType vehicleType;

        try{
            vehicleType= VehicleType.valueOf(parkRequest.getVehicleType());
        }catch (Exception e){
            throw new PklBadRequestException(ErrorCode.UNKNOWN_VEHICLE_TYPE);
        }

        VehicleDTO vehicle= parkingService.parkVehicle(vehicleType);

        return ResponseEntity.ok(vehicle);

    }

    @DeleteMapping("/leave/{vehicleId}")
    public ResponseEntity<VehicleDTO> removeVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(parkingService.removeVehicle(vehicleId));
    }

    @GetMapping("/available-spots/{type}")
    public ResponseEntity<AvailableSpotResponse> countAvailableSpots(@PathVariable String type) {

        ParkingSpotType spotType;

        try{
            spotType= ParkingSpotType.valueOf(type);
        }catch (Exception e){
            throw new PklBadRequestException(ErrorCode.UNKNOWN_PARKING_SPOT);
        }

        Long freeSpots= parkingService.countAvailableSpots(spotType);

        return ResponseEntity.ok(AvailableSpotResponse.builder().type(spotType).freeSpots(freeSpots.intValue()).build());
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
