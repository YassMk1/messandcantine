package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.controller.cooker.ArticleController;
import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.model.CommandStatistics;
import be.mkfin.messandcantine.service.ArticleService;
import be.mkfin.messandcantine.service.StatisticsService;
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

    @Autowired
    private StatisticsService statisticsService ;

    @GetMapping(path = "/admin/allarticles")
    public String allArticles(Model model) {
        List<Article> allArticles = articleService.getAllArticles();
        articleController.fillUrlImages(allArticles);

        model.addAttribute("articles", allArticles);
        return "cooker/all_articles";

    }

    @GetMapping(path = "/admin/Statistics")
    public String commandeSatistics(Model model) {
        List<CommandStatistics> statisticsArticles = statisticsService.getStatisticsOfAllCommands();

        statisticsArticles.stream().forEach(st -> st.article.getImages().stream().forEach(image -> articleController.setFullUrlImg(image)));
        model.addAttribute("statArticles", statisticsArticles);
        return "admin/command_statistics";

    }

}
