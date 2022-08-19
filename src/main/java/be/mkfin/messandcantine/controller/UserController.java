package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/subscribe/new")
    public String newUser(UserRegistered user, HttpServletRequest request) {
         user = userService.save(user);
        return "redirect:/";
    }

    @GetMapping(path = "/admin/allusers")
    public String subscribe(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/all_users";

    }
}
