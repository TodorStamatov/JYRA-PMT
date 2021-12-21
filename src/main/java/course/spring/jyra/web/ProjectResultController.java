package course.spring.jyra.web;

import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.service.ProjectResultService;
import course.spring.jyra.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projectresults")
@Slf4j
public class ProjectResultController {
    private final ProjectResultService projectResultService;

    @Autowired
    public ProjectResultController(ProjectResultService projectResultService, ProjectService projectService) {
        this.projectResultService = projectResultService;
    }

    @GetMapping
    public String getProjectResults(Model model) {
        model.addAttribute("project results", projectResultService.findAll());
        log.debug("GET: Project results: {}", projectResultService.findAll());
        return "projectresults";
    }

    @GetMapping("/{projectId}/project-result")
    public String getResultsByProjectId(Model model, @PathVariable("projectId") String projectId) {
        model.addAttribute("project result", projectResultService.findByProject(projectId));
        log.debug("GET: Result of Project with ID=%s: {}", projectId, projectResultService.findByProject(projectId));
        return "projectresults";
    }

    @PostMapping
    public String addProjectResult(@ModelAttribute("projectResult") ProjectResult projectResult) {
        projectResultService.create(projectResult);
        log.debug("POST: Project result: {}", projectResult);
        return "redirect:/projectsresults";
    }

    @DeleteMapping
    public String deleteProjectResult(@RequestParam("delete") String id) {
        ProjectResult projectResult = projectResultService.findById(id);
        log.debug("DELETE: Project result: {}", projectResult);
        projectResultService.deleteById(id);
        return "redirect:/projectresults";
    }

    @PutMapping
    public String updateProjectResult(@RequestParam("update") String id) {
        ProjectResult projectResult = projectResultService.findById(id);
        log.debug("UPDATE: Project result: {}", projectResult);
        projectResultService.update(projectResult);
        return "redirect:/projectresults";
    }
}
