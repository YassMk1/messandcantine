package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @GetMapping(path = "/admin/allarticles")
    public String allArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "cooker/all_articles";

    }
}
