package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.enumerations.TypeClientLivraison;
import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import com.phoenixacces.apps.persistence.entities.module.livraison.Clients;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.PorteFeuilleClientRepository;
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
public class PorteFeuilleClientService {

    private final PorteFeuilleClientRepository repository;

    @Autowired
    public PorteFeuilleClientService(PorteFeuilleClientRepository repository) {
        this.repository = repository;
    }


    public List<PorteFeuilleClient> findAlls() {

        return repository.findAll();
    }


    public List<PorteFeuilleClient> findAll(Entreprises entreprise) {

        return repository.findAllByActiveAndEntreprises(true, entreprise).orElseGet(ArrayList::new);
    }


    public PorteFeuilleClient findOne(String nomClient, ProduitAssurance produitAssurance, Entreprises entreprises) {

        return repository.findByNomClientAndActiveAndProduitAndEntreprises(nomClient, true, produitAssurance, entreprises).orElseGet(() -> null);
    }


    public PorteFeuilleClient findOne(Long id) {

        return repository.findByActiveAndId(true, id).orElseGet(() -> null);
    }

    public PorteFeuilleClient create(PorteFeuilleClient input) {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return repository.save(input);
    }

    public PorteFeuilleClient update(PorteFeuilleClient request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return repository.save(request);
        }

        return request;
    }


    public void actionUser(long id, boolean statut) {

        repository.findById(id).ifPresent(model -> {

            model.setActive(statut);

            repository.save(model);

        });
    }
}
