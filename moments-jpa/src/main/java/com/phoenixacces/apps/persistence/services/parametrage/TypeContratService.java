package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypeContratRepository;
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
public class TypeContratService {

    private final TypeContratRepository typeContratRepository;

    @Autowired
    public TypeContratService(TypeContratRepository typeContratRepository) {
        this.typeContratRepository = typeContratRepository;
    }

    public List<TypeContrat> findAlls() {
        return typeContratRepository.findAll();
    }


    public List<TypeContrat> findAll(String module) {
        return typeContratRepository.findAllByActiveAndModule(true, module).orElseGet(() -> new ArrayList<>());
    }


    public List<TypeContrat> findAll() {
        return typeContratRepository.findAllByActive(true).orElseGet(() -> new ArrayList<>());
    }


    public TypeContrat findOne(String typeContrat) {
        return typeContratRepository.findByTypecontratAndActive(typeContrat, true).orElseGet(() -> null);
    }


    public TypeContrat findOne(Long id) {
        return typeContratRepository.findById(id).orElseGet(() -> null);
    }


    public TypeContrat create(TypeContrat input) throws Exception {
        return typeContratRepository.findByTypecontratAndActive(input.getTypecontrat().toUpperCase(), true).orElseGet(() -> {
            TypeContrat a = new TypeContrat();
            a.setTypecontrat(input.getTypecontrat().toUpperCase());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return typeContratRepository.save(a);
        });
    }


    public TypeContrat update(TypeContrat upt) throws Exception {
        typeContratRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {
            upt.setLastUpdate(Instant.now());
            typeContratRepository.save(upt);
        });
        return upt;
    }

    public void disable(long id) throws Exception {
        typeContratRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            typeContratRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        typeContratRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            typeContratRepository.save(model);
        });
    }
}
