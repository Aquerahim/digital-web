package com.phoenixacces.apps.persistence.repositories.module.depot;
import com.phoenixacces.apps.persistence.entities.module.depot.TypesPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface TypesPaiementRepository extends JpaRepository<TypesPaiement, Long> {

    Optional<List<TypesPaiement>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<TypesPaiement> findByIdAndActive(Long id, Boolean active);

    Optional<TypesPaiement> findByTypePaiementAndActive(String natMouvt, Boolean active);

    Optional<TypesPaiement> findByOrdre(Long ordre);
}
