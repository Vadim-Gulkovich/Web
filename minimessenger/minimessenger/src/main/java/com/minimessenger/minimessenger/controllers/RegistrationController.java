package com.minimessenger.minimessenger.controllers;

import com.minimessenger.minimessenger.models.db.Users;
import com.minimessenger.minimessenger.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class RegistrationController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("title", "Register page!");
        model.addAttribute("curCustomUser", new Users());

        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute Users curCustomUser, BindingResult bindingResult, Model model) {
        model.addAttribute("curCustomUser", curCustomUser);
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        // Adding necessary fields to object
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(curCustomUser.getPassword());
        curCustomUser.setPassword(encodedPassword);

        String currentDate = new Date().toString();
        curCustomUser.setLastLoginDate(currentDate);
        curCustomUser.setRegistrationDate(currentDate);
        curCustomUser.setBlocked(false);

        usersRepository.save(curCustomUser);

        return "registration";
    }
}
