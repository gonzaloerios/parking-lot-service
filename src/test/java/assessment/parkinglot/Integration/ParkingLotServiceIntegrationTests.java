package assessment.parkinglot.Integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import assessment.parkinglot.controller.request.ParkRequest;
import assessment.parkinglot.entities.VehicleEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.enums.VehicleType;
import assessment.parkinglot.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ParkingLotServiceIntegrationTests {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private ParkingService parkingService;
  @Autowired private MockMvc mockMvc;

  @Test
  /**
   * Tested Endpoints: - Park van (OK) - Park van (Failed) - All spots taken for a van (FALSE) - All
   * spots taken for a van (TRUE) - Leave a van - Amount of spots available
   *
   * <p>This test involves adding all possible vans and then removing them, while testing the "all
   * spots taken" status and "amount of free spots" in between.
   */
  void fullVanPark() throws Exception {

    boolean first = true;
    this.availableSpots(ParkingSpotType.REGULAR, 9);

    for (int i = 0; i < 3; i++) {
      this.parkVehicleOk(VehicleType.VAN);

      if (first) {
        this.allSpotsTaken(VehicleType.VAN, false);
        first = false;
      }
    }

    this.parkVehicleError(VehicleType.VAN);
    this.allSpotsTaken(VehicleType.VAN, true);
    this.availableSpots(ParkingSpotType.REGULAR, 0);

    removeAllVehicles();

    this.allSpotsTaken(VehicleType.VAN, false);
    this.availableSpots(ParkingSpotType.REGULAR, 9);
  }

  @Test
  /**
   * Tested Endpoints: - Park car (OK) - Park car (Failed) - All spots taken for a car (FALSE) - All
   * spots taken for a car (TRUE) - Leave a car - Amount of spots available
   *
   * <p>This test involves adding all possible cars and then removing them, while testing the "all
   * spots taken" status and "amount of free spots" in between.
   */
  void fullCARPark() throws Exception {

    boolean first = true;
    this.availableSpots(ParkingSpotType.REGULAR, 9);
    this.availableSpots(ParkingSpotType.COMPACT, 11);

    for (int i = 0; i < 20; i++) {
      this.parkVehicleOk(VehicleType.CAR);
      if (first) {
        this.allSpotsTaken(VehicleType.CAR, false);
        first = false;
      }
    }
    this.parkVehicleError(VehicleType.CAR);
    this.allSpotsTaken(VehicleType.CAR, true);
    this.availableSpots(ParkingSpotType.REGULAR, 0);
    this.availableSpots(ParkingSpotType.COMPACT, 0);

    removeAllVehicles();

    this.allSpotsTaken(VehicleType.CAR, false);
    this.availableSpots(ParkingSpotType.REGULAR, 9);
    this.availableSpots(ParkingSpotType.COMPACT, 11);
  }

  @Test
  /**
   * Tested Endpoints: - Park motorcycle (OK) - Park motorcycle (Failed) - All spots taken for a
   * motorcycle (FALSE) - All spots taken for a motorcycle (TRUE) - Leave a motorcycle - Amount of spots available
   *
   * <p>This test involves adding all possible motorcycles and then removing them, while testing the
   * "all spots taken" status  and "amount of free spots" in between.
   */
  void fullMotorcyclePark() throws Exception {

    boolean first = true;
    this.availableSpots(ParkingSpotType.MOTORCYCLE, 5);

    for (int i = 0; i < 5; i++) {
      this.parkVehicleOk(VehicleType.MOTORCYCLE);
      if (first) {
        this.allSpotsTaken(VehicleType.MOTORCYCLE, false);
        first = false;
      }
    }
    this.parkVehicleError(VehicleType.MOTORCYCLE);
    this.allSpotsTaken(VehicleType.MOTORCYCLE, true);
    this.availableSpots(ParkingSpotType.MOTORCYCLE, 0);

    removeAllVehicles();

    this.allSpotsTaken(VehicleType.MOTORCYCLE, false);
    this.availableSpots(ParkingSpotType.MOTORCYCLE, 5);
  }

  @Test
  /**
   * Test full the parking with vans and then with cars, leave a van, park a car and checks there is
   * 2 spots available and all spots are taken for a van
   */
  void noParkingVanSpotWithTwoRegularSpotsAvailable() throws Exception {

    this.availableSpots(ParkingSpotType.REGULAR, 9);

    for (int i = 0; i < 3; i++) {
      this.parkVehicleOk(VehicleType.VAN);
    }

    for (int i = 0; i < 11; i++) {
      this.parkVehicleOk(VehicleType.CAR);
    }

    this.allSpotsTaken(VehicleType.VAN, true);

    List<VehicleEntity> parkedVehicles = this.getAllVehicles();

    Long vanId =
        parkedVehicles.stream()
            .filter(v -> VehicleType.VAN.equals(v.getType()))
            .map(v -> v.getId())
            .findFirst()
            .orElse(1L);

    this.leaveParkVehicle(vanId);

    this.allSpotsTaken(VehicleType.VAN, false);

    this.parkVehicleOk(VehicleType.CAR);

    this.allSpotsTaken(VehicleType.VAN, true);

    this.availableSpots(ParkingSpotType.REGULAR, 2);

    removeAllVehicles();

    this.availableSpots(ParkingSpotType.REGULAR, 9);
  }

  private void parkVehicleOk(VehicleType type) throws Exception {

    ParkRequest request = ParkRequest.builder().vehicleType(type.name()).build();

    this.mockMvc
        .perform(
            post("/parking/park")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNumber());
  }

  private void parkVehicleError(VehicleType type) throws Exception {

    ParkRequest request = ParkRequest.builder().vehicleType(type.name()).build();

    this.mockMvc
        .perform(
            post("/parking/park")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().is5xxServerError());
  }

  private void allSpotsTaken(VehicleType type, boolean value) throws Exception {

    this.mockMvc
        .perform(
            get("/parking/all-spots-taken/{vehicleType}", type)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(value));
  }

  private void availableSpots(ParkingSpotType type, int value) throws Exception {

    this.mockMvc
        .perform(
            get("/parking/available-spots/{type}", type)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(value));
  }

  private void removeAllVehicles() throws Exception {
    List<VehicleEntity> vehicles = this.getAllVehicles();

    vehicles.forEach(
        v -> {
          try {
            this.leaveParkVehicle(v.getId());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private List<VehicleEntity> getAllVehicles() throws Exception {

    return this.parkingService.getAllParkedVehicles();
  }

  private void leaveParkVehicle(Long vehicleId) throws Exception {

    this.mockMvc
        .perform(
            delete("/parking/leave/{vehicleId}", vehicleId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
