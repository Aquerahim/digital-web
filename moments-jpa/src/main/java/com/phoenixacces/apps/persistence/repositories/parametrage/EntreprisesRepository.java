package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeSouscrivant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface EntreprisesRepository extends JpaRepository<Entreprises, Long> {

    Optional<List<Entreprises>> findAllByActive(boolean active);

    Optional<List<Entreprises>> findAllByActiveAndTypeSouscrivant(boolean active, TypeSouscrivant typeSouscrivant);

    Optional<Entreprises> findByRccmAndCompagnieAndActive(String rccm, String compagnie, boolean active);

    Optional<Entreprises> findByCompagnieAndActive(String compagnie, boolean active);

    Optional<Entreprises> findByRccmAndActive(String compagnie, boolean active);

    Optional<Entreprises> findByIdAndActive(Long id, boolean active);
}
