package com.minimessenger.minimessenger.controllers;

import com.minimessenger.minimessenger.models.CustomUserDetails;
import com.minimessenger.minimessenger.models.db.Users;
import com.minimessenger.minimessenger.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class UserManagerController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/userManager")
    public String userManager(Model model) {
        model.addAttribute("title", "User Manager!");
        Iterable<Users> users = usersRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("curCustomUser", new Users());
        return "userManager";
    }

    @RequestMapping(value = "/userManager", method = RequestMethod.POST, params = "delete")
    public RedirectView delete(@RequestParam(value = "checkboxName",required=false) List<String> checkboxes) {
        if (checkboxes != null) {
            for (String checkbox : checkboxes) {
                usersRepository.deleteById(Long.valueOf(checkbox));
            }
            LogoutUsers();
        }
        return new RedirectView("userManager");
    }

    @RequestMapping(value = "/userManager", method = RequestMethod.POST, params = "block")
    public RedirectView block(@RequestParam(value = "checkboxName",required=false) List<String> checkboxes, Model model) {
        if (checkboxes != null) {
            for (String checkbox : checkboxes) {
                Users currentUser = usersRepository.findById(Long.valueOf(checkbox)).get();
                currentUser.setBlocked(true);
                usersRepository.save(currentUser);
            }
            LogoutUsers();
        }
        return new RedirectView("userManager");
    }

    @RequestMapping(value = "/userManager", method = RequestMethod.POST, params = "unblock")
    public RedirectView unblock(@RequestParam(value = "checkboxName",required=false) List<String> checkboxes, Model model) {
        if (checkboxes != null) {
            for (String checkbox : checkboxes) {
                Users currentUser = usersRepository.findById(Long.valueOf(checkbox)).get();
                currentUser.setBlocked(false);
                usersRepository.save(currentUser);
            }
        }
        return new RedirectView("userManager");
    }

    public void LogoutUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (final Object principal : allPrincipals) {
            if (principal instanceof CustomUserDetails) {
                final CustomUserDetails user = (CustomUserDetails) principal;

                CustomUserDetails dbUser = new CustomUserDetails(usersRepository.findByEmailAddress(user.getEmail()));
                if (dbUser == null || !dbUser.isAccountNonLocked()) {
                    List<SessionInformation> activeUserSessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : activeUserSessions) {
                        session.expireNow();
                    }
                }
            }
        }
    }
}
