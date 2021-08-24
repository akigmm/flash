package com.moving.image.repository;

import com.moving.image.entity.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    @Query("{ 'sensor_id' : ?0 }")
    List<Measurement> findAllBySensorId(@Param("id") String sensorId);
}
