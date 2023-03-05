package com.phoenixacces.apps.persistence.repositories.module;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ServiceCourrierRepository extends JpaRepository<ServiceCourrier, Long> {

    Optional<ServiceCourrier> findByIdAndActiveAndRetrait(Long id, boolean active, boolean retrait);

    Optional<List<ServiceCourrier>> findAllByGare_CompagnieAndActiveAndRetrait(Entreprises cie, boolean active, boolean retrait);

    Optional<List<ServiceCourrier>> findAllByActiveAndRetrait(boolean active, boolean retrait);

    Optional<List<ServiceCourrier>> findAllByActiveAndProfileAndRetrait(boolean active,Profile profile, boolean retrait);

    Optional<ServiceCourrier> findByColisNumberAndActiveAndGare_CompagnieAndRetrait(String numeroColis, boolean active, Entreprises cie, boolean retrait);

    Optional<ServiceCourrier> findByColisNumberAndRetrait(String numeroColis, boolean retrait);

    Optional<ServiceCourrier> findByProfileAndActiveAndRetrait(Profile profile, boolean active, boolean retrait);

    Optional<List<ServiceCourrier>> findByColisNumberAndActiveAndRetrait(String ref, boolean b, boolean retrait);

    Optional<List<ServiceCourrier>> findByColisNumberAndPhoneDestinatireAndActiveAndRetrait(String ref, String telDest, boolean b, boolean retrait);

    Optional<List<ServiceCourrier>> findByColisNumberAndNomDestinataireAndActiveAndRetrait(String ref, String nomDest, boolean b, boolean retrait);

    Optional<List<ServiceCourrier>> findByColisNumberAndNomDestinataireAndPhoneDestinatireAndActiveAndRetrait(String ref, String nomDest, String telDest, boolean b, boolean retrait);

    Optional<List<ServiceCourrier>> findAllByPhoneDestinatireAndActiveAndRetrait(String telDest, boolean b, boolean retrait);

    Optional<List<ServiceCourrier>> findAllByNomDestinataireLikeAndActiveAndRetrait(String nomDest, boolean b, boolean retrait);

    @Query(value="select * from t_service_courrier u where u.nom_destinataire like %:nomDest% and u.active = true and u.retrait = false ORDER BY u.creation DESC", nativeQuery=true)
    Optional<List<ServiceCourrier>> findAllByNomDestinataire(@Param("nomDest") String nomDest);

    Optional<List<ServiceCourrier>> findByNomDestinataireAndPhoneDestinatireAndActiveAndRetrait(String nomDest, String telDest, boolean b, boolean retrait);
}