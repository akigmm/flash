package com.moving.image.repository;

import com.moving.image.entity.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for Sensors
 */

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {

    @Transactional
    @Query("update sensors u set u.status=:status WHERE u.sensor_id = :id")
    void updateStatusById(@Param("status") String status, @Param("id") String sensorId);
}
