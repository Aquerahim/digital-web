package com.phoenixacces.apps.persistence.services.module.livraison;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import com.phoenixacces.apps.persistence.repositories.module.livraison.LivreursRepository;
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
public class LivreursService {

    private final LivreursRepository livreursRepository;

    @Autowired
    public LivreursService(LivreursRepository livreursRepository) {
        this.livreursRepository = livreursRepository;
    }


    public List<Livreurs> findAll(boolean disponible, Profile profile) {

        return livreursRepository.findAllByActiveAndDisponibleAndProfile(true, disponible, profile).orElseGet(ArrayList::new);
    }


    public List<Livreurs> findAll() {

        return livreursRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public List<Livreurs> findAlls() {

        return livreursRepository.findAll();
    }


    public List<Livreurs> findAll(Profile profile) {

        return livreursRepository.findAllByActiveAndProfile(true, profile).orElseGet(ArrayList::new);
    }


    public List<Livreurs> find(Profile profile) {

        return livreursRepository.findAllByProfile(profile).orElseGet(ArrayList::new);
    }


    public List<Livreurs> findAllLivreur(Profile profile, boolean active) {

        return livreursRepository.findAllByActiveAndProfile(active, profile).orElseGet(ArrayList::new);
    }

    public Livreurs findOne(String nomPrenoms, String Contact) {

        return livreursRepository.findByNomPrenomsAndContactAndActive(nomPrenoms, Contact, true).orElseGet(() -> null);
    }

    public Livreurs findOneByUsername(String username) {

        return livreursRepository.findByUsernameAndActive(username, true).orElseGet(() -> null);
    }


    public Livreurs findOne(String Contact) {

        return livreursRepository.findByContactAndActive(Contact, true).orElseGet(() -> null);
    }

    public Livreurs findOneByOndre(Long ordre) {

        return livreursRepository.findByOrdreAndActive(ordre, true).orElseGet(() -> null);
    }

    public Livreurs findOne(Long id) {

        return livreursRepository.findById(id).orElseGet(() -> null);
    }


    public Livreurs create(Livreurs input) throws Exception {

        return livreursRepository.findByNomPrenomsAndContactAndActive(input.getNomPrenoms().toUpperCase(), input.getContact(), true).orElseGet(() -> {

            input.setConnected(true);

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            input.setVersion(0);

            input.setDisponible(true);

            return livreursRepository.save(input);

        });
    }


    public Livreurs update(Livreurs request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            request.setVersion(request.getVersion() + 1);

            return livreursRepository.save(request);
        }

        return request;
    }


    public void disable(long id) throws Exception {

        livreursRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            livreursRepository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        livreursRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            livreursRepository.save(model);
        });
    }
}
