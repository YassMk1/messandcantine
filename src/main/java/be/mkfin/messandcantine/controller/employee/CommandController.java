package be.mkfin.messandcantine.controller.employee;


import be.mkfin.messandcantine.controller.cooker.ArticleController;
import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CommandController {


    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleController articleController;


    @GetMapping(path = "/command/articles")
    public String myArticles(Model model) {

        List<Article> articles = articleService.getArticlesWithAvailability();
        articleController.fillUrlImages(articles);
        model.addAttribute("articles", groupArticlesBy4(articles));
        return "employee/all_articles";

    }

    private List<ArticleGroup> groupArticlesBy4(List<Article> articles) {
        List<ArticleGroup> group = new ArrayList<>();
        ArticleGroup articleGroup = null ;
        for(int i =0 ; i< articles.size() ; i++){
            if( i %4 == 0){
                articleGroup = new ArticleGroup() ;
                group.add(articleGroup);
            }
            switch (i %4){
                case  0 : articleGroup._1 = articles.get(i); break ;
                case  1 : articleGroup._2 = articles.get(i);break ;
                case  2 : articleGroup._3 = articles.get(i);break ;
                case  3 : articleGroup._4 = articles.get(i);break ;
            }
        }
        return group;
    }

    public class ArticleGroup {

        public Article _1 ;
        public Article _2 ;
        public Article _3 ;
        public Article _4 ;
    }
}
