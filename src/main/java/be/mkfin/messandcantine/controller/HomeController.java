package be.mkfin.messandcantine.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(path = "")
    public String home(Model model) {
        return "index";
    }
}
