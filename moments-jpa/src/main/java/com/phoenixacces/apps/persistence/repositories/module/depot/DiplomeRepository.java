package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Diplome;
import com.phoenixacces.apps.persistence.entities.module.depot.MotifAvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiplomeRepository extends JpaRepository<Diplome, Long> {

    Optional<List<Diplome>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<Diplome> findByIdAndActive(Long id, Boolean active);

    Optional<Diplome> findByDiplomeAndActive(String natMouvt, Boolean active);

    Optional<Diplome> findByOrdre(Long ordre);
}
