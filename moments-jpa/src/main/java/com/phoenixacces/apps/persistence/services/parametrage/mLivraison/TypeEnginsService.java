package com.phoenixacces.apps.persistence.services.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typeengin.TypeEngins;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.TypeColisRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.TypeEnginsRepository;
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
public class TypeEnginsService {

    private final TypeEnginsRepository typeEnginsRepository;


    @Autowired
    public TypeEnginsService(TypeEnginsRepository typeEnginsRepository) {
        this.typeEnginsRepository = typeEnginsRepository;
    }

    public List<TypeEngins> findAlls() {

        return typeEnginsRepository.findAll();
    }


    public List<TypeEngins> findAll() {

        return typeEnginsRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public TypeEngins findOne(String typeEngins) {
        return typeEnginsRepository.findByTypeenginsAndActive(typeEngins, true).orElseGet(() -> null);
    }


    public TypeEngins findOne(Long id) {

        return typeEnginsRepository.findById(id).orElseGet(() -> null);
    }


    public TypeEngins create(TypeEngins input) throws Exception {

        return typeEnginsRepository.findByTypeenginsAndActive(input.getTypeengins().toUpperCase(), true).orElseGet(() -> {

            TypeEngins a = new TypeEngins();

            a.setTypeengins(input.getTypeengins().toUpperCase());

            a.setActive(true);

            a.setCreation(Instant.now());

            a.setLastUpdate(Instant.now());

            return typeEnginsRepository.save(a);
        });
    }
    

    public TypeEngins update(TypeEngins request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return typeEnginsRepository.save(request);
        }

        return request;
    }


    public void disable(long id) throws Exception {

        typeEnginsRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            typeEnginsRepository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        typeEnginsRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            typeEnginsRepository.save(model);
        });
    }
}
