package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.module.depot.ReceptionFacture;
import com.phoenixacces.apps.persistence.entities.module.depot.TypesPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ReceptionFactureRepository extends JpaRepository<ReceptionFacture, Long> {

    Optional<List<ReceptionFacture>> findAllByActiveOrderByIdDesc(Boolean active);


    Optional<ReceptionFacture> findByIdAndActive(Long id, Boolean active);

    Optional<ReceptionFacture> findByNumeroFactureAndActive(String param, Boolean active);

    Optional<ReceptionFacture> findByNumeroFactureAndActiveAndFournisseur(String param, Boolean active, Fournisseur fournisseur);

    Optional<ReceptionFacture> findByOrdre(Long ordre);
}
