package com.api.carparkslot.repository;

import com.api.carparkslot.entity.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, Long> {
}
