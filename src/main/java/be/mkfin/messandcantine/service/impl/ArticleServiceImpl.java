package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.repository.ArticleRepository;
import be.mkfin.messandcantine.service.ArticleService;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService ;

    @Autowired
    private ArticleRepository articleRepository ;
    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Article save(Article article) {

        UserRegistered coocker = userService.getConnectedCooker();
        if (coocker == null){
            throw new IllegalStateException("Only cookers can add a new Article !");
        }
        article.setCooker(coocker);


        return articleRepository.save(article);
    }

    @Override
    public List<Article> getArticlesOfConnectedCoocker() {
        UserRegistered coocker = userService.getConnectedCooker();
        if (coocker == null){
            return new ArrayList<>();
        }
        return articleRepository.findAllByCooker(coocker);
    }

    @Override
    public Article findArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Article> getArticlesWithAvailability() {
        List<Article> all = articleRepository.findAll();
        List<Article> articlesWithAvailability = new ArrayList<>();
        for (Article article: all) {
            for(Availability availability : article.getAvailabilities()){
                if (availability.getNbreOrder() != 0){
                    articlesWithAvailability.add(article);
                    break;
                }
            }
        }

        return articlesWithAvailability;
    }
}
