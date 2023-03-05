package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.repositories.module.assurance.ProduitAssuranceRepository;
import com.phoenixacces.apps.persistence.repositories.module.assurance.TypeClientRepository;
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
public class ProduitAssuranceService {

    private final ProduitAssuranceRepository produitAssuranceRepository;

    @Autowired
    public ProduitAssuranceService(ProduitAssuranceRepository produitAssuranceRepository) {
        this.produitAssuranceRepository = produitAssuranceRepository;
    }

    public List<ProduitAssurance> findAlls() {

        return produitAssuranceRepository.findAll();
    }


    public List<ProduitAssurance> findAll() {

        return produitAssuranceRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public ProduitAssurance findOne(String type, String produit) {

        return produitAssuranceRepository.findByTypeAndProduitAndActive(type, produit, true).orElseGet(() -> null);
    }

    public ProduitAssurance findOne(String produit) {

        return produitAssuranceRepository.findByProduitAndActive(produit.toUpperCase(), true).orElseGet(() -> null);
    }

    public ProduitAssurance findById(Long id) {

        return produitAssuranceRepository.findById(id).orElseGet(() -> null);
    }

    public ProduitAssurance create(ProduitAssurance input) throws Exception {

        return produitAssuranceRepository.findByTypeAndProduitAndActive(input.getType().toUpperCase(), input.getProduit().toUpperCase(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return produitAssuranceRepository.save(input);
        });
    }


    public ProduitAssurance update(ProduitAssurance c) throws Exception {

        produitAssuranceRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            produitAssuranceRepository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        produitAssuranceRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            produitAssuranceRepository.save(model);
        });
    }
}
