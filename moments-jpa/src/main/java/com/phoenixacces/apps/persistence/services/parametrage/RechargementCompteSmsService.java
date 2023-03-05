package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.RechargementCompteSms;
import com.phoenixacces.apps.persistence.entities.parametrage.TypePaiement;
import com.phoenixacces.apps.persistence.repositories.parametrage.RechargementCompteSmsRepository;
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
public class RechargementCompteSmsService {

    private final RechargementCompteSmsRepository rechargementCompteSmsRepository;
    private final MotifSuspensionCollaborationService motifSuspensionCollaborationService;

    @Autowired
    public RechargementCompteSmsService(
            RechargementCompteSmsRepository rechargementCompteSmsRepository,
            MotifSuspensionCollaborationService motifSuspensionCollaborationService
    ) {
        this.rechargementCompteSmsRepository        = rechargementCompteSmsRepository;
        this.motifSuspensionCollaborationService    = motifSuspensionCollaborationService;
    }


    public List<RechargementCompteSms> findAll() {
        return rechargementCompteSmsRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<RechargementCompteSms> findAll(Profile profile) {
        return rechargementCompteSmsRepository.findAllByActiveAndProfile(true, profile).orElseGet(ArrayList::new);
    }

    public List<RechargementCompteSms> findOneByRefPaiement(String refPaiement) {

        return rechargementCompteSmsRepository.findAllByActiveAndStatutAndRefPaiement(true, EtatLecture.PENDING, refPaiement).orElseGet(() -> null);
    }

    public List<RechargementCompteSms> findAllByNumPayeur(String numPayeur) {

        return rechargementCompteSmsRepository.findAllByActiveAndStatutAndNumPayeur(true, EtatLecture.PENDING, numPayeur).orElseGet(() -> null);
    }


    public List<RechargementCompteSms> findAll(Entreprises entreprises) {
        return rechargementCompteSmsRepository.findAllByActiveAndEntreprises(true, entreprises).orElseGet(ArrayList::new);
    }

    public RechargementCompteSms findOne(String refPaiement) {
        return rechargementCompteSmsRepository.findByRefPaiementAndActive(refPaiement, true).orElseGet(() -> null);
    }

    public RechargementCompteSms findOne(Long id) {
        return rechargementCompteSmsRepository.findByIdAndActive(id, true).orElseGet(() -> null);
    }

    public RechargementCompteSms create(RechargementCompteSms input) {

        input.setActive(true);

        input.setStatut(EtatLecture.PENDING);

        input.setMotif(motifSuspensionCollaborationService.findOne(7L));

        input.setCreation(Instant.now());

        input.setLastUpdate(input.getCreation());

        return rechargementCompteSmsRepository.save(input);
    }


    public RechargementCompteSms update(RechargementCompteSms c) {

        c.setLastUpdate(Instant.now());

        return rechargementCompteSmsRepository.save(c);

    }
}
