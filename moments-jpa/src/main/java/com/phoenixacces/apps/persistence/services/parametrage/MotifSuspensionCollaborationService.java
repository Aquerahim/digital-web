package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.MotifSuspensionCollaboration;
import com.phoenixacces.apps.persistence.repositories.parametrage.MotifSuspensionCollaborationRepository;
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
public class MotifSuspensionCollaborationService {

    private final MotifSuspensionCollaborationRepository motifSuspensionCollaborationRepository;

    @Autowired
    public MotifSuspensionCollaborationService(MotifSuspensionCollaborationRepository motifSuspensionCollaborationRepository) {
        this.motifSuspensionCollaborationRepository = motifSuspensionCollaborationRepository;
    }

    public List<MotifSuspensionCollaboration> findAll() {
        return motifSuspensionCollaborationRepository.findAllByActive(true).orElseGet(() -> {
            return new ArrayList<>();
        });
    }

    public List<MotifSuspensionCollaboration> findAlls() {
        return motifSuspensionCollaborationRepository.findAll();
    }

    public MotifSuspensionCollaboration findOne(String motif) {
        return motifSuspensionCollaborationRepository.findByMotifAndActive(motif, true).orElseGet(() -> {
            return null;
        });
    }

    public MotifSuspensionCollaboration findOne(Long id) {
        return motifSuspensionCollaborationRepository.findById(id).orElseGet(() -> {
            return null;
        });
    }

    public MotifSuspensionCollaboration create(MotifSuspensionCollaboration input) throws Exception {
        return motifSuspensionCollaborationRepository.findByMotifAndActive(input.getMotif().toUpperCase(), true).orElseGet(() -> {
            MotifSuspensionCollaboration a = new MotifSuspensionCollaboration();
            a.setMotif(input.getMotif().toUpperCase());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return motifSuspensionCollaborationRepository.save(a);
        });
    }


    public MotifSuspensionCollaboration update(MotifSuspensionCollaboration upt) throws Exception {
        motifSuspensionCollaborationRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {
            //b.setMotif(upt.getMotif().toUpperCase());
            upt.setLastUpdate(Instant.now());
            motifSuspensionCollaborationRepository.save(upt);
        });
        return upt;
    }

    public void disable(long id) throws Exception {
        motifSuspensionCollaborationRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            motifSuspensionCollaborationRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        motifSuspensionCollaborationRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            motifSuspensionCollaborationRepository.save(model);
        });
    }
}
