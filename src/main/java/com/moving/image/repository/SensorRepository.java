package com.moving.image.repository;

import com.moving.image.entity.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

/**
 * Repository for Sensors
 */

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {

}
