package course.spring.jyra.web;

import course.spring.jyra.model.Sprint;
import course.spring.jyra.service.SprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sprints")
@Slf4j
public class SprintController {
    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping
    public String getSprints(Model model) {
        model.addAttribute("sprints", sprintService.findAll());
        log.debug("GET: Sprints: {}", sprintService.findAll());
        return "sprints";
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
        return "redirect:/sprints"; //should redirect to other page
    }

    @PutMapping
    public String updateSprint(@RequestParam("update") String id) {
        Sprint sprint = sprintService.findById(id);
        log.debug("UPDATE: Sprint: {}", sprint);
        sprintService.update(sprint);
        return "redirect:/sprints";
    }

}
