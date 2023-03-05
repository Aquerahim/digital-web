package com.phoenixacces.apps.persistence.services.module;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.ServiceCourrierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class ServiceCourrierService {

    private final ServiceCourrierRepository serviceCourrierRepository;

    @Autowired
    public ServiceCourrierService(ServiceCourrierRepository serviceCourrierRepository) {
        this.serviceCourrierRepository = serviceCourrierRepository;
    }

    public List<ServiceCourrier> findAlls(Entreprises cie, boolean retrait) {
        return serviceCourrierRepository.findAllByGare_CompagnieAndActiveAndRetrait(cie, true, retrait).orElseGet(ArrayList::new);
    }

    public List<ServiceCourrier> findAlls() {
        return serviceCourrierRepository.findAll();
    }


    public List<ServiceCourrier> findAll(boolean retrait) {
        return serviceCourrierRepository.findAllByActiveAndRetrait(true, retrait).orElseGet(ArrayList::new);
    }


    public List<ServiceCourrier> findAll(Profile profile, boolean retrait) {
        return serviceCourrierRepository.findAllByActiveAndProfileAndRetrait(true, profile, retrait).orElseGet(ArrayList::new);
    }

    public ServiceCourrier findOne(String colisNumber, boolean retrait) {
        return serviceCourrierRepository.findByColisNumberAndRetrait(colisNumber,  retrait).orElseGet(() -> null);
    }

    public ServiceCourrier findOne(String colisNumber, Entreprises cie, boolean retrait) {
        return serviceCourrierRepository.findByColisNumberAndActiveAndGare_CompagnieAndRetrait(colisNumber, true, cie, retrait).orElseGet(() -> null);
    }

    public ServiceCourrier findOne(Profile profile, boolean retrait) {
        return serviceCourrierRepository.findByProfileAndActiveAndRetrait(profile, true, retrait).orElseGet(() -> null);
    }

    public ServiceCourrier findOne(Long id) {
        return serviceCourrierRepository.findById(id).orElseGet(() -> null);
    }


    public ServiceCourrier create(ServiceCourrier serviceCourrier) throws Exception {

        Random r = new Random();
        int low = 0;
        int high = 999999;
        int ordre = r.nextInt(high-low) + low;

        serviceCourrier.setCreation(Instant.now());
        serviceCourrier.setLastUpdate(Instant.now());
        serviceCourrier.setActive(true);
        serviceCourrier.setRetrait(false);
        serviceCourrier.setOrdre(ordre);
        serviceCourrier = serviceCourrierRepository.save(serviceCourrier);
        log.info("\t[ DI-GITAL :: SERVICE ] - ServiceCourrierService::create --- create new policy « {} »", serviceCourrierRepository);
        log.info("\t[ DI-GITAL :: SERVICE ] - ServiceCourrierService::create --- DONE");
        return serviceCourrier;
    }


    public ServiceCourrier update(ServiceCourrier request) {
        if (request.getId() != null) {
            request.setLastUpdate(Instant.now());
            return serviceCourrierRepository.save(request);
        }
        return request;
    }

    public void disable(long id) throws Exception {
        serviceCourrierRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            serviceCourrierRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        serviceCourrierRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            serviceCourrierRepository.save(model);
        });
    }


    public List<ServiceCourrier> findOne(String ref, String nomDest, String telDest) {

        List<ServiceCourrier> rep = null;

        if(ref != null && !ref.equals("")) {

            if (nomDest != null && !nomDest.equals("")) {

                if (telDest != null && !telDest.equals("")) {

                    // ref = 1 / nomDest = 1 / telDest = 1
                    rep = serviceCourrierRepository.findByColisNumberAndNomDestinataireAndPhoneDestinatireAndActiveAndRetrait(ref, nomDest, telDest, true, false).orElseGet(() -> null);
                }

                // ref = 1 / nomDest = 1 / phone = 0
                rep = serviceCourrierRepository.findByColisNumberAndNomDestinataireAndActiveAndRetrait(ref, nomDest, true, false).orElseGet(() -> null);
            }

            if (telDest != null && !telDest.equals("")) {

                // ref = 1 / nomDest = 0 / telDest = 1
                rep = serviceCourrierRepository.findByColisNumberAndPhoneDestinatireAndActiveAndRetrait(ref, telDest, true, false).orElseGet(() -> null);
            }

            // ref = 1 / nomDest = 0 / telDest = 0
            rep = serviceCourrierRepository.findByColisNumberAndActiveAndRetrait(ref, true, false).orElseGet(() -> null);
        }

        if (telDest != null && !telDest.equals("")) {

            if (nomDest != null && !nomDest.equals("")) {

                // ref = 0 / nomDest = 1 / telDest = 1
                rep = serviceCourrierRepository.findByNomDestinataireAndPhoneDestinatireAndActiveAndRetrait(nomDest, telDest, true, false).orElseGet(() -> null);
            }

            // ref = 0 / nomDest = 0 / telDest = 1
            rep = serviceCourrierRepository.findAllByPhoneDestinatireAndActiveAndRetrait(telDest, true, false).orElseGet(() -> null);
        }

        if (nomDest != null && !nomDest.equals("")) {

            rep = serviceCourrierRepository.findAllByNomDestinataire(nomDest).orElseGet(() -> null);
        }

        return rep;
    }
}
