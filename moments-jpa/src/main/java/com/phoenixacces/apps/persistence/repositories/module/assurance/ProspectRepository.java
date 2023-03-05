package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.Groupe;
import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import com.phoenixacces.apps.persistence.entities.module.assurance.Prospect;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ProspectRepository extends JpaRepository<Prospect, Long> {

    Optional<Prospect> findByIdAndActive(Long id, boolean active);

    Optional<Prospect> findByNomPrenomsAndActiveAndGroupeAndEntreprises(String nomClient, boolean active, Groupe groupe, Entreprises entreprises);

    Optional<List<Prospect>> findAllByActive(boolean active);

    Optional<Prospect> findByActiveAndId(boolean active, Long id);

    Optional<List<Prospect>> findAllByActiveAndEntreprises(boolean active, Entreprises entreprise);

    Optional<List<Prospect>> findAllByActiveAndGroupe(boolean active, Groupe groupe);

    Optional<List<Prospect>> findAllByActiveAndGroupeAndEntreprises(boolean active, Groupe groupe, Entreprises entreprise);
}
