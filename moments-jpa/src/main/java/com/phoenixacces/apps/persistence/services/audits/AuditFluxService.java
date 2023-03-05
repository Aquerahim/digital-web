package com.phoenixacces.apps.persistence.services.audits;

import com.phoenixacces.apps.persistence.entities.audits.AuditFlux;
import com.phoenixacces.apps.persistence.repositories.audits.AuditFluxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AuditFluxService {

    private final AuditFluxRepository auditFluxRepository;

    @Autowired
    public AuditFluxService(AuditFluxRepository auditFluxRepository) {
        this.auditFluxRepository = auditFluxRepository;
    }

    public AuditFlux create(AuditFlux auditFlux){
        return auditFluxRepository.save(auditFlux);
    }

    public List<AuditFlux> find(String idMedicis){
        return auditFluxRepository.findByIdFlux(idMedicis);
    }

    public List<AuditFlux> find(String idMedicis, Date begin, Date end){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return auditFluxRepository.findByIdFluxAndCreationBetween(idMedicis, simpleDateFormat.format(begin),
                simpleDateFormat.format(end));
    }

    public List<AuditFlux> list(){
        return auditFluxRepository.findAll();
    }
}
