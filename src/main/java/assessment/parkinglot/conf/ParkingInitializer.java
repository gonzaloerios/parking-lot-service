package assessment.parkinglot.conf;

import static assessment.parkinglot.constants.SystemConfConstants.*;

import assessment.parkinglot.entities.ParkingSpotEntity;
import assessment.parkinglot.enums.ParkingSpotType;
import assessment.parkinglot.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that initializes the parking spot repository
 * when the application starts.
 *
 * This class is responsible for creating and saving parking spots in the repository
 * at application startup, distributing them into different types of spots: motorcycle,
 * compact, and regular.
 *
 * Implements the {@link ApplicationRunner} interface to execute additional code
 * after the application has started.
 */
@Configuration
public class ParkingInitializer implements ApplicationRunner {

  @Autowired ParkingSpotRepository spotRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    // Add motorcycle spots
    for (int i = 0; i < MOTORCYCLE_SPOTS_AMOUNT; i++) {
      spotRepository.save(ParkingSpotEntity.builder().type(ParkingSpotType.MOTORCYCLE).build());
    }
    // Add compact car spots
    for (int i = 0; i < COMPACT_SPOTS_AMOUNT; i++) {
      spotRepository.save(ParkingSpotEntity.builder().type(ParkingSpotType.COMPACT).build());
    }
    // Add regular spots
    for (int i = 0; i < REGULAR_SPOTS_AMOUNT; i++) {
      spotRepository.save(ParkingSpotEntity.builder().type(ParkingSpotType.REGULAR).build());
    }
  }
}
