package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.module.livraison.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    Optional<Notifications> findByIdAndActive(Long id, boolean active);

    Optional<List<Notifications>> findAllByActive(boolean active);

    Optional<List<Notifications>> findAllByStatutEnvoiAndActive(StatutLivraison statutEnvoi, boolean active);

    Optional<Notifications> findByStatutEnvoiAndActive(StatutLivraison statutEnvoi, boolean active);

    Optional<Notifications> findByRefAndEnvoiAndActive(String ref, boolean envoi,boolean active);
}
