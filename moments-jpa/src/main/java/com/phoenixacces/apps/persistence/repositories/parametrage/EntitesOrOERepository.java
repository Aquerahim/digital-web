package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntitesOrOERepository extends JpaRepository<EntitesOrOE, Long> {

    Optional<EntitesOrOE> findByIdAndActive(Long id, boolean active);

    Optional<List<EntitesOrOE>> findAllByActive(boolean active);

    Optional<List<EntitesOrOE>> findAllByActiveAndCompagnie(boolean active, Entreprises compagnieRoutiere);

    Optional<EntitesOrOE> findByCompagnieAndActiveAndGareRoutiere(Entreprises compagnie, boolean active, String gare);
}
