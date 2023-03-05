package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Civilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface CiviliteRepository extends JpaRepository<Civilite, Long> {

    Optional<Civilite> findByIdAndActive(Long id, boolean active);

    Optional<List<Civilite>> findAllByActive(boolean active);

    Optional<Civilite> findByCiviliteAndActive(String label, boolean active);
}
