package course.spring.jyra.web;

import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final SprintService sprintService;
    private final ProjectService projectService;
    private final HtmlService htmlService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, SprintService sprintService, ProjectService projectService, HtmlService htmlService) {
        this.taskService = taskService;
        this.userService = userService;
        this.sprintService = sprintService;
        this.projectService = projectService;
        this.htmlService = htmlService;
    }

    @GetMapping
    public String getTasks(Model model) {
        Map<Task, User> map = new HashMap<>();
        taskService.findAll().forEach(task -> map.put(task, userService.findById(task.getAddedById())));

        model.addAttribute("tasks", taskService.findAll());
        model.addAttribute("map", map);
        log.debug("GET: Tasks: {}", taskService.findAll());
        return "all-tasks";
    }

    @GetMapping("/create")
    public String getCreateTask(Model model, @RequestParam String projectId) {
        Project project = projectService.findById(projectId);
        if (project.getCurrentSprintId() != null) {
            model.addAttribute("sprint", sprintService.findById(project.getCurrentSprintId()));
        }

        if (!model.containsAttribute("task")) {
            model.addAttribute("task", new Task());
        }

        model.addAttribute("request", "POST");
        model.addAttribute("developers", userService.findAll().stream().filter(u -> u.getRoles().contains(Role.DEVELOPER)).collect(Collectors.toList()));
        return "form-task";
    }

    @PostMapping("/create")
    public String addTask(@ModelAttribute Task task, @RequestParam String projectId) {
        taskService.create(task, projectId);
        log.debug("POST: Task: {}", task);
        return "redirect:/tasks";
    }

    @DeleteMapping("/delete")
    public String deleteProject(@RequestParam String taskId, @RequestParam String projectId) {
        Task task = taskService.findById(taskId);
        log.debug("DELETE: Task: {}", task);
        taskService.deleteById(taskId, projectId);
        return "redirect:/tasks";
    }

    @GetMapping("/{taskId}")
    public String getTaskById(Model model, @PathVariable("taskId") String id) {
        Task task = taskService.findById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canCreateResult = false;

        if (taskService.findById(id).getDevelopersAssignedIds().contains(editor.getId()) || taskService.findById(id).getAddedById().equals(editor.getId())) {
            canCreateResult = true;
        }

        model.addAttribute("canCreateResult", canCreateResult);
        model.addAttribute("task", task);
        model.addAttribute("developersAssigned", task.getDevelopersAssignedIds().stream().map(userService::findById).collect(Collectors.toList()));
        model.addAttribute("reporter", userService.findById(task.getAddedById()));
        model.addAttribute("sprint", sprintService.findById(task.getSprintId()));
        model.addAttribute("htmlService", htmlService);

        log.debug("GET: Task with Id=%s : {}", id, taskService.findById(id));
        return "single-task";
    }

    @GetMapping("/edit")
    public String getEditTask(Model model, @RequestParam String taskId, @RequestParam String projectId) {
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        if (!model.containsAttribute("task")) {
            model.addAttribute("task", task);
        }

        if (project.getCurrentSprintId() != null) {
            model.addAttribute("sprint", sprintService.findById(project.getCurrentSprintId()));
        }

        model.addAttribute("request", "PUT");
        model.addAttribute("developers", userService.findAll().stream().filter(user -> user.getRoles().contains(Role.DEVELOPER)).collect(Collectors.toList()));
        return "form-task";
    }

    @PutMapping("/edit")
    public String updateTask(@RequestParam String taskId, @RequestParam String projectId, @ModelAttribute Task task) {
        log.debug("UPDATE: Task: {}", task);
        taskService.update(task, taskId, projectId);
        return "redirect:/tasks";
    }

    @GetMapping("/search")
    public String getTasksBySearch(Model model, @RequestParam String keywords) {
        Map<Task, User> map = new HashMap<>();
        taskService.findBySearch(keywords).forEach(task -> map.put(task, userService.findById(task.getAddedById())));

        model.addAttribute("tasks", taskService.findBySearch(keywords));
        model.addAttribute("map", map);

        log.debug("GET: Tasks by search: {}", taskService.findBySearch(keywords));
        return "all-tasks";
    }
}
