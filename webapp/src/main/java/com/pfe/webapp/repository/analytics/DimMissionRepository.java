// repository/analytics/DimMissionRepository.java
package com.pfe.webapp.repository.analytics;

import com.pfe.webapp.entity.analytics.DimMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DimMissionRepository extends JpaRepository<DimMission, Long> {

    Optional<DimMission> findByCodeMission(String codeMission);

    List<DimMission> findByMethodologie(String methodologie);
}