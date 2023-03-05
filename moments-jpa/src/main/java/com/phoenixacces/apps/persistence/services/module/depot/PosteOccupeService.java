package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.MotifAvance;
import com.phoenixacces.apps.persistence.entities.module.depot.PosteOccupe;
import com.phoenixacces.apps.persistence.repositories.module.depot.MotifAvanceRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.PosteOccupeRepository;
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
public class PosteOccupeService {

    private final PosteOccupeRepository posteOccupeRepository;

    @Autowired
    public PosteOccupeService(PosteOccupeRepository posteOccupeRepository) {
        this.posteOccupeRepository = posteOccupeRepository;
    }


    public List<PosteOccupe> findAll() {

        return posteOccupeRepository.findAll();
    }

    public List<PosteOccupe> findAll(Boolean aBoolean) {

        return posteOccupeRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public PosteOccupe findOne(String name, Boolean aBoolean) {

        return posteOccupeRepository.findByPosteAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public PosteOccupe findByOrdre(Long ordre) {

        return posteOccupeRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public PosteOccupe findById(Long id) {

        return posteOccupeRepository.findById(id).orElseGet(() -> null);
    }

    public PosteOccupe create(PosteOccupe item) throws Exception {

        return posteOccupeRepository.findByPosteAndActive(item.getPoste(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return posteOccupeRepository.save(item);
        });
    }


    public PosteOccupe update(PosteOccupe donne) throws Exception {

        posteOccupeRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            posteOccupeRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        posteOccupeRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            posteOccupeRepository.save(model);
        });
    }
}
