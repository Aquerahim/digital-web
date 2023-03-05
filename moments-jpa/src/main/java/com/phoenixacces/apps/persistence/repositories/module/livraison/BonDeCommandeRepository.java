package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.BonDeCommande;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface BonDeCommandeRepository extends JpaRepository<BonDeCommande, Long> {

    Optional<BonDeCommande> findByIdAndActive(Long id, boolean active);


    Optional<List<BonDeCommande>> findAllByActive(boolean active);


    Optional<List<BonDeCommande>> findAllByOrdreAndActive(Long ordre, boolean active);


    Optional<BonDeCommande> findByOrdreAndActiveAndNatureColisAndNomProduit(Long ordre, boolean active,
                                                                            NatureColis nature, String designation);

    Optional<BonDeCommande> findByOrdreAndActive(Long ordre, boolean active);
}
