package be.mkfin.messandcantine.controller.employee;


import be.mkfin.messandcantine.controller.cooker.ArticleController;
import be.mkfin.messandcantine.entity.*;
import be.mkfin.messandcantine.service.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CommandController {


    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleController articleController;

    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private PayementService payementService;


    @Autowired
    private PaypalService paypalService;


    @GetMapping(path = "/command/articles")
    public String myArticles(Model model) {

        List<Article> articles = articleService.getArticlesWithAvailability();
        articleController.fillUrlImages(articles);
        model.addAttribute("articles", groupArticlesBy4(articles));
        return "employee/all_articles";

    }

    @GetMapping(path = "/command/all")
    public String allCommands(Model model) {
        List<Payement> allMyPayemens = payementService.getAllMyPayemens();
        List<Article> articles = allMyPayemens.stream()
                .map(payement -> payement.getCommande().getAvailability().getArticle()).collect(Collectors.toList());
        articleService.getArticlesWithAvailability();
        articleController.fillUrlImages(articles);
        model.addAttribute("allMyPayemens", allMyPayemens);
        return "employee/all_commands";

    }

    @GetMapping( path = "command/new/{id}")
    public String doACommand(@PathVariable Long id , Model model) {
        Availability availability = availabilityService.findAvailabilityById(id);
        if(availability == null){
            throw new IllegalArgumentException("Can't find that availability ");
        }
        if (availability.getArticle().haveImage()){
            availability.getArticle().getImages().stream().forEach(image -> articleController.setFullUrlImg(image));
        }
        List<Article> articles = articleService.getArticlesWithAvailability();
        articleController.fillUrlImages(articles);
        model.addAttribute("article", availability.getArticle());
        Commande commande = new Commande();
        commande.setCommander(userService.getConnectedEmployee());
        commande.setAvailability(availability);
        model.addAttribute("command", commande);
        return "employee/command";

    }

    ///command/new

    @PostMapping("/command/new")
    public String newCommand(Commande command,BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Fait rien pour le moment, ceci est pour la validation des champs d'input
        }
        if (command.getAvailability().getArticle().haveImage()){
            command.getAvailability().getArticle().getImages().stream().forEach(image -> articleController.setFullUrlImg(image));
        }
        Payement payement = new Payement();
        payement.setCommande(command);
        payement.setAmount(""+command.getQuantity() * command.getAvailability().getPrice());
        payementService.save(payement);
        model.addAttribute("payement",payement);
        model.addAttribute("article",command.getAvailability().getArticle());
        model.addAttribute("command",command);
        return "/employee/command_pay";
    }

    /**
     * The payement can be done only by its owner
     * @param payement : the payement to do
     * @return
     */
    @PostMapping(path = "/command/pay")
    public String bookCourse(Payement payement, HttpServletRequest request)  {
        payement = getPayement(payement.getId(), "This payement can't be done for security reasons  ");
        // TODO : check the status of the reservation
        try {
            String cancelUrl = "http://localhost:8080/command/pay/cancel/"+payement.getId();//cancelPayURL();
            String successUrl = "http://localhost:8080/command/pay/success/"+payement.getId();//successPayURL();
            String description = String.format("Command of %s of %s for the price of %s €",payement.getCommande().getQuantity()
            ,payement.getCommande().getAvailability().getArticle().getName()
            ,payement.getAmount());
            Payment payment = paypalService.createPayment(Double.valueOf(payement.getAmount()),
                    "EUR",
                    "PAYPAL",
                    "SALE",
                    description, cancelUrl,
                    successUrl);

            payementService.makePaypalPayment(payement,payment);

            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return "redirect:"+link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "redirect:/command/pay/"+ payement.getId();
    }

    private Payement getPayement(Long id,String errorMessage) {
        Payement payement =  payementService.findById(id);
        // security check
        UserRegistered connectedUser = userService.getConnectedUser();
        if( !payement.getCommande().getCommander().getId().equals(connectedUser.getId())){
            throw new IllegalStateException(errorMessage);
        }
        return payement;
    }


    @GetMapping( path = "command/pay/success/{id}")
    public String payementSuccess(@PathVariable Long id , Model model,@PathParam("paymentId") String paypalPayement) {
        Payement payement = payementService.findById(id);
        payement.setPaypalPayment(paypalPayement);
        payement.setStatus(PayementStatus.CONFIRMED);
        payementService.update(payement);
        model.addAttribute("payement",payement);
        Commande command = payement.getCommande();
        if (command.getAvailability().getArticle().haveImage()){
            command.getAvailability().getArticle().getImages().stream().forEach(image -> articleController.setFullUrlImg(image));
        }
        model.addAttribute("article", command.getAvailability().getArticle());
        model.addAttribute("command", command);
        return "/employee/command_confirm";
    }

    @GetMapping( path = "command/pay/cancel/{id}")
    public String payementCancel(@PathVariable Long id , Model model) {
        Payement payement = payementService.findById(id);
        payement.setStatus(PayementStatus.REJECTED);
        payementService.reject(payement);
        model.addAttribute("payement",payement);
        Commande commande = payement.getCommande();
        if (commande.getAvailability().getArticle().haveImage()){
            commande.getAvailability().getArticle().getImages().stream().forEach(image -> articleController.setFullUrlImg(image));
        }
        model.addAttribute("article", commande.getAvailability().getArticle());
        model.addAttribute("command", commande);
        return "/employee/command_cancel";
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
