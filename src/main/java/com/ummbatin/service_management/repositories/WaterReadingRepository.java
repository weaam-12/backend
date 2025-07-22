package com.ummbatin.service_management.repositories;

import com.ummbatin.service_management.models.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    List<WaterReading> findByProperty_PropertyIdOrderByDateDesc(Long propertyId);
    List<WaterReading> findByProperty_User_UserId(Long userId);

    // أو باستخدام query مخصصة
    @Query("SELECT wr FROM WaterReading wr WHERE wr.property.user.userId = :userId")
    List<WaterReading> findByUserId(@Param("userId") Long userId);

    // للحصول على قراءات عقار معين
    List<WaterReading> findByProperty_PropertyId(Long propertyId);
}