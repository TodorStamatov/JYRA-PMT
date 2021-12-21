package course.spring.jyra.web;

import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.service.SprintResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sprintresults")
@Slf4j
public class SprintResultController {
    private final SprintResultService sprintResultService;

    @Autowired
    public SprintResultController(SprintResultService sprintResultService) {
        this.sprintResultService = sprintResultService;
    }

    @GetMapping
    public String getSprintResult(Model model) {
        model.addAttribute("sprint results", sprintResultService.findAll());
        log.debug("GET: Sprint results: {}", sprintResultService.findAll());
        return "sprintresults";
    }

    //TODO: getSprintResultByProjectId
    @GetMapping("/{projectId}/sprint-result")
    public String getSprintResultByProjectId(Model model, @PathVariable("projectId") String id) {
        model.addAttribute("sprint result", sprintResultService.findBySprintId(id));
        log.debug("GET: Sprint result: {}", sprintResultService.findBySprintId(id));
        return "sprintresults";
    }

    @PostMapping
    public String addSprintResult(@ModelAttribute("sprintResult") SprintResult sprintResult) {
        sprintResultService.create(sprintResult);
        log.debug("POST: Sprint result: {}", sprintResult);
        return "redirect:/sprintsresults";
    }

    @DeleteMapping
    public String deleteProjectResult(@RequestParam("delete") String id) {
        SprintResult sprintResult = sprintResultService.findById(id);
        log.debug("DELETE: Sprint result: {}", sprintResult);
        sprintResultService.deleteById(id);
        return "redirect:/sprintresults";
    }

    @PutMapping
    public String updateSprintResult(@RequestParam("update") String id) {
        SprintResult sprintResult = sprintResultService.findById(id);
        log.debug("UPDATE: Sprint result: {}", sprintResult);
        sprintResultService.update(sprintResult);
        return "redirect:/sprintresults";
    }
}
