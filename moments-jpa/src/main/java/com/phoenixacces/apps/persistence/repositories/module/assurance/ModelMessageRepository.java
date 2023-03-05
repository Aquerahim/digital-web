package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ModelMessage;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ModelMessageRepository extends JpaRepository<ModelMessage, Long> {

    Optional<ModelMessage> findByIdAndActive(Long id, boolean active);


    Optional<List<ModelMessage>> findAllByActive(boolean active);

    Optional<ModelMessage> findByTypeMessageAndEntreprisesAndActiveAndLibelle(TypeMessage typeMessage, Entreprises entreprises, boolean active, String libelle);

    Optional<ModelMessage> findByEntreprisesAndActiveAndLibelle(Entreprises entreprises, boolean active, String libelle);

    Optional<ModelMessage> findByTypeMessageAndEntreprisesAndActive(TypeMessage typeMessage, Entreprises entreprises, boolean active);
}
