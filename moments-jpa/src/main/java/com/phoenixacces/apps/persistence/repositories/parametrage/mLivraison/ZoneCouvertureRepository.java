package com.phoenixacces.apps.persistence.repositories.parametrage.mLivraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneCouvertureRepository extends JpaRepository<ZoneCouverture, Long> {

    Optional<ZoneCouverture> findByIdAndActive(Long id, boolean active);

    Optional<List<ZoneCouverture>> findAllByActiveOrderByZoneCouvertureAsc(boolean active);

    Optional<List<ZoneCouverture>> findAllByActiveAndTypeZoneCouverture(boolean active, TypeZoneCouverture typeCouverture);

    Optional<ZoneCouverture> findByZoneCouvertureAndTypeZoneCouvertureAndActive(String zoneCouverture, TypeZoneCouverture typeCouverture, boolean active);
}