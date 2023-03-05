package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.PaysAutorise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface PaysAutoriseRepository extends JpaRepository<PaysAutorise, Long> {

    Optional<PaysAutorise> findByIdAndActive(Long id, boolean active);

    Optional<List<PaysAutorise>> findAllByActive(boolean active);

    Optional<PaysAutorise> findByPaysautoriseAndActive(String label, boolean active);
}
