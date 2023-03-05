package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.enumerations.TypeClientLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface MesClientsRepository extends JpaRepository<PartenaireAffaire, Long> {


    Optional<PartenaireAffaire> findByIdAndActive(Long id, boolean active);

    Optional<PartenaireAffaire> findByOrdreAndActive(Long ordre, boolean active);

    Optional<PartenaireAffaire> findByLiaisonAndActive(String liaison, boolean active);

    Optional<PartenaireAffaire> findByNomCompletAndActiveAndTypeClient(String nomComplet, boolean active, TypeClientLivraison typeClientLivraison);

    Optional<List<PartenaireAffaire>> findAllByActive(boolean active);

    Optional<List<PartenaireAffaire>> findAllByActiveAndProfile(boolean active, Profile profile);
}
