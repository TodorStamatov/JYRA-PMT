package course.spring.jyra.web;

import course.spring.jyra.model.Project;
import course.spring.jyra.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String getProjects(Model model) {
        model.addAttribute("projects", projectService.findAll());
        log.debug("GET: Projects: {}", projectService.findAll());
        return "projects";
    }

    @PostMapping
    public String addProject(@ModelAttribute("project") Project project) {
        projectService.create(project);
        log.debug("POST: Project: {}", project);
        return "redirect:/projects";
    }

    @DeleteMapping
    public String deleteProject(@RequestParam("delete") String id) {
        Project project = projectService.findById(id);
        log.debug("DELETE: Project: {}", project);
        projectService.deleteById(id);
        return "redirect:/projects";
    }

    @GetMapping("/{projectId}")
    public String getProjectById(Model model, @PathVariable("projectId") String id) {
        model.addAttribute("project", projectService.findById(id));
        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "redirect:/projects"; //should redirect to other page
    }

    @PutMapping
    public String updateProject(@RequestParam("update") String id) {
        Project project = projectService.findById(id);
        log.debug("UPDATE: Project: {}", project);
        projectService.update(project);
        return "redirect:/projects";
    }
}
