package com.moving.image.repository;

import com.moving.image.entity.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Measurements
 */

@Repository
public interface MeasurementRepository extends MongoRepository<Measurement, String> {
}
