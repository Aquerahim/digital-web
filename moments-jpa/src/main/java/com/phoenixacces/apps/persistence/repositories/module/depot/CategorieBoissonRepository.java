package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.CategorieBoisson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface CategorieBoissonRepository extends JpaRepository<CategorieBoisson, Long> {

    Optional<List<CategorieBoisson>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<CategorieBoisson> findByIdAndActive(Long id, Boolean active);

    Optional<CategorieBoisson> findByCategorieBoissonAndActive(String natMouvt, Boolean active);

    Optional<CategorieBoisson> findByOrdre(Long ordre);
}
