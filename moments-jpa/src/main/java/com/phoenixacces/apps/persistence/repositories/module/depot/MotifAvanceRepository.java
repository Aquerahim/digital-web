package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.MotifAvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotifAvanceRepository extends JpaRepository<MotifAvance, Long> {

    Optional<List<MotifAvance>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<MotifAvance> findByIdAndActive(Long id, Boolean active);

    Optional<MotifAvance> findByMotifAndActive(String natMouvt, Boolean active);

    Optional<MotifAvance> findByOrdre(Long ordre);
}
