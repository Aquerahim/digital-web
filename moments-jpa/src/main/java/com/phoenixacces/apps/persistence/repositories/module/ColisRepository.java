package com.phoenixacces.apps.persistence.repositories.module;

import com.phoenixacces.apps.persistence.entities.module.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ColisRepository extends JpaRepository<Colis, Long> {

    Optional<Colis> findByIdAndActive(Long id, boolean active);

    Optional<List<Colis>> findAllByActive(boolean active);

    Optional<List<Colis>> findAllByColisNumberAndActive(String numeroColis, boolean active);

    Optional<Colis> findByColisNumberAndActiveAndNatureColisAndDesignationColis(String numeroColis, boolean active,
                                                                                String nature, String designation);

    Optional<Colis> findByColisNumberAndActive(String numeroColis, boolean active);
}
