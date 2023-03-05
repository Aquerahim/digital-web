package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmsEnvoyeRepository extends JpaRepository<SmsEnvoye, Long> {

    Optional<SmsEnvoye> findByTypeMessageAndNumeroDestinataireAndActive(TypeMessage typeMessage, String numeroDestinataire, boolean active);

    Optional<List<SmsEnvoye>> findByNumeroDestinataireAndActiveOrderByIdDesc(String numeroDestinataire, boolean active);

    Optional<List<SmsEnvoye>> findByServiceCourrierAndActiveOrderByIdDesc(ServiceCourrier serviceCourrier, boolean active);

    Optional<List<SmsEnvoye>> findByTypeMessageAndNumeroDestinataire(TypeMessage typeMessage, String numeroDestinataire);
}
