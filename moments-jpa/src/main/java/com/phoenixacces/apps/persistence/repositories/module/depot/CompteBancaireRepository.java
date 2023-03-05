package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Banque;
import com.phoenixacces.apps.persistence.entities.module.depot.CompteBancaire;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface CompteBancaireRepository  extends JpaRepository<CompteBancaire, Long> {

    Optional<List<CompteBancaire>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<List<CompteBancaire>> findAllByActiveAndEntreprisesOrderByIdDesc(Boolean active, Entreprises entreprises);

    Optional<CompteBancaire> findByBanqueAndActiveAndEntreprisesOrderByIdDesc(Banque banque, Boolean active, Entreprises entreprises);

    Optional<CompteBancaire> findByIdAndActive(Long id, Boolean active);

    Optional<CompteBancaire> findByOrdre(Long ordre);
}
