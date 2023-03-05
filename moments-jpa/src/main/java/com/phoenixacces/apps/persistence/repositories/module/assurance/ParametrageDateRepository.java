package com.phoenixacces.apps.persistence.repositories.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ParametrageDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ParametrageDateRepository extends JpaRepository<ParametrageDates, Long> {

    Optional<ParametrageDates> findByIdAndActive(Long id, boolean active);


    Optional<List<ParametrageDates>> findAllByActive(boolean active);

    Optional<ParametrageDates> findByDateDebAndTypeDateDebAndActive(String dateDeb, String typeDateDeb, boolean active);
    Optional<ParametrageDates> findByTypeDateDebAndActive(String typeDateDeb, boolean active);
}
