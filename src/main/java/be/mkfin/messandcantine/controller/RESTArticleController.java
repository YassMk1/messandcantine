package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.controller.cooker.ArticleController;
import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RESTArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleController articleController ;
    @GetMapping(path = "/api/command/articles")
    public List<Article> myArticles(Model model) {

        List<Article> articles = articleService.getArticlesWithAvailability();
        articleController.fillUrlImages(articles);
        articles.stream().forEach(art -> {
            art.setCooker(null);
        });
        return articles;

    }
}
