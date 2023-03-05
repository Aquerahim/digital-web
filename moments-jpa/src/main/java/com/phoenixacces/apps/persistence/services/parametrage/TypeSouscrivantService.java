package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.TypeSouscrivant;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypeSouscrivantRepository;
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
public class TypeSouscrivantService {

    private final TypeSouscrivantRepository typeSouscrivantRepository;

    @Autowired
    public TypeSouscrivantService(TypeSouscrivantRepository typeSouscrivantRepository) {
        this.typeSouscrivantRepository = typeSouscrivantRepository;
    }

    public List<TypeSouscrivant> findAlls() {
        return typeSouscrivantRepository.findAll();
    }


    public List<TypeSouscrivant> findAll() {
        return typeSouscrivantRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public TypeSouscrivant findOne(String name) {
        return typeSouscrivantRepository.findByTypesouscrivantAndActive(name, true).orElseGet(() -> null);
    }

    public TypeSouscrivant findById(Long id) {
        return typeSouscrivantRepository.findById(id).orElseGet(() -> null);
    }

    public TypeSouscrivant create(TypeSouscrivant input) {
        return typeSouscrivantRepository.findByTypesouscrivantAndActive(input.getTypesouscrivant().toUpperCase(), true).orElseGet(() -> {
            TypeSouscrivant a = new TypeSouscrivant();
            a.setTypesouscrivant(input.getTypesouscrivant().toUpperCase());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return typeSouscrivantRepository.save(a);
        });
    }


    public TypeSouscrivant update(TypeSouscrivant c) {
        typeSouscrivantRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {
            //b.setTypesouscrivant(c.getTypesouscrivant().toUpperCase());
            c.setLastUpdate(Instant.now());
            typeSouscrivantRepository.save(c);
        });
        return c;
    }

    public void disable(long id) {
        typeSouscrivantRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            typeSouscrivantRepository.save(model);
        });
    }

    public void enable(long id) {
        typeSouscrivantRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            typeSouscrivantRepository.save(model);
        });
    }
}
