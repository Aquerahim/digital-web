package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.TypePaiement;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypePaiementRepository;
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
public class TypePaiementService {

    private final TypePaiementRepository typePaiementRepository;

    @Autowired
    public TypePaiementService(TypePaiementRepository typePaiementRepository) {
        this.typePaiementRepository = typePaiementRepository;
    }



    public List<TypePaiement> findAll() {
        return typePaiementRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public TypePaiement findOne(String name) {
        return typePaiementRepository.findByTypepaiementAndActive(name, true).orElseGet(() -> null);
    }

    public TypePaiement findById(Long id) {
        return typePaiementRepository.findById(id).orElseGet(() -> null);
    }

    public TypePaiement create(TypePaiement input) {

        return typePaiementRepository.findByTypepaiementAndActive(input.getTypepaiement().toUpperCase(), true).orElseGet(() -> {

            TypePaiement a = new TypePaiement();

            a.setTypepaiement(input.getTypepaiement().toUpperCase());

            a.setActive(true);

            a.setCreation(Instant.now());

            a.setLastUpdate(Instant.now());

            return typePaiementRepository.save(a);
        });
    }


    public TypePaiement update(TypePaiement c) {
        typePaiementRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {
            c.setLastUpdate(Instant.now());
            typePaiementRepository.save(c);
        });
        return c;
    }

    public void disable(long id) {
        typePaiementRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            typePaiementRepository.save(model);
        });
    }

    public void enable(long id) {
        typePaiementRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            typePaiementRepository.save(model);
        });
    }
}
