package com.phoenixacces.apps.persistence.services.module.livraison;
import com.phoenixacces.apps.persistence.entities.module.livraison.Clients;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.repositories.module.livraison.ClientsRepository;
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
public class ClientsService {

    private final ClientsRepository repository;

    @Autowired
    public ClientsService(ClientsRepository repository) {
        this.repository = repository;
    }


    public List<Clients> findAlls() {

        return repository.findAll();
    }

    public List<Clients> findAll(Long ordre) {

        return repository.findAllByOrdreAndActive(ordre, true).orElseGet(ArrayList::new);
    }

    public List<Clients> findAll() {

        return repository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<Clients> findAll(PartenaireAffaire mesClients) {

        return repository.findAllByActiveAndPartenaire(true, mesClients).orElseGet(ArrayList::new);
    }


    public Clients create(Clients input) throws Exception {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return repository.save(input);
    }

    public Clients update(Clients request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return repository.save(request);
        }
        return request;
    }
}
