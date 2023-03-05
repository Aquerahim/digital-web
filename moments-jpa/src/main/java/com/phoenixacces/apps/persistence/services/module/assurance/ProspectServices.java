package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.Groupe;
import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import com.phoenixacces.apps.persistence.entities.module.assurance.Prospect;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.PorteFeuilleClientRepository;
import com.phoenixacces.apps.persistence.repositories.module.assurance.ProspectRepository;
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
public class ProspectServices {

    private final ProspectRepository repository;

    @Autowired
    public ProspectServices(ProspectRepository repository) {
        this.repository = repository;
    }


    public List<Prospect> findAlls() {

        return repository.findAll();
    }


    public List<Prospect> findAll(Groupe groupe) {

        return repository.findAllByActiveAndGroupe(true, groupe).orElseGet(ArrayList::new);
    }


    public List<Prospect> findAll(Entreprises entreprise) {

        return repository.findAllByActiveAndEntreprises(true, entreprise).orElseGet(ArrayList::new);
    }


    public List<Prospect> findAll(Groupe groupe, Entreprises entreprise) {

        return repository.findAllByActiveAndGroupeAndEntreprises(true, groupe, entreprise).orElseGet(ArrayList::new);
    }


    public Prospect findOne(String nomClient, Groupe groupe, Entreprises entreprises) {

        return repository.findByNomPrenomsAndActiveAndGroupeAndEntreprises(nomClient, true, groupe, entreprises).orElseGet(() -> null);
    }


    public Prospect findOne(Long id) {

        return repository.findByActiveAndId(true, id).orElseGet(() -> null);
    }

    public Prospect create(Prospect input) {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return repository.save(input);
    }

    public Prospect update(Prospect request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return repository.save(request);
        }

        return request;
    }


    public void actionProspect(long id, boolean statut) {

        repository.findById(id).ifPresent(model -> {

            model.setActive(statut);

            repository.save(model);

        });
    }
}
