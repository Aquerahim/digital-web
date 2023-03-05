package com.phoenixacces.apps.persistence.repositories.authentication;


import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<List<Profile>> findAllByActive(boolean active);

    @Query("select c from Profile c where c.active = 1 and c.idDigital <> '2022000001' ")
    Optional<List<Profile>> findAlls();

    Optional<Profile> findByIdDigitalAndActive(String IdDigitalApps, boolean active);

    Optional<Profile> findByNomPrenomsAndActive(String libelle, boolean active);

    Optional<Profile> findByEmailAndActive(String email, boolean active);

    Optional<Profile> findByEmailAndPhone(String email, String phone);

    Optional<Profile> findByGareRoutiere(EntitesOrOE gareRoutiere);

    Optional<Profile> findByGareRoutiereAndPhoneAndActive(EntitesOrOE gareRoutiere, String phone, boolean active);

    Optional<Profile> findByGareRoutiereAndPhoneAndActiveAndProfileType(EntitesOrOE gareRoutiere, String phone, boolean active, ProfileType profileType);

    Optional<List<Profile>> findAllByActiveAndGareRoutiere_Compagnie(boolean active, Entreprises entreprises);

}
