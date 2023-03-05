package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface LivraisonsRepository extends JpaRepository<Livraisons, Long> {

    Optional<Livraisons> findByIdAndActive(Long id, boolean active);

    Optional<List<Livraisons>> findAllByActive(boolean active);

    Optional<List<Livraisons>> findAllByActiveAndStatutLivraison(boolean active, StatutLivraison statutLivraison);

    Optional<Livraisons> findByReferenceAndActive(String reference, boolean active);

    Optional<Livraisons> findByOrdreAndActiveAndReference(Long ordre, boolean active, String reference);

    Optional<Livraisons> findByOrdreAndActive(Long ordre, boolean active);

    Optional<List<Livraisons>> findAllByActiveAndProfileOrderByIdDesc(boolean active, Profile profile);

    Optional<List<Livraisons>> findAllByActiveAndProfileAndStatutLivraisonOrderByIdDesc(boolean active, Profile profile, StatutLivraison statut);


    Optional<List<Livraisons>> findAllByActiveAndProfile(boolean active, Profile profile);

    Optional<List<Livraisons>> findAllByActiveAndProfileAndNotifClientOrderByIdDesc(boolean active, Profile profile, String notif);

    Optional<List<Livraisons>> findAllByActiveAndProfileAndNotifClientAndStatutLivraisonOrderByIdDesc(
            boolean active, Profile profile, String notif, StatutLivraison statutLivraison
    );
}
