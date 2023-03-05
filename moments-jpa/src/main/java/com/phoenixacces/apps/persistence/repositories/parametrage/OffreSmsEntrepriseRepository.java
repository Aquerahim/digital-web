package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.OffreSmsEntreprise;
import com.phoenixacces.apps.persistence.entities.parametrage.RechargementCompteSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface OffreSmsEntrepriseRepository extends JpaRepository<OffreSmsEntreprise, Long> {

    Optional<OffreSmsEntreprise> findByIdAndActive(Long id, boolean active);

    Optional<List<OffreSmsEntreprise>> findAllByActive(boolean active);

    Optional<OffreSmsEntreprise> findAllByActiveAndNomFormuleAndTypeFormule(boolean active, String nomFormule, String typeFormule);

    Optional<OffreSmsEntreprise> findAllByActiveAndOrdre(boolean active, String ordre);
}
