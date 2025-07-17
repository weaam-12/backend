package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {

    List<WaterReading> findByProperty_PropertyId(Long propertyId);

}