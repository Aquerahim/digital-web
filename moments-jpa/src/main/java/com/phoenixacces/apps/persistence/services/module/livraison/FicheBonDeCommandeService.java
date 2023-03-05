package com.phoenixacces.apps.persistence.services.module.livraison;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import com.phoenixacces.apps.persistence.repositories.module.livraison.FicheBonDeCommandeRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.SuiviDemandeRepository;
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
public class FicheBonDeCommandeService {

    private final FicheBonDeCommandeRepository ficheBonDeCommandeRepository;
    private final SuiviDemandeRepository suiviDemandeRepository;

    @Autowired
    public FicheBonDeCommandeService(FicheBonDeCommandeRepository ficheBonDeCommandeRepository,
                                     SuiviDemandeRepository suiviDemandeRepository) {
        this.ficheBonDeCommandeRepository   = ficheBonDeCommandeRepository;
        this.suiviDemandeRepository         = suiviDemandeRepository;
    }


    public List<FicheBonDeCommande> findAll() {

        return ficheBonDeCommandeRepository.findAllByActiveOrderByIdDesc(true).orElseGet(ArrayList::new);
    }


    public List<FicheBonDeCommande> findAll(PartenaireAffaire mesClients) {

        return ficheBonDeCommandeRepository.findAllByActiveAndPartenaireOrderByIdDesc(true, mesClients).orElseGet(ArrayList::new);
    }


    public List<FicheBonDeCommande> findAll(Profile profile) {

        return ficheBonDeCommandeRepository.findAllByActiveAndProfileOrderByIdDesc(true, profile).orElseGet(ArrayList::new);
    }


    public List<FicheBonDeCommande> findAll(Entreprises entreprise) {

        return ficheBonDeCommandeRepository.findAllByActiveAndEntrepriseOrderByIdDesc(true, entreprise).orElseGet(ArrayList::new);
    }


    public List<FicheBonDeCommande> findAll(Entreprises entreprise, SuiviDemande suiviDemande) {

        return ficheBonDeCommandeRepository.findAllByActiveAndEntrepriseAndSuiviDemandeOrderByIdDesc(true, entreprise, suiviDemande).orElseGet(ArrayList::new);
    }


    public List<FicheBonDeCommande> findAll(SuiviDemande suiviDemande) {

        return ficheBonDeCommandeRepository.findAllByActiveAndSuiviDemandeOrderByIdDesc(true, suiviDemande).orElseGet(ArrayList::new);
    }


    public FicheBonDeCommande findOne(Long numOrdre) {

        return ficheBonDeCommandeRepository.findByOrdreAndActive(numOrdre, true).orElseGet(() -> null);
    }


    public FicheBonDeCommande findOne(String ref) {

        return ficheBonDeCommandeRepository.findByReferenceAndActive(ref, true).orElseGet(() -> null);
    }


    public FicheBonDeCommande create(FicheBonDeCommande e) throws Exception {

        return ficheBonDeCommandeRepository.findByReferenceAndActive(e.getReference().toUpperCase(), true).orElseGet(() -> {

            e.setActive(true);

            e.setSuiviDemande(suiviDemandeRepository.getOne(5L));

            e.setNotification(true);

            e.setCreation(Instant.now());

            e.setLastUpdate(Instant.now());

            return ficheBonDeCommandeRepository.save(e);

        });
    }


    public FicheBonDeCommande update(FicheBonDeCommande request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            request.setVersion(request.getVersion()+1);

            return ficheBonDeCommandeRepository.save(request);

        }

        return request;
    }


    public void disable(long id) throws Exception {

        ficheBonDeCommandeRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            ficheBonDeCommandeRepository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        ficheBonDeCommandeRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            ficheBonDeCommandeRepository.save(model);

        });
    }
}
