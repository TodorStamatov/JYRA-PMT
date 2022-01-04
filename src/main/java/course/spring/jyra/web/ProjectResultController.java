package course.spring.jyra.web;

import course.spring.jyra.model.*;
import course.spring.jyra.service.ProjectResultService;
import course.spring.jyra.service.ProjectService;
import course.spring.jyra.service.SprintResultService;
import course.spring.jyra.service.SprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projectresults")
@Slf4j
public class ProjectResultController {
    private final ProjectResultService projectResultService;
    private final ProjectService projectService;
    private final SprintResultService sprintResultService;
    private final SprintService sprintService;

    @Autowired
    public ProjectResultController(ProjectResultService projectResultService, ProjectService projectService, ProjectService projectService1, SprintResultService sprintResultService, SprintService sprintService) {
        this.projectResultService = projectResultService;
        this.projectService = projectService1;
        this.sprintResultService = sprintResultService;
        this.sprintService = sprintService;
    }

    @GetMapping
    public String getProjectResults(Model model) {
        Map<ProjectResult, Project> map = new HashMap<>();
        projectResultService.findAll().forEach(projectResult -> map.put(projectResult, projectService.findById(projectResult.getProjectId())));

        model.addAttribute("projectResults", projectResultService.findAll());
        model.addAttribute("map", map);

        log.debug("GET: Project results: {}", projectResultService.findAll());
        return "all-project-results";
    }

    @GetMapping("/{projectId}/project-result")
    public String getResultsByProjectId(Model model, @PathVariable("projectId") String projectId) {
        ProjectResult projectResult = projectResultService.findByProject(projectId);
        List<SprintResult> sprintResultsList = projectResult.getSprintResultListIds().stream().map(sprintResultService::findById).collect(Collectors.toList());
        Map<SprintResult, Sprint> map = new HashMap<>();
        sprintResultsList.forEach(sprintResult -> map.put(sprintResult, sprintService.findById(sprintResult.getSprintId())));

        model.addAttribute("projectResult", projectResult);
        model.addAttribute("project", projectService.findById(projectResult.getProjectId()));
        model.addAttribute("sprintResults", sprintResultsList);
        model.addAttribute("map", map);

        log.debug("GET: Result of Project with ID=%s: {}", projectId, projectResultService.findByProject(projectId));
        return "single-project-result";
    }

    @GetMapping("/create")
    public String getCreateProjectResult(Model model) {
        if (!model.containsAttribute("projectResult")) {
            model.addAttribute("projectResult", new ProjectResult());
        }
        model.addAttribute("request", "POST");
        return "form-project-result";
    }

    @PostMapping("/create")
    public String addProjectResult(@ModelAttribute ProjectResult projectResult) {
        projectResultService.create(projectResult);
        log.debug("POST: Project result: {}", projectResult);
        return "redirect:/projectsresults";
    }

    @GetMapping("/edit")
    public String getEditProjectResult(Model model, @RequestParam String projectResultId) {
        ProjectResult projectResult = projectResultService.findById(projectResultId);
        if (!model.containsAttribute("projectResult")) {
            model.addAttribute("projectResult", projectResult);
        }
        model.addAttribute("request", "PUT");

        return "form-project-result";
    }

    @DeleteMapping("/delete")
    public String deleteProjectResult(@RequestParam String projectResultId) {
        ProjectResult projectResult = projectResultService.findById(projectResultId);
        log.debug("DELETE: Project result: {}", projectResult);
        projectResultService.deleteById(projectResultId);
        return "redirect:/projectresults";
    }

    @PutMapping("/edit")
    public String updateProjectResult(@RequestParam String projectResultId, @ModelAttribute ProjectResult projectResult) {
        log.debug("UPDATE: Project result: {}", projectResult);
        projectResultService.update(projectResult, projectResultId);
        return "redirect:/projectresults";
    }
}
