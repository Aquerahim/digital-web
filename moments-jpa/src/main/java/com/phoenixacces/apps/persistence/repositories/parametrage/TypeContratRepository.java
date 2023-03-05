package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface TypeContratRepository extends JpaRepository<TypeContrat, Long> {

    Optional<TypeContrat> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeContrat>> findAllByActive(boolean active);

    Optional<List<TypeContrat>> findAllByActiveAndModule(boolean active, String module);

    Optional<TypeContrat> findByTypecontratAndActive(String label, boolean active);
}
