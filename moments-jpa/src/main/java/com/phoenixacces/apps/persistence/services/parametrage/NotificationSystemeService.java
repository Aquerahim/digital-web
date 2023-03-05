package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import com.phoenixacces.apps.persistence.repositories.parametrage.NotificationSystemeRepository;
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
public class NotificationSystemeService {

    private final NotificationSystemeRepository notificationRepository;

    @Autowired
    public NotificationSystemeService(NotificationSystemeRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    Instant instant = Instant.now();


    public List<NotificationSysteme> findAll(TypeNotification typeNotification) {
        return notificationRepository.findAllByActiveAndType(true, typeNotification).orElseGet(() -> new ArrayList<>());
    }


    public List<NotificationSysteme> findAll() {
        return notificationRepository.findAllByActive(true).orElseGet(() -> new ArrayList<>());
    }



    public List<NotificationSysteme> findAll(Profile profile) {
        return notificationRepository.findAllByActiveAndProfileOrderByIdDesc(true, profile).orElseGet(() -> new ArrayList<>());
    }


    public NotificationSysteme findOne(Long id) {
        return notificationRepository.findById(id).orElseGet(() -> null);
    }


    public NotificationSysteme create(NotificationSysteme input) throws Exception {

        return notificationRepository.findByActiveAndReference(true, input.getReference().toUpperCase()).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(instant);

            input.setLastUpdate(instant);

            return notificationRepository.save(input);
        });
    }


    public NotificationSysteme update(NotificationSysteme upt) throws Exception {

        notificationRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {

            upt.setLastUpdate(instant);

            notificationRepository.save(upt);
        });

        return upt;
    }

    public void disable(long id) throws Exception {

        notificationRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            model.setLastUpdate(instant);

            notificationRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {

        notificationRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            model.setLastUpdate(instant);

            notificationRepository.save(model);
        });
    }
}
