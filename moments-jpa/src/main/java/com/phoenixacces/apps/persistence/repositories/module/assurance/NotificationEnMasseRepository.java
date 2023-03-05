package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.Groupe;
import com.phoenixacces.apps.persistence.entities.module.assurance.NotificationEnMasse;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface NotificationEnMasseRepository extends JpaRepository<NotificationEnMasse, Long> {

    Optional<NotificationEnMasse> findByNomCampagneAndActive(String nomCapagne, Boolean aBoolean);

    Optional<List<NotificationEnMasse>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<NotificationEnMasse> findByIdAndActive(Long id, boolean active);

    Optional<List<NotificationEnMasse>> findAllByActiveAndEntreprisesOrderByIdDesc(boolean active, Entreprises entreprises);

    Optional<NotificationEnMasse> findByNomCampagneAndActiveAndEntreprisesOrderByIdDesc(String label, boolean active, Entreprises entreprises);

}
