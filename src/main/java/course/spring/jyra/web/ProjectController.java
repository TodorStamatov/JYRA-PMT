package course.spring.jyra.web;

import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final SprintResultService sprintResultService;
    private final UserService userService;
    private final SprintService sprintService;

    @Autowired
    public ProjectController(ProjectService projectService, TaskService taskService, SprintResultService sprintResultService, UserService userService, SprintService sprintService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.sprintResultService = sprintResultService;
        this.userService = userService;
        this.sprintService = sprintService;
    }

    @GetMapping
    public String getProjects(Model model) {
        Map<Project, User> map = new HashMap<>();
        projectService.findAll().forEach(project -> map.put(project, userService.findById(project.getOwnerId())));

        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("map", map);

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
        Project project = projectService.findById(id);

        model.addAttribute("project", project);
        model.addAttribute("owner", userService.findById(project.getOwnerId()));
        model.addAttribute("developers", project.getDevelopersIds().stream().map(userService::findById).collect(Collectors.toList()));

        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "single-project";
    }

    @GetMapping("/{projectId}/backlog")
    public String getProjectBacklog(Model model, @PathVariable("projectId") String id) {
        List<Task> taskBacklog = new ArrayList<>();
        projectService.findById(id).getTasksBacklogIds().forEach(taskId -> taskBacklog.add(taskService.findById(taskId)));

        Map<Task, User> map = new HashMap<>();
        taskBacklog.forEach(task -> map.put(task, userService.findById(task.getAddedById())));

        model.addAttribute("project", projectService.findById(id));
        model.addAttribute("backlog", taskBacklog);
        model.addAttribute("map", map);

        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "single-project-backlog";
    }

    @GetMapping("/{projectId}/sprint-results")
    public String getPrevSprintResults(Model model, @PathVariable("projectId") String id) {
        List<SprintResult> sprintResultsList = new ArrayList<>();
        projectService.findById(id).getPreviousSprintResultsIds().forEach(sprintId -> sprintResultsList.add(sprintResultService.findById(sprintId)));
        Map<SprintResult, Sprint> map = new HashMap<>();
        sprintResultsList.forEach(sprintResult -> map.put(sprintResult, sprintService.findById(sprintResult.getSprintId())));

        model.addAttribute("project", projectService.findById(id));
        model.addAttribute("sprintResults", sprintResultsList);
        model.addAttribute("map", map);

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

    @GetMapping("/search")
    public String getProjectsBySearch(Model model, @RequestParam String keywords) {
        Map<Project, User> map = new HashMap<>();
        projectService.findBySearch(keywords).forEach(project -> map.put(project, userService.findById(project.getOwnerId())));

        model.addAttribute("projects", projectService.findBySearch(keywords));
        model.addAttribute("map", map);

        log.debug("GET: Projects by search: {}", projectService.findBySearch(keywords));
        return "all-projects";
    }
}
