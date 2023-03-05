package com.phoenixacces.apps.persistence.repositories.template;


import com.phoenixacces.apps.persistence.entities.template.Templates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface TemplatesRepository extends JpaRepository<Templates, Long> {

    Optional<List<Templates>> findAllByActive(boolean active);

    Optional<Templates> findByContentAndActiveAndType(String content, String type, boolean active);

    Optional<Templates> findByContentAndActive(String content, boolean active);

    Optional<Templates> findByCodeAndActive(String code, boolean active);

    Optional<Templates> findByTypeAndActive(String type, boolean active);

    Optional<Templates> findByIdAndActive(Long id, boolean active);
}
