package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    public String home(Model model) {
        if (userService.isAuthenticated()) {
            return "index";
        } else {
            return "sign-in";
        }
    }

    @GetMapping(path = "/login-error")
    public String loginError(Model model) {
        model.addAttribute("errorLogin","LoginError") ;
        return "sign-in";

    }

    @GetMapping(path = "/subscribe")
    public String subscribe(Model model) {
        model.addAttribute("user", new UserRegistered());
        return "subscribe";

    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        httpSession.invalidate();
        return "redirect:/";
    }
}
