package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.MotifSuspensionCollaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface MotifSuspensionCollaborationRepository extends JpaRepository<MotifSuspensionCollaboration, Long> {

    Optional<MotifSuspensionCollaboration> findByIdAndActive(Long id, boolean active);

    Optional<List<MotifSuspensionCollaboration>> findAllByActive(boolean active);

    Optional<MotifSuspensionCollaboration> findByMotifAndActive(String label, boolean active);
}
