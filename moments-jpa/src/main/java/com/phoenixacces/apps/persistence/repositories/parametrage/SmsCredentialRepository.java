package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface SmsCredentialRepository extends JpaRepository<SmsCredential, Long> {

    Optional<SmsCredential> findByIdAndActive(Long id, boolean active);

    Optional<List<SmsCredential>> findAllByActive(boolean active);

    Optional<List<SmsCredential>> findAllByActiveAndAffected(boolean active, boolean affected);

    Optional<List<SmsCredential>> findAllByActiveAndSenderId(boolean active, String senderId);

    Optional<SmsCredential> findByUsernameAndActiveAndSenderId(String label, boolean active, String senderId);

    Optional<SmsCredential> findByActiveAndSenderId(boolean active, String senderId);
}
