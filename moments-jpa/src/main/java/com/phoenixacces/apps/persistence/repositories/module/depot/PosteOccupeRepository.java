package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Diplome;
import com.phoenixacces.apps.persistence.entities.module.depot.PosteOccupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosteOccupeRepository extends JpaRepository<PosteOccupe, Long> {

    Optional<List<PosteOccupe>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<PosteOccupe> findByIdAndActive(Long id, Boolean active);

    Optional<PosteOccupe> findByPosteAndActive(String natMouvt, Boolean active);

    Optional<PosteOccupe> findByOrdre(Long ordre);
}
