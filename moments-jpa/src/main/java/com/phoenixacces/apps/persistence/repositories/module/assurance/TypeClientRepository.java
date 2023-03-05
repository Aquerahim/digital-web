package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface TypeClientRepository extends JpaRepository<TypeClient, Long> {

    Optional<TypeClient> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeClient>> findAllByActive(boolean active);

    Optional<TypeClient> findByTypeAndActive(String label, boolean active);
}
