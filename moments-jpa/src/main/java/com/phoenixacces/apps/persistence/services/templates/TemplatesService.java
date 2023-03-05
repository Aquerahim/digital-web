package com.phoenixacces.apps.persistence.services.templates;


import com.phoenixacces.apps.persistence.entities.template.Templates;
import com.phoenixacces.apps.persistence.repositories.template.TemplatesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TemplatesService {

    private final TemplatesRepository templatesRepository;

    @Autowired
    public TemplatesService(TemplatesRepository templatesRepository) {
        this.templatesRepository = templatesRepository;
    }


    public List<Templates> findAll() {
        return templatesRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public Templates findOne(String libelle) {
        return templatesRepository.findByContentAndActive(libelle, true).orElseGet(() -> {
            return null;
        });
    }


    public Templates find(String code) {
        return templatesRepository.findByCodeAndActive(code, true).orElseGet(() -> {
            return null;
        });
    }


    public Templates findByTemplateByType(String type) {
        return templatesRepository.findByTypeAndActive(type, true).orElseGet(() -> {
            return null;
        });
    }


    public Templates findById(Long Id) {
        return templatesRepository.findById(Id).orElseGet(() -> {
            return null;
        });
    }


    public Templates create(Templates e) throws Exception {
        return templatesRepository.findByContentAndActiveAndType(e.getContent(), e.getType(),true).orElseGet(() -> {
            e.setActive(true);
            e.setCreation(Instant.now());
            e.setLastUpdate(Instant.now());
            return templatesRepository.save(e);
        });
    }


    public Templates update(Templates model) throws Exception {
        templatesRepository.findByIdAndActive(model.getId(), true).ifPresent(b -> {
            b.setLastUpdate(Instant.now());
            b.setVersion(model.getVersion() + 1);
            templatesRepository.save(b);
        });

        return model;
    }


    public void disable(long id) throws Exception {
        templatesRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            templatesRepository.save(model);
        });
    }
}
