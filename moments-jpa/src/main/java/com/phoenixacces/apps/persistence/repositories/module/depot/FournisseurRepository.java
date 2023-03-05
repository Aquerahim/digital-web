package com.phoenixacces.apps.persistence.repositories.module.depot;
import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    Optional<List<Fournisseur>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<List<Fournisseur>> findAllByActiveAndEntreprisesOrderByIdDesc(Boolean active, Entreprises entreprises);

    Optional<Fournisseur> findByIdAndActive(Long id, Boolean active);

    Optional<Fournisseur> findByNomCompletAndActive(String natMouvt, Boolean active);

    Optional<Fournisseur> findByOrdre(Long ordre);
}
