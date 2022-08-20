package be.mkfin.messandcantine.controller.cooker;

import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class AvailabilityController {

    @Autowired
   private AvailabilityService availabilityService ;


    @PostMapping(path = "article/newavail")
    public String addNewAvailabilityArticle(Availability availability,BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            // Fait rien pour le moment, ceci est pour la validation des champs d'input
        }
        if (availability.getId() == null) {
            availabilityService.save(availability);
        }
        return "redirect:/article/view/"+availability.getArticle().getId();

    }
}
