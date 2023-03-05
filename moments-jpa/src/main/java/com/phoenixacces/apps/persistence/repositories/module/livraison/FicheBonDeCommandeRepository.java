package com.phoenixacces.apps.persistence.repositories.module.livraison;


import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface FicheBonDeCommandeRepository extends JpaRepository<FicheBonDeCommande, Long> {

    Optional<List<FicheBonDeCommande>> findAllByActiveOrderByIdDesc(boolean active);

    Optional<List<FicheBonDeCommande>> findAllByActiveAndSuiviDemandeOrderByIdDesc(boolean active, SuiviDemande suiviDemande);

    Optional<List<FicheBonDeCommande>> findAllByActiveAndProfileOrderByIdDesc(boolean active, Profile profile);

    Optional<List<FicheBonDeCommande>> findAllByActiveAndEntrepriseOrderByIdDesc(boolean active, Entreprises entreprise);

    Optional<List<FicheBonDeCommande>> findAllByActiveAndEntrepriseAndSuiviDemandeOrderByIdDesc(boolean active, Entreprises entreprise, SuiviDemande suiviDemande);

    Optional<List<FicheBonDeCommande>> findAllByActiveAndPartenaireOrderByIdDesc(boolean active, PartenaireAffaire partenaire);

    Optional<FicheBonDeCommande> findByIdAndActive(Long id, boolean active);

    Optional<FicheBonDeCommande> findByReferenceAndActive(String reference, boolean active);

    Optional<FicheBonDeCommande> findByOrdreAndActive(Long ordre, boolean active);
}
