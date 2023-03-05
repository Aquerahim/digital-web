package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeMessageRepository extends JpaRepository<TypeMessage, Long> {

    Optional<TypeMessage> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeMessage>> findAllByActive(boolean active);

    Optional<List<TypeMessage>> findAllByActiveAndModule(boolean active, String module);

    Optional<TypeMessage> findByTypemessgaeAndActive(String label, boolean active);
}
