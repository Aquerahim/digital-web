package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Banque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface BanqueRepository extends JpaRepository<Banque, Long> {

    Optional<List<Banque>> findAllByActiveOrderByIdDesc(Boolean active);

    Optional<Banque> findByIdAndActive(Long id, Boolean active);

    Optional<Banque> findByNomBanqueAndActive(String nombanque, Boolean active);

    Optional<Banque> findByOrdre(Long ordre);
}
