package course.spring.jyra.web;

import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.Administrator;
import course.spring.jyra.model.Developer;
import course.spring.jyra.model.ProductOwner;
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
        return "all-users";
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
        User user = userService.findById(id);
        String userType = "";
        if (user instanceof Developer) {
            userType = "DEV";
            Developer dev = (Developer) user;
            model.addAttribute("user", dev);
        } else if (user instanceof Administrator) {
            userType = "ADMIN";
            Administrator admin = (Administrator) user;
            model.addAttribute("user", admin);
        } else if (user instanceof ProductOwner) {
            userType = "PO";
            ProductOwner po = (ProductOwner) user;
            model.addAttribute("user", po);
        } else {
            throw new InvalidEntityException(String.format("User with ID=%s is not one of the supported user types format.", id));
        }
        model.addAttribute("userType", userType);

        log.debug("GET: User with Id=%s : {}", id, userService.findById(id));
        //TODO:should redirect to other page
        return "single-user";
    }

    @PutMapping
    public String updateUser(@RequestParam("update") String id) {
        User user = userService.findById(id);
        log.debug("UPDATE: User: {}", user);
        userService.update(user);
        return "redirect:/users";
    }
}
