package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.CategorieBoisson;
import com.phoenixacces.apps.persistence.entities.module.depot.TypesPaiement;
import com.phoenixacces.apps.persistence.repositories.module.depot.CategorieBoissonRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.TypesPaiementRepository;
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
public class CategorieBoissonService {

    private final CategorieBoissonRepository categorieBoissonRepository;

    @Autowired
    public CategorieBoissonService(CategorieBoissonRepository categorieBoissonRepository) {
        this.categorieBoissonRepository = categorieBoissonRepository;
    }


    public List<CategorieBoisson> findAll() {

        return categorieBoissonRepository.findAll();
    }

    public List<CategorieBoisson> findAll(Boolean aBoolean) {

        return categorieBoissonRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public CategorieBoisson findOne(String name, Boolean aBoolean) {

        return categorieBoissonRepository.findByCategorieBoissonAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public CategorieBoisson findByOrdre(Long ordre) {

        return categorieBoissonRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public CategorieBoisson findById(Long id) {

        return categorieBoissonRepository.findById(id).orElseGet(() -> null);
    }

    public CategorieBoisson create(CategorieBoisson item) throws Exception {

        return categorieBoissonRepository.findByCategorieBoissonAndActive(item.getCategorieBoisson(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return categorieBoissonRepository.save(item);
        });
    }


    public CategorieBoisson update(CategorieBoisson donne) throws Exception {

        categorieBoissonRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            categorieBoissonRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        categorieBoissonRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            categorieBoissonRepository.save(model);
        });
    }
}
