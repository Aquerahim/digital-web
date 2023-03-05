package com.phoenixacces.apps.persistence.services.authentification;


import com.phoenixacces.apps.enumerations.Gender;
import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.authentication.ProfileRepository;
import com.phoenixacces.apps.persistence.repositories.authentication.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }


    public List<Profile> findAll() {
        return profileRepository.findAlls().orElseGet(ArrayList::new);
    }


    public List<Profile> findAll(Entreprises compagnie) {
        return profileRepository.findAllByActiveAndGareRoutiere_Compagnie(true, compagnie).orElseGet(ArrayList::new);
    }



    public List<User> findAlluser() {
        return userRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }



    public Profile findProfile(EntitesOrOE gare) {
        Profile profile = profileRepository.findByGareRoutiere(gare).orElseGet(() -> {
            log.info("\t[ ASK-ASSURANCE-MIDDLEWARE :: SERVICE ] - ProfileService::findAll --- contact « {} » not found into database", gare);
            return null;
        });
        log.info("\t[ ASK-ASSURANCE-MIDDLEWARE :: SERVICE ] - ProfileService::findAll --- find contact {}", profile);
        return profile;
    }


    public Profile findOneProfile(EntitesOrOE gare, String phone) {
        return profileRepository.findByGareRoutiereAndPhoneAndActive(gare, phone, true).orElseGet(() -> null);
    }


    public Profile findOneEmail(String email) {
        return profileRepository.findByEmailAndActive(email, true).orElseGet(() -> null);
    }

    public Profile findOneBy2Param(String email, String phone) {
        return profileRepository.findByEmailAndPhone(email, phone).orElseGet(() -> null);
    }


    public Profile findOne(String IdDigitalApps) {
        return profileRepository.findByIdDigitalAndActive(IdDigitalApps, true).orElseGet(() -> null);
    }


    public Profile findOneProfile(EntitesOrOE gare, String phone, ProfileType profileType) {
        return profileRepository.findByGareRoutiereAndPhoneAndActiveAndProfileType(gare, phone, true, profileType).orElseGet(() -> null);
    }


    public User findOne(String username, String defaultpassword) {
        return userRepository.findByUsernameAndActiveAndDefaultPassword(username, true, defaultpassword).orElseGet(() -> null);
    }


    public User findOneUser(String username) {
        return userRepository.findByUsernameAndActive(username, true).orElseGet(() -> null);
    }


    public User findUser(Profile profile) {
        return userRepository.findByProfileAndActive(profile, true).orElseGet(() -> null);
    }


    public Profile create(Profile e) throws Exception {
        return profileRepository.findByGareRoutiereAndPhoneAndActiveAndProfileType(e.getGareRoutiere(), e.getPhone(),true, e.getProfileType()).orElseGet(() -> {
            e.setActive(true);
            e.setCreation(Instant.now());
            e.setLastUpdate(Instant.now());
            e.setGenre(Gender.DEFAULT);
            e.setAccount(true);
            return profileRepository.save(e);
        });
    }


    public Profile update(Profile model) throws Exception {

        model.setLastUpdate(Instant.now());

        profileRepository.save(model);

        return model;
    }
}
