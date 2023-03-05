package com.phoenixacces.apps.persistence.repositories.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface SuiviDemandeRepository extends JpaRepository<SuiviDemande, Long> {

    Optional<SuiviDemande> findByIdAndActive(Long id, boolean active);


    Optional<List<SuiviDemande>> findAllByActive(boolean active);

    //Optional<List<SuiviDemande>> findAllByActiveAndMdle(boolean active, String mdle);


    Optional<SuiviDemande> findBySuiviAndActive(String label, boolean active);


    @Query(value = "SELECT * FROM t_suivi_demande WHERE id > :demandeId AND id <> 4", nativeQuery = true)
    Optional<List<SuiviDemande>> demandeDisponible(@Param("demandeId") Long demandeId);


    @Query(value = "SELECT * FROM t_suivi_demande WHERE id > :suiviId AND id <> 4 AND mdle =:mdle AND active =:active", nativeQuery = true)
    Optional<List<SuiviDemande>> findAllByActiveAndMdle(@Param("suiviId") Long suiviId, @Param("mdle") String mdle, @Param("active") boolean active);

    /*
    @Query(nativeQuery = true, value = "SELECT * FROM t_notification WHERE active = 1 AND pays_deploy =:paysDeploy ORDER BY id DESC LIMIT 4")
    List<Notification> findAllByLimit(@Param("paysDeploy") String paysDeploy);
     */
}