package course.spring.jyra.web;

import course.spring.jyra.model.User;
import course.spring.jyra.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        log.debug("GET: Users: {}", userService.findAll());
        return "users";
    }

    @DeleteMapping
    public String deleteUser(@RequestParam("delete") String id) {
        User user = userService.findById(id);
        log.debug("DELETE: User: {}", user);
        userService.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getUserById(Model model, @PathVariable("userId") String id) {
        model.addAttribute("user", userService.findById(id));
        log.debug("GET: User with Id=%s : {}", id, userService.findAll());
        return "redirect:/users"; //should redirect to other page
    }

    @PutMapping
    public String updateUser(@RequestParam("update") String id) {
        User user = userService.findById(id);
        log.debug("UPDATE: User: {}", user);
        userService.update(user);
        return "redirect:/users";
    }
}
