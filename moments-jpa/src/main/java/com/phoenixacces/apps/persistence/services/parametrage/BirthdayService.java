package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.repositories.parametrage.BirthdayRepository;
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
public class BirthdayService {

    private final BirthdayRepository birthdayRepository;

    @Autowired
    public BirthdayService(BirthdayRepository birthdayRepository) {
        this.birthdayRepository = birthdayRepository;
    }

    public List<Birthday> findAlls() {
        return birthdayRepository.findAll();
    }

    public List<Birthday> findAll() {
        return birthdayRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public Birthday findOne(String birthday) {
        return birthdayRepository.findByBirthdayAndActive(birthday, true).orElseGet(() -> null);
    }

    public Birthday findeOne(String anniv) {
        return birthdayRepository.findByAnniversaireuxAndActive(anniv, true).orElseGet(() -> null);
    }

    public Birthday findeOne(String birthDay, String anniversaireux, String annee) {

        return birthdayRepository.findByBirthdayAndActiveAndAnniversaireuxAndAnnee(birthDay,true, anniversaireux.toUpperCase(),annee).orElseGet(() -> null);
    }

    public Birthday findOne(Long id) {

        return birthdayRepository.findById(id).orElseGet(() -> null);
    }

    public Birthday create(Birthday input) throws Exception {

        return birthdayRepository.findByBirthdayAndActiveAndAnniversaireuxAndAnnee(input.getBirthday().toUpperCase(),

                true, input.getAnniversaireux(), input.getAnnee().toUpperCase()).orElseGet(() -> {

            Birthday a = new Birthday();

            a.setBirthday(input.getBirthday().toUpperCase());

            a.setAnniversaireux(input.getAnniversaireux());

            a.setAnnee(input.getAnnee().toUpperCase());

            a.setActive(true);

            a.setCreation(Instant.now());

            a.setLastUpdate(Instant.now());

            return birthdayRepository.save(a);
        });
    }


    public Birthday update(Birthday upt) throws Exception {

        birthdayRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {

            /*b.setBirthday(upt.getBirthday().toUpperCase());

            b.setAnniversaireux(upt.getAnniversaireux());

            b.setAnnee(upt.getAnnee().toUpperCase());*/

            upt.setLastUpdate(Instant.now());

            upt.setVersion(upt.getVersion()+1);

            birthdayRepository.save(upt);
        });

        return upt;
    }

    public void disable(long id) throws Exception {

        birthdayRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            birthdayRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {

        birthdayRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            birthdayRepository.save(model);
        });
    }

    /*
    public Request update(Request request) {
        if (request.getId() != null) {
            request.setLastUpdate(Instant.now());
            return requestRepository.save(request);
        }
        return request;
    }
     */
}
