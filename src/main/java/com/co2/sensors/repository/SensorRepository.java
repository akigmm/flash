package com.co2.sensors.repository;

import com.co2.sensors.entity.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

/**
 * Repository for Sensors
 */

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {

}
