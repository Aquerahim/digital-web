package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.PosteOccupe;
import com.phoenixacces.apps.persistence.entities.module.depot.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<List<Produit>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<Produit> findByIdAndActive(Long id, Boolean active);
    Optional<Produit> findByNomBoissonAndActive(String natMouvt, Boolean active);

    Optional<Produit> findByOrdre(Long ordre);
}
