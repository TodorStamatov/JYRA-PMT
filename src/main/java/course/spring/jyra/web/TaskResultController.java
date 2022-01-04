package course.spring.jyra.web;

import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.model.Task;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.model.User;
import course.spring.jyra.service.TaskResultService;
import course.spring.jyra.service.TaskService;
import course.spring.jyra.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/taskresults")
@Slf4j
public class TaskResultController {
    private final TaskResultService taskResultService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskResultController(TaskResultService taskResultService, TaskService taskService, UserService userService) {
        this.taskResultService = taskResultService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String getTaskResult(Model model) {
        Map<TaskResult, Task> taskMap = new HashMap<>();
        Map<TaskResult, User> userMap = new HashMap<>();
        taskResultService.findAll().forEach(taskResult -> {
            taskMap.put(taskResult, taskService.findById(taskResult.getTaskId()));
            userMap.put(taskResult, userService.findById(taskResult.getVerifiedById()));
        });

        model.addAttribute("taskResults", taskResultService.findAll());
        model.addAttribute("taskMap", taskMap);
        model.addAttribute("userMap", userMap);

        log.debug("GET: Task results: {}", taskResultService.findAll());
        return "all-task-results";
    }

    @GetMapping("/{taskId}/task-result")
    public String getTaskResultByTaskId(Model model, @PathVariable("taskId") String id) {
        TaskResult taskResult = taskResultService.findByTaskId(id);

        model.addAttribute("taskResult", taskResult);
        model.addAttribute("approver", userService.findById(taskResult.getVerifiedById()));
        model.addAttribute("task", taskService.findById(taskResult.getTaskId()));

        log.debug("GET: Result of task with Id:%s {}", id, taskResultService.findAll());
        return "single-task-result";
    }

    @GetMapping("/create")
    public String getCreateTaskResult(Model model) {
        if (!model.containsAttribute("taskResult")) {
            model.addAttribute("taskResult", new TaskResult());
        }
        model.addAttribute("request", "POST");
        return "form-task-result";
    }

    @PostMapping("/create")
    public String addTaskResult(@ModelAttribute TaskResult taskResult) {
        taskResultService.create(taskResult);
        log.debug("POST: Task result: {}", taskResult);
        return "redirect:/taskresults";
    }

    @GetMapping("/edit")
    public String getEditTaskResult(Model model, @RequestParam String taskResultId) {
        TaskResult taskResult = taskResultService.findById(taskResultId);
        if (!model.containsAttribute("taskResult")) {
            model.addAttribute("taskResult", taskResult);
        }
        model.addAttribute("request", "PUT");

        return "form-task-result";
    }

    @DeleteMapping("/delete")
    public String deleteTaskResult(@RequestParam String taskResultId) {
        TaskResult taskResult = taskResultService.findById(taskResultId);
        log.debug("DELETE: Task result: {}", taskResult);
        taskResultService.deleteById(taskResultId);
        return "redirect:/taskresults";
    }

    @PutMapping("/edit")
    public String updateTaskResult(@RequestParam String taskResultId, @ModelAttribute TaskResult taskResult) {
        log.debug("UPDATE: Task result: {}", taskResult);
        taskResultService.update(taskResult, taskResultId);
        return "redirect:/taskresults";
    }
}
