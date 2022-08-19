package be.mkfin.messandcantine.controller;


import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/subscribe/new")
    public String newUser(UserRegistered user, HttpServletRequest request) {

         user = userService.save(user);
        return "redirect:/";
    }
}
