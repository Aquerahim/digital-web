package com.phoenixacces.apps.persistence.repositories.module.livraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.Clients;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long> {

    Optional<Clients> findByIdAndActive(Long id, boolean active);

    Optional<List<Clients>> findAllByActive(boolean active);

    Optional<List<Clients>> findAllByOrdreAndActive(Long ordre, boolean active);

    Optional<List<Clients>> findAllByActiveAndPartenaire(boolean active, PartenaireAffaire mesClients);
}
