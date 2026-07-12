// repository/analytics/DimResourceRepository.java
package com.pfe.webapp.repository.analytics;

import com.pfe.webapp.entity.analytics.DimResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DimResourceRepository extends JpaRepository<DimResource, Long> {

    List<DimResource> findByTypeResource(String typeResource);
}