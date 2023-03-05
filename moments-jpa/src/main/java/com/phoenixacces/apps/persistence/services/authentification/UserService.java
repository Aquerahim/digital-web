package com.phoenixacces.apps.persistence.services.authentification;

import com.phoenixacces.apps.enumerations.Gender;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.authentication.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> findAll() {
        return userRepository.findAllByActiveAndProfileIsNotNull(true).orElseGet(ArrayList::new);
    }


    public List<User> findAll(Profile profile) {
        return userRepository.findAllByActiveAndProfile(true, profile).orElseGet(ArrayList::new);
    }


    public List<User> findAll(Entreprises entreprises) {
        return userRepository.findAllByProfileGareRoutiereGareRoutiere(entreprises).orElseGet(ArrayList::new);
    }

    public User create(User e) throws Exception {
        return userRepository.findByProfileAndActive(e.getProfile(), true).orElseGet(() -> {
            e.setActive(true);
            e.setCreation(Instant.now());
            e.setLastUpdate(Instant.now());
            e.setLastUpdate(Instant.now());
            e.setFirstConnexion(1);
            e.setPassword(BCrypt.hashpw("di-gital", BCrypt.gensalt()));
            e.setDefaultPassword("di-gital");
            return userRepository.save(e);
        });
    }


    public User update(User user) throws Exception {

        userRepository.findByIdAndActive(user.getId(), true).ifPresent(b -> {

            user.setLastUpdate(Instant.now());

            user.setVersion(user.getVersion() + 1);

            userRepository.save(user);

        });

        return user;
    }


    public User updated(User model) throws Exception {

        model.setLastUpdate(Instant.now());

        userRepository.save(model);

        return model;
    }

    /*
    public void updatePassword(String username, String paswword) throws Exception {
        this.userRepository.findByUsernameAndActive(username, true).ifPresent((b) -> {
            log.info("[ ASK-ASSURANCE :: MOBILE API ] - UPDATE PASSWORD =====> START ");
            Calendar calendar = Calendar.getInstance();
            calendar.add(2, 3);
            Date dateChangPwd = calendar.getTime();
            b.setLastUpdate(Instant.now());
            b.setPassword(BCrypt.hashpw(paswword, BCrypt.gensalt()));
            b.setFirst(false);
            b.setDateChangPwd(dateChangPwd);
            b.setLastConnexion(Instant.now());
            this.userRepository.save(b);
        });
    }
     */

    public void lastConnexion(String username) throws Exception {
        this.userRepository.findByUsernameAndActive(username, true).ifPresent((b) -> {
            b.setLastConnexion(Instant.now());
            b.setVersion(b.getVersion() + 1);
            b.setLastUpdate(Instant.now());
            this.userRepository.save(b);
        });
    }


    public User findOne(String username) {
        return userRepository.findByUsernameAndActive(username, true).orElseGet(() -> null);
    }


    public User findOne(Profile profile) {
        return userRepository.findByProfileAndActive(profile, true).orElseGet(() -> null);
    }


    public User findOne(Long id) {
        return userRepository.findById(id).orElseGet(() -> null);
    }

}
