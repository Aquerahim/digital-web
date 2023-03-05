package com.phoenixacces.apps.persistence.repositories.parametrage;


import com.phoenixacces.apps.persistence.entities.parametrage.TypeSouscrivant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeSouscrivantRepository extends JpaRepository<TypeSouscrivant, Long> {

    Optional<TypeSouscrivant> findByIdAndActive(Long id, boolean active);

    Optional<List<TypeSouscrivant>> findAllByActive(boolean active);

    Optional<TypeSouscrivant> findByTypesouscrivantAndActive(String label, boolean active);
}
