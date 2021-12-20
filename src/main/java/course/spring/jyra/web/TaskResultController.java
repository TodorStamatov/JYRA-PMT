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

    @DeleteMapping
    public String deleteProjectResult(@RequestParam("delete") String id) {
        TaskResult taskResult = taskResultService.findById(id);
        log.debug("DELETE: Task result: {}", taskResult);
        taskResultService.deleteById(id);
        return "redirect:/taskresults";
    }
}
