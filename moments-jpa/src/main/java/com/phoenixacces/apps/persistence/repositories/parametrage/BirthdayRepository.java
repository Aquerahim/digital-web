package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BirthdayRepository extends JpaRepository<Birthday, Long> {

    Optional<Birthday> findByIdAndActive(Long id, boolean active);


    Optional<List<Birthday>> findAllByActive(boolean active);


    Optional<Birthday> findByBirthdayAndActive(String label, boolean active);


    Optional<Birthday> findByBirthdayAndActiveAndAnniversaireuxAndAnnee(String birthDay, boolean active,
                                                                        String anniversaireux, String annee);

    Optional<Birthday> findByAnniversaireuxAndActive(String anniversaireux, boolean active);
}