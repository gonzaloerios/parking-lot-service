package assessment.parkinglot.controller;


import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.controller.response.AvailableSpotResponse;
import assessment.parkinglot.controller.response.ErrorResponse;
import assessment.parkinglot.dto.VehicleDTO;
import assessment.parkinglot.enums.ErrorCode;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.exception.PklBadRequestException;
import assessment.parkinglot.exception.PklErrorException;
import assessment.parkinglot.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all parked vehicles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of parked vehicles"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/status")
    public ResponseEntity<List<VehicleDTO>> parkedVehicles() {

        List<VehicleDTO> parkedVehicles= parkingService.getAllParkedVehicles();

        return ResponseEntity.ok(parkedVehicles);

    }

    @Operation(summary = "Park a vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully parked the vehicle"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle type", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Remove a parked vehicle by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed the vehicle"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/leave/{vehicleId}")
    public ResponseEntity<VehicleDTO> removeVehicle(@PathVariable Long vehicleId) {
        VehicleDTO vehicleDTO;
        try{
            vehicleDTO= parkingService.removeVehicle(vehicleId);
        }catch (Exception e){
            log.error("Error at removing a vehicle: " + e.getMessage(), e.getStackTrace());
            throw new PklErrorException(ErrorCode.UNABLE_TO_LEAVE_PARK);
        }
        return ResponseEntity.ok(vehicleDTO);
    }

    @Operation(summary = "Get the number of available parking spots for a specific type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the number of available spots"),
            @ApiResponse(responseCode = "400", description = "Invalid parking spot type", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Check if all parking spots for a specific vehicle type are taken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked the parking spots"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle type", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
