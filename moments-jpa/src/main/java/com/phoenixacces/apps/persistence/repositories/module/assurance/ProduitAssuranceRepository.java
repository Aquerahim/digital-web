package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProduitAssuranceRepository extends JpaRepository<ProduitAssurance, Long> {

    Optional<ProduitAssurance> findByIdAndActive(Long id, boolean active);

    Optional<List<ProduitAssurance>> findAllByActive(boolean active);

    Optional<ProduitAssurance> findByTypeAndProduitAndActive(String label, String produit, boolean active);

    Optional<ProduitAssurance> findByProduitAndActive(String produit, boolean active);
}
