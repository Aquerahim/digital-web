package com.phoenixacces.apps.persistence.repositories.module.assurance;


import com.phoenixacces.apps.persistence.entities.module.assurance.Groupe;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface GroupeRepository extends JpaRepository<Groupe, Long> {

    Optional<Groupe> findByIdAndActive(Long id, boolean active);

    Optional<List<Groupe>> findAllByActiveOrderByIdDesc(boolean active);

    Optional<List<Groupe>> findAllByActiveAndEntreprisesOrderByIdDesc(boolean active, Entreprises entreprises);

    Optional<Groupe> findByNomGroupeAndActiveAndEntreprisesOrderByIdDesc(String label, boolean active, Entreprises entreprises);

    Optional<Groupe> findByOrdre(String ordre);
}
