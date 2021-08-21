package com.moving.image;

import com.moving.image.repository.MeasurementRepository;
import com.moving.image.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Slf4j
@EnableMongoRepositories(basePackageClasses = {SensorRepository.class, MeasurementRepository.class})
public class FlashApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashApplication.class, args);
        log.info("Started flash application.");
    }
}
