package course.spring.jyra.web;

import javax.servlet.http.HttpSession;

import course.spring.jyra.model.Developer;
import course.spring.jyra.model.User;
import course.spring.jyra.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public String indexRedirect(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/index";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new Developer());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        authenticationService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("password")) {
            model.addAttribute("password", "");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession httpSession) {
        User user = authenticationService.login(username, password);

        if (user == null) {
            // TODO: add exception
            return "redirect:login";
        }
        httpSession.setAttribute("user", user);
        return "redirect:/index";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
