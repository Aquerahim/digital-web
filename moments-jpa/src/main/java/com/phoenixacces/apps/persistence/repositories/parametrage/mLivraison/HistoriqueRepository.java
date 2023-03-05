package com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.module.livraison.Historique;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {

    Optional<Historique> findByIdAndActive(Long id, boolean active);

    Optional<List<Historique>> findAllByActive(boolean active);

    Optional<List<Historique>> findAllByActiveAndLivraisonsOrderByIdDesc(boolean active, Livraisons livraisons);

    Optional<List<Historique>> findAllByActiveAndFicheBonDeCommandeOrderByIdDesc(boolean active, FicheBonDeCommande cmde);

    Optional<List<Historique>> findAllByActiveAndOrdreOrderByIdDesc(boolean active, Long ordre);
}
