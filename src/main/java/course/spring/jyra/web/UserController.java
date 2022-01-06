package course.spring.jyra.web;

import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final TaskService taskService;
    private final TaskResultService taskResultService;
    private final ProjectService projectService;
    private final ProjectResultService projectResultService;

    @Autowired
    public UserController(UserService userService, TaskService taskService, TaskResultService taskResultService, ProjectService projectService, ProjectResultService projectResultService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskResultService = taskResultService;
        this.projectService = projectService;
        this.projectResultService = projectResultService;
    }

    @GetMapping
    public String getUsers(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        String userType = "";
        if (user instanceof Administrator) {
            userType = "ADMIN";
            Administrator admin = (Administrator) user;
            model.addAttribute("user", admin);
        }
        model.addAttribute("userType", userType);
        model.addAttribute("users", userService.findAll());
        log.debug("GET: Users: {}", userService.findAll());
        return "all-users";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String userId) {
        User user = userService.findById(userId);
        log.debug("DELETE: User: {}", user);
        userService.deleteById(userId);
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getUserById(Model model, @PathVariable("userId") String id) {
        User user = userService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        String canEdit = "";
        String userType = "";
        if (user instanceof Developer) {
            userType = "DEV";
            Developer dev = (Developer) user;

            List<Task> assignedTasks = dev.getAssignedTasksIds().stream().map(taskService::findById).collect(Collectors.toList());
            List<TaskResult> completedTaskResults = dev.getCompletedTaskResultsIds().stream().map(taskResultService::findById).collect(Collectors.toList());
            Map<Task, User> taskUserMapDev = new HashMap<>();
            Map<TaskResult, User> taskResultUserMapDev = new HashMap<>();
            Map<TaskResult, Task> taskResultTaskMapDev = new HashMap<>();
            assignedTasks.forEach(task -> taskUserMapDev.put(task, userService.findById(task.getAddedById())));
            completedTaskResults.forEach(taskResult -> taskResultUserMapDev.put(taskResult, userService.findById(taskResult.getVerifiedById())));
            completedTaskResults.forEach(taskResult -> taskResultTaskMapDev.put(taskResult, taskService.findById(taskResult.getTaskId())));

            model.addAttribute("user", dev);
            model.addAttribute("assignedTasks", assignedTasks);
            model.addAttribute("completedTaskResults", completedTaskResults);
            model.addAttribute("taskUserMapDev", taskUserMapDev);
            model.addAttribute("taskResultUserMapDev", taskResultUserMapDev);
            model.addAttribute("taskResultTaskMapDev", taskResultTaskMapDev);
        } else if (user instanceof Administrator) {
            userType = "ADMIN";
            Administrator admin = (Administrator) user;
            model.addAttribute("user", admin);
        } else if (user instanceof ProductOwner) {
            userType = "PO";
            ProductOwner po = (ProductOwner) user;

            List<Project> projects = po.getProjectsIds().stream().map(projectService::findById).collect(Collectors.toList());
            List<ProjectResult> completedProjectResults = po.getCompletedProjectResultsIds().stream().map(projectResultService::findById).collect(Collectors.toList());
            Map<ProjectResult, Project> projectMapPo = new HashMap<>();
            completedProjectResults.forEach(projectResult -> projectMapPo.put(projectResult, projectService.findById(projectResult.getProjectId())));

            model.addAttribute("user", po);
            model.addAttribute("projects", projects);
            model.addAttribute("completedProjectResults", completedProjectResults);
            model.addAttribute("projectMapPo", projectMapPo);
        } else {
            throw new InvalidEntityException(String.format("User with ID=%s is not one of the supported user types format.", id));
        }
        model.addAttribute("userType", userType);

        if (editor.getId().equals(user.getId()) || editor instanceof Administrator) {
            canEdit = "Yes";
        }


        model.addAttribute("canEdit", canEdit);

        log.debug("GET: User with Id=%s : {}", id, userService.findById(id));
        return "single-user";
    }

    @GetMapping("/edit")
    public String getEditUser(Model model, @RequestParam String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        String editorType = "";
        if (editor instanceof Administrator) {
            editorType = "ADMIN";
        }
        model.addAttribute("editorType", editorType);
        User user = userService.findById(userId);
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", user);
        }
        return "form-user";
    }

    @PutMapping("/edit")
    public String updateUser(@RequestParam String userId, @ModelAttribute User user) {
        log.debug("UPDATE: User: {}", user);
        userService.update(user, userId);
        return "redirect:/users";
    }

//    @GetMapping("/search")
//    public String getUsersBySearch(Model model, @RequestParam String keywords) {
//        model.addAttribute("users", userService.findBySearch(keywords));
//
//        log.debug("GET: Users by search: {}", userService.findBySearch(keywords));
//        return "all-users";
//    }
}
