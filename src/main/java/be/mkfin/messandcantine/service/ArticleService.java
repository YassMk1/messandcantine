package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Article;

import java.util.List;

public interface ArticleService {


    List<Article> getAllArticles();

    Article save(Article article);

    List<Article> getArticlesOfConnectedCoocker();
}
