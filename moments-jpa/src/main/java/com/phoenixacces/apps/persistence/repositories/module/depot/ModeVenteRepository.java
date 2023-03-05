package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.ModeVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ModeVenteRepository extends JpaRepository<ModeVente, Long> {

    Optional<List<ModeVente>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<ModeVente> findByIdAndActive(Long id, Boolean active);

    Optional<ModeVente> findByModeVenteAndActive(String modeVente, Boolean active);

    Optional<ModeVente> findByOrdre(Long ordre);
}
