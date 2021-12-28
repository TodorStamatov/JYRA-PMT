package course.spring.jyra.web;

import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.Task;
import course.spring.jyra.model.User;
import course.spring.jyra.service.SprintService;
import course.spring.jyra.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sprints")
@Slf4j
public class SprintController {
    private final SprintService sprintService;
    private final UserService userService;

    @Autowired
    public SprintController(SprintService sprintService, UserService userService) {
        this.sprintService = sprintService;
        this.userService = userService;
    }

    @GetMapping
    public String getSprints(Model model) {
        Map<Sprint, User> map = new HashMap<>();
        sprintService.findAll().forEach(sprint -> map.put(sprint, userService.findById(sprint.getOwnerId())));

        model.addAttribute("sprints", sprintService.findAll());
        model.addAttribute("map", map);

        log.debug("GET: Sprints: {}", sprintService.findAll());
        return "all-sprints";
    }

    @PostMapping
    public String addSprint(@ModelAttribute("sprint") Sprint sprint) {
        sprintService.create(sprint);
        log.debug("POST: Sprint: {}", sprint);
        return "redirect:/sprints";
    }

    @DeleteMapping
    public String deleteProject(@RequestParam("delete") String id) {
        Sprint sprint = sprintService.findById(id);
        log.debug("DELETE: Sprint: {}", sprint);
        sprintService.deleteById(id);
        return "redirect:/sprints";
    }

    @GetMapping("/{sprintId}")
    public String getSprintById(Model model, @PathVariable("sprintId") String id) {
        model.addAttribute("sprint", sprintService.findById(id));
        log.debug("GET: Sprint with Id=%s : {}", id, sprintService.findById(id));
        //TODO:should redirect to other page
        return "redirect:/sprints";
    }

    @PutMapping
    public String updateSprint(@RequestParam("update") String id) {
        Sprint sprint = sprintService.findById(id);
        log.debug("UPDATE: Sprint: {}", sprint);
        sprintService.update(sprint);
        return "redirect:/sprints";
    }

    @GetMapping("/search")
    public String getSprintsBySearch(Model model, @RequestParam String keywords) {
        Map<Sprint, User> map = new HashMap<>();
        sprintService.findBySearch(keywords).forEach(sprint -> map.put(sprint, userService.findById(sprint.getOwnerId())));

        model.addAttribute("sprints", sprintService.findBySearch(keywords));
        model.addAttribute("map", map);

        log.debug("GET: Tasks by search: {}", sprintService.findBySearch(keywords));
        return "all-sprints";
    }
}
