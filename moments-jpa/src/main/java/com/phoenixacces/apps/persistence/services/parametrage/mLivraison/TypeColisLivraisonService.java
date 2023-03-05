package com.phoenixacces.apps.persistence.services.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.TypeColisRepository;
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
public class TypeColisLivraisonService {

    private final TypeColisRepository typeColisRepository;


    @Autowired
    public TypeColisLivraisonService(TypeColisRepository typeColisRepository) {
        this.typeColisRepository = typeColisRepository;
    }

    public List<NatureColis> findAlls() {

        return typeColisRepository.findAll();
    }

    public List<NatureColis> findAll() {

        return typeColisRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public NatureColis findOne(String typeColis) {
        return typeColisRepository.findByTypecolisAndActive(typeColis, true).orElseGet(() -> null);
    }

    public NatureColis findOne(Long id) {

        return typeColisRepository.findById(id).orElseGet(() -> null);
    }

    public NatureColis create(NatureColis input) throws Exception {

        return typeColisRepository.findByTypecolisAndActive(input.getTypecolis().toUpperCase(), true).orElseGet(() -> {

            NatureColis a = new NatureColis();

            a.setTypecolis(input.getTypecolis().toUpperCase());

            a.setActive(true);

            a.setCreation(Instant.now());

            a.setLastUpdate(Instant.now());

            return typeColisRepository.save(a);
        });
    }

    public NatureColis update(NatureColis request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return typeColisRepository.save(request);
        }

        return request;
    }


    public void disable(long id) throws Exception {

        typeColisRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            typeColisRepository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        typeColisRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            typeColisRepository.save(model);
        });
    }
}
