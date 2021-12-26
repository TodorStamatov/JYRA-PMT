package course.spring.jyra.web;

import course.spring.jyra.model.Task;
import course.spring.jyra.model.User;
import course.spring.jyra.service.SprintService;
import course.spring.jyra.service.TaskService;
import course.spring.jyra.service.UserService;
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

    @Autowired
    public TaskController(TaskService taskService, UserService userService, SprintService sprintService) {
        this.taskService = taskService;
        this.userService = userService;
        this.sprintService = sprintService;
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

    @PostMapping
    public String addTask(@ModelAttribute("task") Task task) {
        taskService.create(task);
        log.debug("POST: Task: {}", task);
        return "redirect:/tasks";
    }

    @DeleteMapping
    public String deleteProject(@RequestParam("delete") String id) {
        Task task = taskService.findById(id);
        log.debug("DELETE: Task: {}", task);
        taskService.deleteById(id);
        return "redirect:/tasks";
    }

    @GetMapping("/{taskId}")
    public String getTaskById(Model model, @PathVariable("taskId") String id) {
        Task task = taskService.findById(id);

        model.addAttribute("task", task);
        model.addAttribute("developersAssigned", task.getDevelopersAssignedIds().stream().map(userService::findById).collect(Collectors.toList()));
        model.addAttribute("reporter", userService.findById(task.getAddedById()));
        model.addAttribute("sprint", sprintService.findById(task.getSprintId()));

        log.debug("GET: Task with Id=%s : {}", id, taskService.findById(id));
        return "single-task";
    }

    @PutMapping
    public String updateTask(@RequestParam("update") String id) {
        Task task = taskService.findById(id);
        log.debug("UPDATE: Task: {}", task);
        taskService.update(task);
        return "redirect:/tasks";
    }
}
