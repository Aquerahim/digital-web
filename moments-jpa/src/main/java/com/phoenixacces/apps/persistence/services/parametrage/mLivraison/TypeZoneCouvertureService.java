package com.phoenixacces.apps.persistence.services.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.TypeZoneCouvertureRepository;
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
public class TypeZoneCouvertureService {

    private final TypeZoneCouvertureRepository typeZoneCouvertureRepository;


    @Autowired
    public TypeZoneCouvertureService(TypeZoneCouvertureRepository typeZoneCouvertureRepository) {
        this.typeZoneCouvertureRepository = typeZoneCouvertureRepository;
    }

    public List<TypeZoneCouverture> findAlls() {

        return typeZoneCouvertureRepository.findAll();
    }

    public List<TypeZoneCouverture> findAll() {

        return typeZoneCouvertureRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public TypeZoneCouverture findOne(String suivi) {
        return typeZoneCouvertureRepository.findByTypezoneAndActive(suivi, true).orElseGet(() -> null);
    }

    public TypeZoneCouverture findOne(Long id) {

        return typeZoneCouvertureRepository.findById(id).orElseGet(() -> null);
    }

    public TypeZoneCouverture create(TypeZoneCouverture input) throws Exception {

        return typeZoneCouvertureRepository.findByTypezoneAndActive(input.getTypezone().toUpperCase(), true).orElseGet(() -> {

            TypeZoneCouverture a = new TypeZoneCouverture();

            a.setTypezone(input.getTypezone().toUpperCase());

            a.setActive(true);

            a.setCreation(Instant.now());

            a.setLastUpdate(Instant.now());

            return typeZoneCouvertureRepository.save(a);
        });
    }

    public TypeZoneCouverture update(TypeZoneCouverture request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return typeZoneCouvertureRepository.save(request);
        }

        return request;
    }


    public void disable(long id) throws Exception {

        typeZoneCouvertureRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            typeZoneCouvertureRepository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        typeZoneCouvertureRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            typeZoneCouvertureRepository.save(model);
        });
    }
}
