package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.SloganEntreprise;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface SloganEntrepriseRepository extends JpaRepository<SloganEntreprise, Long> {

    Optional<SloganEntreprise> findByIdAndActive(Long id, boolean active);

    Optional<List<SloganEntreprise>> findAllByActive(boolean active);

    Optional<SloganEntreprise> findByEntreprisesAndActive(Entreprises entreprises, boolean active);
}
