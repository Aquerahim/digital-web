package com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeZoneCouvertureRepository extends JpaRepository<TypeZoneCouverture, Long> {

    Optional<TypeZoneCouverture> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeZoneCouverture>> findAllByActive(boolean active);

    Optional<TypeZoneCouverture> findByTypezoneAndActive(String typeZone, boolean active);
}