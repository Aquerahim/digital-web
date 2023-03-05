package com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeColisRepository extends JpaRepository<NatureColis, Long> {

    Optional<NatureColis> findByIdAndActive(Long id, boolean active);

    Optional<List<NatureColis>> findAllByActive(boolean active);

    Optional<NatureColis> findByTypecolisAndActive(String typeColis, boolean active);
}
