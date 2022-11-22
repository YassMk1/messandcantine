package be.mkfin.messandcantine.controller.cooker;


import be.mkfin.messandcantine.entity.*;
import be.mkfin.messandcantine.model.CommandStatistics;
import be.mkfin.messandcantine.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ArticleController {


    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ImageService imageService;
    @Autowired
    private StorageService storageService;

    @Autowired
    private StatisticsService statisticsService ;


    @GetMapping(path = "/article/Statistics")
    public String commandeSatistics(Model model) {
        List<CommandStatistics> statisticsArticles = statisticsService.getStatisticsOfMyCommands();

        statisticsArticles.stream().forEach(st -> st.article.getImages().stream().forEach(image -> setFullUrlImg(image)));
        model.addAttribute("statArticles", statisticsArticles);
        return "admin/command_statistics";

    }

    @GetMapping(path = "/article/new")
    public String newArticle(Model model) {
        model.addAttribute("article", new Article());
        return "cooker/new_article";

    }
    @GetMapping(path = "article/view/{id}")
    public String getArticle(@PathVariable Long id , Model model) {
        Article articleById = articleService.findArticleById(id);
        if(articleById == null){
            throw new IllegalArgumentException("Can't find that article ");
        }
        if (articleById.haveImage()){
            articleById.getImages().stream().forEach(image -> setFullUrlImg(image));
        }
        model.addAttribute("article", articleById);
        return "cooker/article";

    }

    @GetMapping(path = "article/edit/{id}")
    public String editArticle(@PathVariable Long id , Model model) {
        Article articleById = articleService.findArticleById(id);



        if(articleById == null){
            throw new IllegalArgumentException("Can't find that article ");
        }
        UserRegistered userRegistered = userService.getConnectedUser();
        if( userRegistered.getId() != articleById.getCooker().getId()){
            throw new IllegalArgumentException("Access denieded: This article can  be edited only by its owner !  ");
        }
        model.addAttribute("article", articleById);
        return "cooker/new_article";

    }

    @GetMapping(path = "article/newavail/{id}")
    public String addNewAvailabilityArticle(@PathVariable Long id , Model model) {
        Article articleById = articleService.findArticleById(id);
        if(articleById == null){
            throw new IllegalArgumentException("Can't find that article ");
        }
        if (articleById.haveImage()){
            articleById.getImages().stream().forEach(image -> setFullUrlImg(image));
        }
        model.addAttribute("article", articleById);
        Availability availability = new Availability();
        availability.setArticle(articleById);
        model.addAttribute("availability", availability);
        return "cooker/new_availability_article";

    }

    @PostMapping("/article/new")
    public String newArticle(Article article,  @RequestParam("file") MultipartFile file,
                             BindingResult result,HttpServletRequest request) {
        if (result.hasErrors()) {
           // Fait rien pour le moment, ceci est pour la validation des champs d'input
        }
        if (article.getId() == null) {
            Image image = null;
            Article newArticle = articleService.save(article);
            if (file != null) {
                Image image1 = new Image();
                if (newArticle.getImages() == null ||newArticle.getImages().isEmpty()  ) {
                    // the first image is always the main image
                    image1.setProfile(true);
                    newArticle.setImages(new HashSet<>());
                }
                image1.setArticle(newArticle);
                image = imageService.store(image1);
            }
            if (image != null) {
                storeFile(file, ""+image.getId(), ""+newArticle.getId());
            }
        } else {
            Image image = null;
            article = articleService.save(article);
            if (file != null && ! file.isEmpty()) {
                Image image1 = new Image();
                if (article.getImages() == null ||article.getImages().isEmpty() ) {
                    // the first image is always the main image
                    image1.setProfile(true);
                    article.setImages(new HashSet<>());
                }
                image1.setArticle(article);
                image = imageService.store(image1);
            }
            if (image != null) {
                storeFile(file,  ""+image.getId(), ""+article.getId());
            }
        }
        return "redirect:/articles";
    }


    @GetMapping(path = "/articles")
    public String myArticles(Model model) {

        List<Article> articles = articleService.getArticlesOfConnectedCoocker();
        fillUrlImages(articles);
        model.addAttribute("articles", articles);
        return "cooker/all_articles";

    }


    @Autowired
    PayementService payementService;

    @GetMapping(path = "/articles/commands")
    public String allCommands(Model model) {
        List<Payement> allMyPayemens = payementService.getAllPayemensOfCooker(userService.getConnectedCooker());
        List<Article> articles = allMyPayemens.stream()
                .flatMap(payement -> payement.getBasket().getCommandes().stream())
                .map(commande -> commande.getAvailability().getArticle()).collect(Collectors.toList());
        fillUrlImages(articles);
        model.addAttribute("allMyPayemens", allMyPayemens);
        return "cooker/all_commands";

    }

    @Autowired
    private BasketService basketService ;
    @GetMapping(path = "/articles/commands/{id}")
    public String doActionOnBasket(Model model , @PathVariable("id") Long id , @PathParam("act") String act) {

        Basket basket = basketService.findById(id);
        if (basket != null && act != null){

            BasketStatus basketStatus = BasketStatus.valueOf(act);
            if (basketStatus != null){
                basket.setStatus(basketStatus);
                basketService.save(basket);
            }
        }

        return "redirect:/articles/commands";

    }
   public void fillUrlImages(List<Article> all) {
        all.stream().flatMap(
                article -> article.getImages() != null ?article.getImages().stream() : Stream.empty()
        ).forEach(image -> setFullUrlImg(image));
    }
    public void setFullUrlImg(Image image) {
        image.setFullUrlImg(fileToPath(storageService.load(image.buildPathName())));
    }
    public static String fileToPath(Path path) {
        return MvcUriComponentsBuilder.fromMethodName(ArticleController.class,
                "loadImageFromServer", path.getFileName().toString()).build().toString();
    }

    private String storeFile(MultipartFile file, String imageId,String parentId) {
        String new_name =  "articles" + File.separator + parentId+ File.separator + imageId + ".jpeg";
        storageService.store(file, new_name);
        return new_name;
    }

    @GetMapping("/downloadFile/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> loadImageFromServer(@PathVariable String filename) {
        String realFileName = filename.replaceAll("_","//");
        Resource file = storageService.loadAsResource("articles" + File.separator + realFileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename()).body(file);
    }



}
