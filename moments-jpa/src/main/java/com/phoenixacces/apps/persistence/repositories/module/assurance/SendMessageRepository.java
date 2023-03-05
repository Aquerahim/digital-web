package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface SendMessageRepository extends JpaRepository<SendMessage, Long> {

    Optional<List<SendMessage>> findAllByActive(boolean active);

    Optional<List<SendMessage>> findAllByActiveAndStatut(boolean active, EtatLecture statut);

    Optional<List<SendMessage>> findAllByActiveAndEntreprises(boolean active, Entreprises entreprises);

    Optional<List<SendMessage>> findAllByActiveAndProfileAndStatutOrderByIdDesc(boolean active, Profile profile, EtatLecture statut);

    Optional<List<SendMessage>> findAllByActiveAndEntreprisesAndStatutOrderByIdDesc(boolean active, Entreprises entreprises, EtatLecture statut);

    Optional<List<SendMessage>> findAllByPorteFeuilleClientOrderByIdDesc(PorteFeuilleClient porteFeuilleClient);


    Optional<SendMessage> findByIdAndActive(Long id, boolean active);

    Optional<SendMessage> findByIdAndActiveAndStatut(Long id, boolean active, EtatLecture statut);
}
