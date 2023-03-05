package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.PosteOccupe;
import com.phoenixacces.apps.persistence.entities.module.depot.Produit;
import com.phoenixacces.apps.persistence.repositories.module.depot.PosteOccupeRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.ProduitRepository;
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
public class ProduitService {

    private final ProduitRepository produitRepository;

    @Autowired
    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }


    public List<Produit> findAll() {

        return produitRepository.findAll();
    }

    public List<Produit> findAll(Boolean aBoolean) {

        return produitRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public Produit findOne(String name, Boolean aBoolean) {

        return produitRepository.findByNomBoissonAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public Produit findByOrdre(Long ordre) {

        return produitRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Produit findById(Long id) {

        return produitRepository.findById(id).orElseGet(() -> null);
    }

    public Produit create(Produit item) throws Exception {

        return produitRepository.findByNomBoissonAndActive(item.getNomBoisson(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return produitRepository.save(item);
        });
    }


    public Produit update(Produit donne) throws Exception {

        produitRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            produitRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        produitRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            produitRepository.save(model);
        });
    }
}
