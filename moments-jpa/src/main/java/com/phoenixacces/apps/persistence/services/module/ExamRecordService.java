package com.phoenixacces.apps.persistence.services.module;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.repositories.module.livraison.FicheBonDeCommandeRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.SuiviDemandeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ExamRecordService {

    private final FicheBonDeCommandeRepository ficheRepo;

    @Autowired
    public ExamRecordService(FicheBonDeCommandeRepository ficheRepo) {
        this.ficheRepo   = ficheRepo;
    }


    public List<FicheBonDeCommande> getAllRecords() {

        return ficheRepo.findAllByActiveOrderByIdDesc(true).orElseGet(ArrayList::new);
    }
}
