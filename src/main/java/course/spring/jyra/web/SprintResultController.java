package course.spring.jyra.web;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
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
@RequestMapping("/sprintresults")
@Slf4j
public class SprintResultController {
    private final SprintResultService sprintResultService;
    private final SprintService sprintService;
    private final TaskResultService taskResultService;
    private final TaskService taskService;
    private final UserService userService;
    private final BoardService boardService;
    private final HtmlService htmlService;

    @Autowired
    public SprintResultController(SprintResultService sprintResultService, SprintService sprintService, TaskResultService taskResultService, TaskService taskService, UserService userService, BoardService boardService) {
        this.sprintResultService = sprintResultService;
        this.sprintService = sprintService;
        this.taskResultService = taskResultService;
        this.taskService = taskService;
        this.userService = userService;
        this.boardService = boardService;
        this.htmlService = htmlService;
    }

    @GetMapping
    public String getSprintResult(Model model) {
        Map<SprintResult, Sprint> map = new HashMap<>();
        sprintResultService.findAll().forEach(sprintResult -> map.put(sprintResult, sprintService.findById(sprintResult.getSprintId())));

        model.addAttribute("sprintResults", sprintResultService.findAll());
        model.addAttribute("map", map);

        log.debug("GET: Sprint results: {}", sprintResultService.findAll());
        return "all-sprint-results";
    }


    @GetMapping("/{sprintId}/sprint-result")
    public String getSprintResultBySprintId(Model model, @PathVariable String sprintId) {
        SprintResult sprintResult = sprintResultService.findBySprintId(sprintId);
        List<TaskResult> taskResultsList = sprintResult.getTaskResultsIds().stream().map(taskResultService::findById).collect(Collectors.toList());
        Map<TaskResult, Task> taskMap = new HashMap<>();
        Map<TaskResult, User> userMap = new HashMap<>();
        taskResultsList.forEach(taskResult -> {
            taskMap.put(taskResult, taskService.findById(taskResult.getTaskId()));
            userMap.put(taskResult, userService.findById(taskResult.getVerifiedById()));
        });

        model.addAttribute("sprintResult", sprintResult);
        model.addAttribute("sprint", sprintService.findById(sprintResult.getSprintId()));
        model.addAttribute("taskResults", taskResultsList);
        model.addAttribute("taskMap", taskMap);
        model.addAttribute("userMap", userMap);
        model.addAttribute("htmlService", htmlService);

        log.debug("GET: Sprint result: {}", sprintResultService.findBySprintId(sprintId));
        return "single-sprint-result";
    }

    @GetMapping("/create")
    public String getCreateSprintResult(Model model) {
        if (!model.containsAttribute("sprintResult")) {
            model.addAttribute("sprintResult", new SprintResult());
        }
        model.addAttribute("request", "POST");
        return "form-sprint-result";
    }

    @PostMapping("/create")
    public String addSprintResult(@ModelAttribute SprintResult sprintResult) {
        sprintResultService.create(sprintResult);

        Board board = boardService.findAll().stream().filter(b -> b.getSprintId().equals(sprintResult.getSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", sprintResult.getSprintId())));
        boardService.deleteById(board.getId());

        log.debug("POST: Sprint result: {}", sprintResult);
        return "redirect:/sprintresults";
    }

    @GetMapping("/edit")
    public String getEditSprintResult(Model model, @RequestParam String sprintResultId) {
        SprintResult sprintResult = sprintResultService.findById(sprintResultId);
        if (!model.containsAttribute("sprintResult")) {
            model.addAttribute("sprintResult", sprintResult);
        }
        model.addAttribute("request", "PUT");

        return "form-sprint-result";
    }

    @PutMapping("/edit")
    public String updateSprintResult(@RequestParam String sprintResultId, @ModelAttribute SprintResult sprintResult) {
        log.debug("UPDATE: Sprint result: {}", sprintResult);
        sprintResultService.update(sprintResult, sprintResultId);
        return "redirect:/sprintresults";
    }

    @DeleteMapping("/delete")
    public String deleteSprintResult(@RequestParam String sprintResultId) {
        SprintResult sprintResult = sprintResultService.findById(sprintResultId);
        log.debug("DELETE: Sprint result: {}", sprintResult);

        sprintResultService.deleteById(sprintResultId);

        Board board = Board.builder().projectId(sprintService.findById(sprintResult.getSprintId()).getProjectId()).sprintId(sprintResult.getSprintId()).build();
        boardService.create(board);

        return "redirect:/sprintresults";
    }
}
