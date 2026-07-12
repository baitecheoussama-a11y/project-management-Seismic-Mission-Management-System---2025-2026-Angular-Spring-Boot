// mongodb/repository/RapportDetailsRepository.java
package com.pfe.webapp.mongodb.repository;

import com.pfe.webapp.mongodb.document.RapportDetailsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RapportDetailsRepository
        extends MongoRepository<RapportDetailsDocument, String> {

    Optional<RapportDetailsDocument> findByRapportId(Long rapportId);

    List<RapportDetailsDocument> findByProjectId(Long projectId);

    @Query(value = "{ 'rapportId': ?0 }", fields = "{ 'details': 1 }")
    Optional<RapportDetailsDocument> findDetailsByRapportId(Long rapportId);
}