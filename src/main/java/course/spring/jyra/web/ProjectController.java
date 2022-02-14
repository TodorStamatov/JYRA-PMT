package course.spring.jyra.web;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ProjectResultService projectResultService;
    private final TaskService taskService;
    private final TaskResultService taskResultService;
    private final SprintResultService sprintResultService;
    private final UserService userService;
    private final SprintService sprintService;
    private final BoardService boardService;
    private final HtmlService htmlService;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectResultService projectResultService, TaskService taskService, TaskResultService taskResultService, SprintResultService sprintResultService, UserService userService, SprintService sprintService, BoardService boardService, HtmlService htmlService) {
        this.projectService = projectService;
        this.projectResultService = projectResultService;
        this.taskService = taskService;
        this.taskResultService = taskResultService;
        this.sprintResultService = sprintResultService;
        this.userService = userService;
        this.sprintService = sprintService;
        this.boardService = boardService;
        this.htmlService = htmlService;
    }

    @GetMapping
    public String getProjects(Model model) {
        Map<Project, User> map = new HashMap<>();
        projectService.findAll().forEach(project -> map.put(project, userService.findById(project.getOwnerId())));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canCreateProject = false;

        if (editor instanceof Administrator || editor instanceof ProductOwner) {
            canCreateProject = true;
        }

        model.addAttribute("canCreateProject", canCreateProject);
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("map", map);
        model.addAttribute("htmlService", htmlService);

        log.debug("GET: Projects: {}", projectService.findAll());
        return "all-projects";
    }

    @GetMapping("/create")
    public String getCreateProject(Model model) {
        if (!model.containsAttribute("project")) {
            model.addAttribute("project", new Project());
        }
        model.addAttribute("request", "POST");
        model.addAttribute("owners", userService.findAll().stream().filter(user -> user.getRoles().contains(Role.PRODUCT_OWNER)).collect(Collectors.toList()));
        model.addAttribute("developers", userService.findAll().stream().filter(user -> user.getRoles().contains(Role.DEVELOPER)).collect(Collectors.toList()));
        return "form-project";
    }

    @PostMapping("/create")
    public String addProject(@ModelAttribute Project project) {
        projectService.create(project);
        log.debug("POST: Project: {}", project);
        return "redirect:/projects";
    }

    @GetMapping("/{projectId}")
    public String getProjectById(Model model, @PathVariable("projectId") String id) {
        Project project = projectService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canFinishProject = false;

        if (project.getOwnerId().equals(editor.getId())) {
            canFinishProject = true;
        }

        model.addAttribute("project", project);
        model.addAttribute("owner", userService.findById(project.getOwnerId()));
        model.addAttribute("developers", project.getDevelopersIds().stream().map(userService::findById).collect(Collectors.toList()));
        model.addAttribute("canFinishProject", canFinishProject);
        model.addAttribute("htmlService", htmlService);

        log.debug("GET: Project with Id=%s : {}", id, projectService.findById(id));
        return "single-project";
    }

    //    This method combines Board and Current Sprint
    @GetMapping("/{projectId}/board")
    public String getProjectBoard(Model model, @PathVariable String projectId) {
        Board board = boardService.findByProjectId(projectId);
        Sprint sprint = sprintService.findById(projectService.findById(projectId).getCurrentSprintId());
        List<Task> toDo = sprint.getTasksIds().stream().map(taskService::findById).filter(task -> task.getStatus().equals(TaskStatus.TO_DO)).collect(Collectors.toList());
        List<Task> inProgress = sprint.getTasksIds().stream().map(taskService::findById).filter(task -> task.getStatus().equals(TaskStatus.IN_PROGRESS)).collect(Collectors.toList());
        List<Task> inReview = sprint.getTasksIds().stream().map(taskService::findById).filter(task -> task.getStatus().equals(TaskStatus.IN_REVIEW)).collect(Collectors.toList());
        List<Task> done = sprint.getTasksIds().stream().map(taskService::findById).filter(task -> task.getStatus().equals(TaskStatus.DONE)).collect(Collectors.toList());
        List<User> devs = sprint.getDevelopersIds().stream().map(userService::findById).collect(Collectors.toList());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canFinishProject = false;
        boolean canEditSprint = false;

        if (projectService.findById(projectId).getOwnerId().equals(editor.getId())) {
            canFinishProject = true;
        }

        if (projectService.findById(projectId).getOwnerId().equals(editor.getId()) || projectService.findById(projectId).getDevelopersIds().contains(editor.getId())) {
            canEditSprint = true;
        }

        model.addAttribute("canFinishProject", canFinishProject);
        model.addAttribute("canEditSprint", canEditSprint);
        model.addAttribute("sprint", sprint);
        model.addAttribute("project", projectService.findById(projectId));
        model.addAttribute("owner", userService.findById(sprint.getOwnerId()));
        model.addAttribute("devs", devs);
        model.addAttribute("toDoList", toDo);
        model.addAttribute("inProgressList", inProgress);
        model.addAttribute("inReviewList", inReview);
        model.addAttribute("doneList", done);

        return "single-project-board";
    }

    @GetMapping("/{projectId}/backlog")
    public String getProjectBacklog(Model model, @PathVariable String projectId) {
        List<Task> taskBacklog = new ArrayList<>();
        projectService.findById(projectId).getTasksBacklogIds().forEach(taskId -> taskBacklog.add(taskService.findById(taskId)));

        Map<Task, User> map = new HashMap<>();
        taskBacklog.forEach(task -> map.put(task, userService.findById(task.getAddedById())));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canManageSprintsAndTasks = false;

        if (projectService.findById(projectId).getOwnerId().equals(editor.getId()) || projectService.findById(projectId).getDevelopersIds().contains(editor.getId())) {
            canManageSprintsAndTasks = true;
        }

        model.addAttribute("canManageSprintsAndTasks", canManageSprintsAndTasks);
        model.addAttribute("project", projectService.findById(projectId));
        model.addAttribute("backlog", taskBacklog);
        model.addAttribute("map", map);

        log.debug("GET: Project with Id=%s : {}", projectId, projectService.findById(projectId));
        return "single-project-backlog";
    }

    @GetMapping("/{projectId}/sprint-results")
    public String getPrevSprintResults(Model model, @PathVariable String projectId) {
        List<SprintResult> sprintResultsList = new ArrayList<>();
        projectService.findById(projectId).getPreviousSprintResultsIds().forEach(sprintId -> sprintResultsList.add(sprintResultService.findById(sprintId)));
        Map<SprintResult, Sprint> map = new HashMap<>();
        sprintResultsList.forEach(sprintResult -> map.put(sprintResult, sprintService.findById(sprintResult.getSprintId())));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canFinishProject = false;
        boolean canEditSprintResults = false;

        if (projectService.findById(projectId).getOwnerId().equals(editor.getId())) {
            canFinishProject = true;
        }

        if (projectService.findById(projectId).getOwnerId().equals(editor.getId()) || projectService.findById(projectId).getDevelopersIds().contains(editor.getId())) {
            canEditSprintResults = true;
        }

        model.addAttribute("canEditSprintResults", canEditSprintResults);
        model.addAttribute("canFinishProject", canFinishProject);
        model.addAttribute("project", projectService.findById(projectId));
        model.addAttribute("sprintResults", sprintResultsList);
        model.addAttribute("map", map);

        log.debug("GET: Project with Id=%s : {}", projectId, projectService.findById(projectId));
        return "single-project-sprint-results";
    }

    @GetMapping("/edit")
    public String getEditProject(Model model, @RequestParam String projectId) {
        Project project = projectService.findById(projectId);
        if (!model.containsAttribute("project")) {
            model.addAttribute("project", project);
        }
        model.addAttribute("request", "PUT");
        model.addAttribute("owners", userService.findAll().stream().filter(user -> user.getRoles().contains(Role.PRODUCT_OWNER)).collect(Collectors.toList()));
        model.addAttribute("developers", userService.findAll().stream().filter(user -> user.getRoles().contains(Role.DEVELOPER)).collect(Collectors.toList()));

        return "form-project";
    }

    @PutMapping("/edit")
    public String updateProject(@RequestParam String projectId, @ModelAttribute Project project) {
        log.debug("UPDATE: Project: {}", project);
        projectService.update(project, projectId);
        return "redirect:/projects";
    }

    @GetMapping("/search")
    public String getProjectsBySearch(Model model, @RequestParam String keywords) {
        Map<Project, User> map = new HashMap<>();
        projectService.findBySearch(keywords).forEach(project -> map.put(project, userService.findById(project.getOwnerId())));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = userService.findByUsername(auth.getName());
        boolean canCreateProject = false;

        if (editor instanceof Administrator || editor instanceof ProductOwner) {
            canCreateProject = true;
        }

        model.addAttribute("canCreateProject", canCreateProject);
        model.addAttribute("projects", projectService.findBySearch(keywords));
        model.addAttribute("map", map);

        log.debug("GET: Projects by search: {}", projectService.findBySearch(keywords));
        return "all-projects";
    }

    @DeleteMapping("/delete")
    public String deleteProject(@RequestParam String projectId) {
        Project project = projectService.findById(projectId);
        log.debug("DELETE: Project: {}", project);

        // prepare the project for deletion
        if (project.getProjectResultId() != null) {
            projectResultService.deleteById(project.getProjectResultId());
        }

        if (project.getCurrentSprintId() != null) {
            sprintService.deleteById(project.getCurrentSprintId());
            Board board = boardService.findAll().stream().filter(b -> b.getSprintId().equals(project.getCurrentSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", project.getCurrentSprintId())));
            boardService.deleteById(board.getId());
        }

        for (String sprintResultId : project.getPreviousSprintResultsIds()) {
            String sprintId = sprintResultService.findById(sprintResultId).getSprintId();
            sprintResultService.deleteById(sprintResultId);
            sprintService.deleteById(sprintId);
        }

        for (String taskId : project.getTasksBacklogIds()) {
            if (taskService.findById(taskId).getTaskResultId() != null) {
                taskResultService.deleteById(taskService.findById(taskId).getTaskResultId());
            }
            taskService.deleteById(taskId);
        }

        projectService.deleteById(projectId);
        return "redirect:/projects";
    }
}
