package course.spring.jyra.web;

import course.spring.jyra.model.Project;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.model.Task;
import course.spring.jyra.service.ProjectService;
import course.spring.jyra.service.SprintResultService;
import course.spring.jyra.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final SprintResultService sprintResultService;

    @Autowired
    public ProjectController(ProjectService projectService, TaskService taskService, SprintResultService sprintResultService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.sprintResultService = sprintResultService;
    }

    @GetMapping
    public String getProjects(Model model) {
        model.addAttribute("projects", projectService.findAll());
        log.debug("GET: Projects: {}", projectService.findAll());
        return "all-projects";
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
        return "single-project";
    }

    @GetMapping("/{projectId}/backlog")
    public String getProjectBacklog(Model model, @PathVariable("projectId") String id) {
        model.addAttribute("project", projectService.findById(id));
//        maybe can be done without creating list
        List<Task> taskBacklog = new ArrayList<>();
        projectService.findById(id).getTasksBacklogIds().forEach(taskId -> taskBacklog.add(taskService.findById(taskId)));
        model.addAttribute("backlog", taskBacklog);
        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "single-project-backlog";
    }

    @GetMapping("/{projectId}/sprint-results")
    public String getPrevSprintResults(Model model, @PathVariable("projectId") String id) {
        model.addAttribute("project", projectService.findById(id));
//        maybe can be done without creating list
        List<SprintResult> sprintResults = new ArrayList<>();
        projectService.findById(id).getPreviousSprintResultsIds().forEach(sprintId -> sprintResults.add(sprintResultService.findById(sprintId)));
        model.addAttribute("sprintResults", sprintResults);
        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "single-project-sprint-results";
    }

//    TODO: Add GetMapping for board (current sprint)

    @PutMapping
    public String updateProject(@RequestParam("update") String id) {
        Project project = projectService.findById(id);
        log.debug("UPDATE: Project: {}", project);
        projectService.update(project);
        return "redirect:/projects";
    }
}
