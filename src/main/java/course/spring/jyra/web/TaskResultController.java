package course.spring.jyra.web;

import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.TaskResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/taskresults")
@Slf4j
public class TaskResultController {
    private final TaskResultService taskResultService;

    @Autowired
    public TaskResultController(TaskResultService taskResultService) {
        this.taskResultService = taskResultService;
    }

    @GetMapping
    public String getTaskResult(Model model) {
        model.addAttribute("task results", taskResultService.findAll());
        log.debug("GET: Task results: {}", taskResultService.findAll());
        return "taskresults";
    }

    @GetMapping("/{taskId}/task-result")
    public String getTaskResultByTaskId(Model model, @PathVariable("taskId") String id) {
        model.addAttribute("task result", taskResultService.findByTaskId(id));
        log.debug("GET: Result of task with Id:%s {}", id, taskResultService.findAll());
        return "taskresults";
    }

    @PostMapping
    public String addTaskResult(@ModelAttribute("taskResult") TaskResult taskResult) {
        taskResultService.create(taskResult);
        log.debug("POST: Task result: {}", taskResult);
        return "redirect:/taskresults";
    }

    @DeleteMapping
    public String deleteProjectResult(@RequestParam("delete") String id) {
        TaskResult taskResult = taskResultService.findById(id);
        log.debug("DELETE: Task result: {}", taskResult);
        taskResultService.deleteById(id);
        return "redirect:/taskresults";
    }

    @PutMapping
    public String updateTaskResult(@RequestParam("update") String id) {
        TaskResult taskResult = taskResultService.findById(id);
        log.debug("UPDATE: Task result: {}", taskResult);
        taskResultService.update(taskResult);
        return "redirect:/taskresults";
    }
}
