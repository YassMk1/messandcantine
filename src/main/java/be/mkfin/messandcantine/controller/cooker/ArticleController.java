package be.mkfin.messandcantine.controller.cooker;


import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ArticleController {


    @Autowired
    private ArticleService articleService;

    @GetMapping(path = "/article/new")
    public String newArticle(Model model) {
        model.addAttribute("article", new Article());
        return "cooker/new_article";

    }

    @PostMapping("/article/new")
    public String newArticle(Article article, HttpServletRequest request) {
        article = articleService.save(article);
        return "redirect:/articles";
    }

    @GetMapping(path = "/articles")
    public String myArticles(Model model) {
        model.addAttribute("articles", articleService.getArticlesOfConnectedCoocker());
        return "cooker/all_articles";

    }


}
