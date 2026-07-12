package com.pfe.webapp.service;

import com.pfe.webapp.entity.Coordonnee;
import com.pfe.webapp.repository.CoordonneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CoordonneeService {

    @Autowired
    private CoordonneeRepository coordonneeRepository;

    public List<Coordonnee> getAllCoordonnees() {
        return coordonneeRepository.findAll();
    }

    public Coordonnee getCoordonneeById(Long id) {
        return coordonneeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coordonnée non trouvée avec id: " + id));
    }

    public Coordonnee createCoordonnee(Coordonnee coordonnee) {
        return coordonneeRepository.save(coordonnee);
    }

    public Coordonnee updateCoordonnee(Long id, Coordonnee coordonneeDetails) {
        Coordonnee coordonnee = getCoordonneeById(id);
        coordonnee.setLatitude(coordonneeDetails.getLatitude());
        coordonnee.setLongitude(coordonneeDetails.getLongitude());
        return coordonneeRepository.save(coordonnee);
    }

    public void deleteCoordonnee(Long id) {
        Coordonnee coordonnee = getCoordonneeById(id);
        coordonneeRepository.delete(coordonnee);
    }
}