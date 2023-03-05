package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.TypesPaiement;
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
public class TypesPaiementService {

    private final TypesPaiementRepository typePaiementRepository;

    @Autowired
    public TypesPaiementService(TypesPaiementRepository typePaiementRepository) {
        this.typePaiementRepository = typePaiementRepository;
    }


    public List<TypesPaiement> findAll() {

        return typePaiementRepository.findAll();
    }

    public List<TypesPaiement> findAll(Boolean aBoolean) {

        return typePaiementRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public TypesPaiement findOne(String name, Boolean aBoolean) {

        return typePaiementRepository.findByTypePaiementAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public TypesPaiement findByOrdre(Long ordre) {

        return typePaiementRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public TypesPaiement findById(Long id) {

        return typePaiementRepository.findById(id).orElseGet(() -> null);
    }

    public TypesPaiement create(TypesPaiement item) throws Exception {

        return typePaiementRepository.findByTypePaiementAndActive(item.getTypePaiement(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return typePaiementRepository.save(item);
        });
    }


    public TypesPaiement update(TypesPaiement donne) throws Exception {

        typePaiementRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            typePaiementRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        typePaiementRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            typePaiementRepository.save(model);
        });
    }
}
