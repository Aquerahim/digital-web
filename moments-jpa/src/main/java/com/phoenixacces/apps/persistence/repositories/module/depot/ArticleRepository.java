package com.phoenixacces.apps.persistence.repositories.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Article;
import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.module.depot.ReceptionFacture;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<List<Article>> findAllByActiveOrderByIdDesc(Boolean active);


    Optional<Article> findByIdAndActive(Long id, Boolean active);

    Optional<Article> findByArticleAndActive(String param, Boolean active);

    Optional<Article> findByArticleAndActiveAndEntreprises(String param, Boolean active, Entreprises entreprises);

    Optional<Article> findByOrdre(Long ordre);
}
