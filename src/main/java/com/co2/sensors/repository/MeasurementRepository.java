package com.co2.sensors.repository;

import com.co2.sensors.entity.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Measurements
 */

@Repository
public interface MeasurementRepository extends MongoRepository<Measurement, String> {

    /**
     * Fetches measurements from the store based on sensorId.
     *
     * If not found, returns an empty list.
     *
     * @param sensorId String sensor identifier
     */
    @Query(value = "{ 'sensorId' : ?0 }")
    List<Measurement> findAllBySensorId(String sensorId);
}
