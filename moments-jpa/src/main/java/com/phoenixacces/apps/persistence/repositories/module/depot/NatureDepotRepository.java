package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.NatureDepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface NatureDepotRepository extends JpaRepository<NatureDepot, Long> {

    Optional<List<NatureDepot>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<NatureDepot> findByIdAndActive(Long id, Boolean active);

    Optional<NatureDepot> findByNatMouvtAndActive(String natMouvt, Boolean active);

    Optional<NatureDepot> findByOrdre(Long ordre);
}
