package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface LivreursRepository extends JpaRepository<Livreurs, Long> {

    Optional<Livreurs> findByIdAndActive(Long id, boolean active);

    Optional<List<Livreurs>> findAllByActive(boolean active);

    Optional<List<Livreurs>> findAllByActiveAndDisponibleAndProfile(boolean active, boolean disponible, Profile profile);

    Optional<Livreurs> findByNomPrenomsAndContactAndActive(String nomPrenoms, String Contact, boolean active);

    Optional<Livreurs> findByContactAndActive(String contact, boolean active);

    Optional<Livreurs> findByUsernameAndActive(String username, boolean active);

    Optional<Livreurs> findByOrdreAndActive(Long ordre, boolean active);

    Optional<List<Livreurs>> findAllByActiveAndProfile(boolean active, Profile profile);

    Optional<List<Livreurs>> findAllByProfile(Profile profile);
}
