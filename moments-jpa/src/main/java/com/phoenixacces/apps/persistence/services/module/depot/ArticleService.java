package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Article;
import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.module.depot.ReceptionFacture;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.depot.ArticleRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.ReceptionFactureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    public List<Article> findAll() {

        return articleRepository.findAll();
    }


    public List<Article> findAll(Boolean aBoolean) {

        return articleRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public Article findOne(String name, Boolean aBoolean) {

        return articleRepository.findByArticleAndActive(name, aBoolean).orElseGet(() -> null);
    }


    public Article findOne(String name, Boolean aBoolean, Entreprises entreprises) {

        return articleRepository.findByArticleAndActiveAndEntreprises(name, aBoolean, entreprises).orElseGet(() -> null);
    }

    public Article findByOrdre(Long ordre) {

        return articleRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Article findById(Long id) {

        return articleRepository.findById(id).orElseGet(() -> null);
    }

    public Article create(Article item) throws Exception {

        return articleRepository.findByArticleAndActiveAndEntreprises(item.getArticle(), true, item.getEntreprises()).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return articleRepository.save(item);
        });
    }


    public Article update(Article donne) throws Exception {

        articleRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            articleRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        articleRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            articleRepository.save(model);
        });
    }
}
