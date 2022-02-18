package com.minimessenger.minimessenger.controllers;
import com.minimessenger.minimessenger.models.CustomUserDetails;
import com.minimessenger.minimessenger.models.db.Users;
import com.minimessenger.minimessenger.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Main page!");
        model.addAttribute("curCustomUser", new Users());
        return "home";
    }

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/successLogin")
    public String successLogin(Model model) {
        model.addAttribute("title", "successLogin");
        return "successLogin";
    }

    @PostMapping("/home")
    public String signUser(@ModelAttribute Users curCustomUser, BindingResult bindingResult, Model model, HttpSession session) {
        model.addAttribute("curCustomUser", curCustomUser);
        if (bindingResult.hasErrors()) {
            return "home";
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Users dbLoginUser = usersRepository.findByEmailAddress(curCustomUser.getEmail());
        String encodedPassword = passwordEncoder.encode(curCustomUser.getPassword());
        curCustomUser.setPassword(encodedPassword);

        if (dbLoginUser != null && dbLoginUser.getPassword().equals(curCustomUser.getPassword()) && !dbLoginUser.isBlocked()) {
            model.addAttribute("loginInfo", "User logged in: " + dbLoginUser.getName());
            dbLoginUser.setLastLoginDate(new Date().toString());
            usersRepository.save(dbLoginUser);
        } else {
            model.addAttribute("loginInfo", "Invalid credentials");
        }

        return "home";
    }

}