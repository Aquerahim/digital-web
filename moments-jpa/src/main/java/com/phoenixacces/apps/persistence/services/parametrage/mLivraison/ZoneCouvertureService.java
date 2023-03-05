package com.phoenixacces.apps.persistence.services.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.ZoneCouvertureRepository;
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
public class ZoneCouvertureService {

    private final ZoneCouvertureRepository zoneCouvertureRepository;

    @Autowired
    public ZoneCouvertureService(ZoneCouvertureRepository zoneCouvertureRepository) {
        this.zoneCouvertureRepository = zoneCouvertureRepository;
    }

    public List<ZoneCouverture> findAlls() {

        return zoneCouvertureRepository.findAll();
    }

    public List<ZoneCouverture> findAll() {

        return zoneCouvertureRepository.findAllByActiveOrderByZoneCouvertureAsc(true).orElseGet(ArrayList::new);
    }

    public List<ZoneCouverture> findAll(TypeZoneCouverture typeCouverture) {

        return zoneCouvertureRepository.findAllByActiveAndTypeZoneCouverture(true, typeCouverture).orElseGet(ArrayList::new);
    }

    public ZoneCouverture findOne(String zone, TypeZoneCouverture typeZoneCouverture) {

        return zoneCouvertureRepository.findByZoneCouvertureAndTypeZoneCouvertureAndActive(zone, typeZoneCouverture,true).orElseGet(() -> null);
    }

    public ZoneCouverture findOne(Long id) {

        return zoneCouvertureRepository.findById(id).orElseGet(() -> null);
    }

    public ZoneCouverture create(ZoneCouverture input) throws Exception {

        /*return zoneCouvertureRepository.findByZoneCouvertureAndTypeZoneCouvertureAndActive(input.getZoneCouverture().toUpperCase(), input.getTypeZoneCouverture(), true).orElseGet(() -> {

            ZoneCouverture a = new ZoneCouverture();

            a.setZoneCouverture(input.getZoneCouverture().toUpperCase());

            a.setTypeZoneCouverture(input.getTypeZoneCouverture());

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return zoneCouvertureRepository.save(input);
        });*/

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return zoneCouvertureRepository.save(input);
    }

    public ZoneCouverture update(ZoneCouverture request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            return zoneCouvertureRepository.save(request);
        }
        return request;
    }


    public void disable(long id) throws Exception {

        zoneCouvertureRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            zoneCouvertureRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {

        zoneCouvertureRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            zoneCouvertureRepository.save(model);
        });
    }
}
