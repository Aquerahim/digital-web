package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface NotificationSystemeRepository extends JpaRepository<NotificationSysteme, Long> {

    Optional<NotificationSysteme> findByIdAndActive(Long id, boolean active);

    Optional<List<NotificationSysteme>> findAllByActive(boolean active);

    Optional<List<NotificationSysteme>> findAllByActiveAndProfileOrderByIdDesc(boolean active, Profile profile);

    Optional<NotificationSysteme> findByActiveAndReference(boolean active, String type);

    Optional<List<NotificationSysteme>> findAllByActiveAndType(boolean active, TypeNotification type);

}
