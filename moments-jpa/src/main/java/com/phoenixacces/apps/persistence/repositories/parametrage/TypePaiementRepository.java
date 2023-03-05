package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.TypePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypePaiementRepository extends JpaRepository<TypePaiement, Long> {

    Optional<TypePaiement> findByIdAndActive(Long id, boolean active);

    Optional<List<TypePaiement>> findAllByActive(boolean active);

    Optional<TypePaiement> findByTypepaiementAndActive(String label, boolean active);
}
