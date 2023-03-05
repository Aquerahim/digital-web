package com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typeengin.TypeEngins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeEnginsRepository extends JpaRepository<TypeEngins, Long> {

    Optional<TypeEngins> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeEngins>> findAllByActive(boolean active);

    Optional<TypeEngins> findByTypeenginsAndActive(String typeEngins, boolean active);
}
