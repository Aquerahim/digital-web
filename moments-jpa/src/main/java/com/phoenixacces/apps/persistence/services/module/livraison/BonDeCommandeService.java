package com.phoenixacces.apps.persistence.services.module.livraison;

import com.phoenixacces.apps.persistence.entities.module.Colis;
import com.phoenixacces.apps.persistence.entities.module.livraison.BonDeCommande;
import com.phoenixacces.apps.persistence.repositories.module.ColisRepository;
import com.phoenixacces.apps.persistence.repositories.module.livraison.BonDeCommandeRepository;
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
public class BonDeCommandeService {

    private final BonDeCommandeRepository bonDeCommandeRepository;

    @Autowired
    public BonDeCommandeService(BonDeCommandeRepository bonDeCommandeRepository) {
        this.bonDeCommandeRepository = bonDeCommandeRepository;
    }


    public List<BonDeCommande> findAll() {

        return bonDeCommandeRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public List<BonDeCommande> findAll(Long ordre) {

        return bonDeCommandeRepository.findAllByOrdreAndActive(ordre, true).orElseGet(ArrayList::new);
    }

    public BonDeCommande findOne(Long ordre) {

        return bonDeCommandeRepository.findByOrdreAndActive(ordre, true).orElseGet(() -> null);
    }

    public BonDeCommande findById(Long id) {

        return bonDeCommandeRepository.findByIdAndActive(id, true).orElseGet(() -> null);
    }


    public BonDeCommande update(BonDeCommande request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            request.setVersion(request.getVersion()+1);

            return bonDeCommandeRepository.save(request);
        }
        return request;
    }

    public void disable(long id) throws Exception {

        bonDeCommandeRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            bonDeCommandeRepository.save(model);
        });
    }


    public void enable(long id) throws Exception {

        bonDeCommandeRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            bonDeCommandeRepository.save(model);
        });
    }

    public BonDeCommande create (BonDeCommande input) throws Exception {

        return bonDeCommandeRepository.findByOrdreAndActiveAndNatureColisAndNomProduit(input.getOrdre(),

                true, input.getNatureColis(), input.getNomProduit().toUpperCase()).orElseGet(() -> {

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            input.setVersion(0);

            input.setActive(true);

            return bonDeCommandeRepository.save(input);
        });
    }
}
