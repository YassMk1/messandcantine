package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.controller.cooker.ArticleController;
import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleController articleController ;

    @GetMapping(path = "/admin/allarticles")
    public String allArticles(Model model) {
        List<Article> allArticles = articleService.getAllArticles();
        articleController.fillUrlImages(allArticles);

        model.addAttribute("articles", allArticles);
        return "cooker/all_articles";

    }
}
