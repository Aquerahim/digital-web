package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.DureeContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DureeContratRepository extends JpaRepository<DureeContrat, Long> {

    Optional<DureeContrat> findByIdAndActive(Long id, boolean active);

    Optional<List<DureeContrat>> findAllByActive(boolean active);

    Optional<DureeContrat> findByDureeContratAndActive(String dureeContrat, boolean active);

    Optional<DureeContrat> findByDureeAndActive(int duree, boolean active);
}