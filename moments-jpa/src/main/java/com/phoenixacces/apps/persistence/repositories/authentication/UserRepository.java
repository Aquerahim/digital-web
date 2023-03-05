package com.phoenixacces.apps.persistence.repositories.authentication;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndActive(String username, boolean active);

    Optional<List<User>> findAllByActive(boolean active);

    Optional<List<User>> findAllByActiveAndProfile(boolean active, Profile profile);

    Optional<List<User>> findAllByActiveAndProfileIsNotNull(boolean active);

    Optional<List<User>> findAllByIdGreaterThanAndActive(long id, boolean active);

    Optional<User> findByIdAndActive(Long id, boolean active);

    Optional<User> findByUsernameAndRoles(String username, String roles);

    Optional<User> findByUsernameAndRolesAndActive(String username, String roles, boolean active);

    Optional<User> findByUsernameAndActiveAndDefaultPassword(String username, boolean active, String defaultpassword);

    Optional<User> findByProfileAndActive(Profile profile, boolean active);

    Optional<List<User>> findAllByProfileGareRoutiereGareRoutiere(Entreprises entreprises);

    @Query("SELECT coalesce(max(user.id), 0) FROM User user")
    Long getMaxId();

}
