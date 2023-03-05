package com.phoenixacces.apps.persistence.services.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.module.livraison.Historique;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison.HistoriqueRepository;
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
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;


    @Autowired
    public HistoriqueService(HistoriqueRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }


    public List<Historique> findAlls() {

        return historiqueRepository.findAll();
    }


    public List<Historique> findAll() {

        return historiqueRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<Historique> findAll(Livraisons livraisons) {

        return historiqueRepository.findAllByActiveAndLivraisonsOrderByIdDesc(true, livraisons).orElseGet(ArrayList::new);
    }


    public List<Historique> findAll(FicheBonDeCommande cmde) {

        return historiqueRepository.findAllByActiveAndFicheBonDeCommandeOrderByIdDesc(true, cmde).orElseGet(ArrayList::new);
    }


    public List<Historique> findAll(Long ordre) {

        return historiqueRepository.findAllByActiveAndOrdreOrderByIdDesc(true, ordre).orElseGet(ArrayList::new);
    }

    public Historique create(Historique input) throws Exception {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return historiqueRepository.save(input);
    }
}
