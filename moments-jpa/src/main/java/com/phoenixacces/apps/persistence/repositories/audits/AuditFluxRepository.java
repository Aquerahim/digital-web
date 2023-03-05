package com.phoenixacces.apps.persistence.repositories.audits;

import com.phoenixacces.apps.persistence.entities.audits.AuditFlux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditFluxRepository extends JpaRepository<AuditFlux, Long> {

    List<AuditFlux> findByIdFlux(String idFlux);

    List<AuditFlux> findByIdFluxAndCreationBetween(String idFlux, String begin, String end);
}
