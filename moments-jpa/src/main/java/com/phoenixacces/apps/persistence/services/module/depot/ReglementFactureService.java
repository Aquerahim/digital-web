package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.module.depot.ReceptionFacture;
import com.phoenixacces.apps.persistence.repositories.module.depot.ReceptionFactureRepository;
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
public class ReglementFactureService {

    private final ReceptionFactureRepository receptionFactureRepository;

    @Autowired
    public ReglementFactureService(ReceptionFactureRepository receptionFactureRepository) {
        this.receptionFactureRepository = receptionFactureRepository;
    }


    public List<ReceptionFacture> findAll() {

        return receptionFactureRepository.findAll();
    }
    

    public List<ReceptionFacture> findAll(Boolean aBoolean) {

        return receptionFactureRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public ReceptionFacture findOne(String name, Boolean aBoolean) {

        return receptionFactureRepository.findByNumeroFactureAndActive(name, aBoolean).orElseGet(() -> null);
    }


    public ReceptionFacture findOne(String name, Boolean aBoolean, Fournisseur fournisseur) {

        return receptionFactureRepository.findByNumeroFactureAndActiveAndFournisseur(name, aBoolean, fournisseur).orElseGet(() -> null);
    }

    public ReceptionFacture findByOrdre(Long ordre) {

        return receptionFactureRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public ReceptionFacture findById(Long id) {

        return receptionFactureRepository.findById(id).orElseGet(() -> null);
    }

    public ReceptionFacture create(ReceptionFacture item) throws Exception {

        return receptionFactureRepository.findByNumeroFactureAndActiveAndFournisseur(item.getNumeroFacture(), true, item.getFournisseur()).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return receptionFactureRepository.save(item);
        });
    }


    public ReceptionFacture update(ReceptionFacture donne) throws Exception {

        receptionFactureRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            receptionFactureRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        receptionFactureRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            receptionFactureRepository.save(model);
        });
    }
}
