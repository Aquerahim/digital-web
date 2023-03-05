package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.repositories.parametrage.SmsCredentialRepository;
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
public class SmsCredentialService {

    private final SmsCredentialRepository smsCredentialRepository;

    @Autowired
    public SmsCredentialService(SmsCredentialRepository smsCredentialRepository) {
        this.smsCredentialRepository = smsCredentialRepository;
    }

    public List<SmsCredential> findAlls() {
        return smsCredentialRepository.findAll();
    }

    public List<SmsCredential> findAll() {
        return smsCredentialRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public List<SmsCredential> findAllAffected() {
        return smsCredentialRepository.findAllByActiveAndAffected(true, true).orElseGet(ArrayList::new);
    }


    public List<SmsCredential> findAll(String senderId) {
        return smsCredentialRepository.findAllByActiveAndSenderId(true, senderId).orElseGet(ArrayList::new);
    }


    public SmsCredential findOne(String senderId) {
        return smsCredentialRepository.findByActiveAndSenderId(true, senderId).orElseGet(() -> null);
    }


    public SmsCredential findOne(Long id) {
        return smsCredentialRepository.findById(id).orElseGet(() -> null);
    }


    public SmsCredential create(SmsCredential input) {

        return smsCredentialRepository.findByActiveAndSenderId(true, input.getSenderId()).orElseGet(() -> {

            input.setAffected(true);

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return smsCredentialRepository.save(input);
        });
    }


    public SmsCredential update(SmsCredential upt) {

        smsCredentialRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {

            b.setUsername(upt.getUsername());

            b.setPassword(upt.getPassword());

            b.setSenderId(upt.getSenderId());

            b.setRef(upt.getRef());

            b.setNombreSmsLeTexto(upt.getNombreSmsLeTexto());

            b.setNombreSms(upt.getNombreSms());

            b.setToken(upt.getToken());

            b.setLastUpdate(Instant.now());

            smsCredentialRepository.save(b);
        });

        return upt;
    }


    public void affectedUpdate (long id){

        smsCredentialRepository.findById(id).ifPresent(model -> {

            model.setAffected(false);

            smsCredentialRepository.save(model);
        });
    }



    public void disable(long id) {

        smsCredentialRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            smsCredentialRepository.save(model);
        });
    }


    public void enable(long id) {

        smsCredentialRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            smsCredentialRepository.save(model);
        });
    }



    public SmsCredential updated(SmsCredential upt) {

        smsCredentialRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {

            upt.setLastUpdate(Instant.now());

            smsCredentialRepository.save(upt);
        });

        return upt;
    }
}
