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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CommandController {


    private static final String BASKET = "BASKET";
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
                .flatMap(payement -> payement.getBasket().getCommandes().stream())
                .map(commande -> commande.getAvailability().getArticle()).collect(Collectors.toList());
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
        commande.setAvailability(availability);
        model.addAttribute("command", commande);
        return "employee/command";

    }

    ///command/new


    @PostMapping("/command/add")
    public String addCommand(Commande command,BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            // Fait rien pour le moment, ceci est pour la validation des champs d'input
        }
        Object basket = session.getAttribute(BASKET);
        if (basket == null){
            basket = new Basket();
            ((Basket) basket).setCommandes(new HashSet<>());
            ((Basket) basket).setCommander(userService.getConnectedUser());
            session.setAttribute(BASKET,basket);
        }
        command.setBasket((Basket) basket);
        ((Basket) basket).getCommandes().add(command);
        return "redirect:/command/articles";
    }

    @GetMapping("/command/basket")
    public  String getBasket(HttpSession session){
        fillImages((Basket) session.getAttribute(BASKET));
        return "employee/basket";
    }

    @GetMapping("/command/clear")
    public String clearBasket(HttpSession session) {
        Basket attribute = (Basket) session.getAttribute(BASKET);
        if (attribute != null) {
            attribute.getCommandes().clear();
        }
        return "employee/basket";
    }

    @GetMapping("/command/delete/{id}")
    public String removeCommandFromBasket(@PathVariable("id") Long id, HttpSession session) {
        Basket basket = (Basket) session.getAttribute(BASKET);
        if (basket != null) {
            Optional<Commande> toRemove = basket.getCommandes().stream().filter(cmd -> cmd.getAvailability().getArticle().getId().equals(id)).findFirst();
            if (toRemove.isPresent()) {
                basket.getCommandes().remove(toRemove.get());
            }
        }
        return "employee/basket";
    }
    @GetMapping("/command/new")
    public String newCommand( Model model, HttpSession session) {
                Object basket = session.getAttribute(BASKET);
        if (basket == null){
            return "redirect:command/basket";
        }
        Basket bskt = (Basket) basket;
        fillImages(bskt);
        Payement payement = new Payement();
        payement.setBasket(bskt);
        payement.setAmount(""+ bskt.getCommandes().stream()
                .mapToDouble(command -> command.getQuantity() * command.getAvailability().getPrice())
                .sum());
        payementService.save(payement);
        model.addAttribute("payement",payement);
        return "/employee/payement";
    }

    private void fillImages(Basket bskt) {
        bskt.getCommandes().stream()
                .filter(command -> command.getAvailability().getArticle().haveImage())
                .flatMap(command ->command.getAvailability().getArticle().getImages().stream())
                .forEach(image -> articleController.setFullUrlImg(image));
    }

    /**
     * The payement can be done only by its owner
     * @param payement : the payement to do
     * @return
     */
    @PostMapping(path = "/command/pay")
    public String bookCourse(Payement payement, HttpSession session)  {
        payement = getPayement(payement.getId(), "This payement can't be done for security reasons  ");
        // TODO : check the status of the reservation
        try {
            String cancelUrl = "http://localhost:8080/command/pay/cancel/"+payement.getId();//cancelPayURL();
            String successUrl = "http://localhost:8080/command/pay/success/"+payement.getId();//successPayURL();
            String description = String.format("Command of %s articles for the price of %s €",payement.getBasket().getCommandes().size()
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
                    session.removeAttribute(BASKET);
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
        if( !payement.getBasket().getCommander().getId().equals(connectedUser.getId())){
            throw new IllegalStateException(errorMessage);
        }
        return payement;
    }


    @GetMapping( path = "command/pay/success/{id}")
    public String payementSuccess(@PathVariable Long id , Model model,@PathParam("paymentId") String paypalPayement) {
        Payement payement = payementService.findById(id);
        payement.setPaypalPayment(paypalPayement);
        payement.setStatus(PayementStatus.CONFIRMED);
        Basket basket = payement.getBasket();
        basket.setStatus(BasketStatus.PAYED);
        payementService.update(payement);
        model.addAttribute("payement",payement);

        fillImages(basket);
        return "/employee/payement_confirm";
    }

    @GetMapping( path = "command/pay/cancel/{id}")
    public String payementCancel(@PathVariable Long id , Model model) {
        Payement payement = payementService.findById(id);
        payement.setStatus(PayementStatus.REJECTED);
        Basket basket = payement.getBasket();
        basket.setStatus(BasketStatus.FAILED);
        basket.setCanceled(true);
        payementService.reject(payement);
        model.addAttribute("payement",payement);
        fillImages(basket);
        return "/employee/payement_cancel";
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
