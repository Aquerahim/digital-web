package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.RechargementCompteSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface RechargementCompteSmsRepository extends JpaRepository<RechargementCompteSms, Long> {

    Optional<RechargementCompteSms> findByIdAndActive(Long id, boolean active);

    Optional<RechargementCompteSms> findByRefPaiementAndActive(String ref, boolean active);

    Optional<List<RechargementCompteSms>> findAllByActive(boolean active);

    Optional<List<RechargementCompteSms>> findAllByActiveAndProfile(boolean active, Profile profile);

    Optional<List<RechargementCompteSms>> findAllByActiveAndStatutAndRefPaiement(boolean active, EtatLecture statut, String refPaiement);

    Optional<List<RechargementCompteSms>> findAllByActiveAndStatutAndNumPayeur(boolean active, EtatLecture statut, String numPayeur);

    Optional<List<RechargementCompteSms>> findAllByActiveAndEntreprises(boolean active, Entreprises entreprises);
}
