package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.ProduitAssurance;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface PorteFeuilleClientRepository extends JpaRepository<PorteFeuilleClient, Long> {

    Optional<PorteFeuilleClient> findByIdAndActive(Long id, boolean active);

    Optional<PorteFeuilleClient> findByNomClientAndActiveAndProduitAndEntreprises(String nomClient, boolean active, ProduitAssurance produitAssurance, Entreprises entreprises);

    Optional<List<PorteFeuilleClient>> findAllByActive(boolean active);

    Optional<PorteFeuilleClient> findByActiveAndId(boolean active, Long id);
    Optional<List<PorteFeuilleClient>> findAllByActiveAndEntreprises(boolean active, Entreprises entreprise);
}
