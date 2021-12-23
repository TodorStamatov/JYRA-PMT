package course.spring.jyra.init;

import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final UserService userService;
    private final TaskService taskService;
    private final SprintService sprintService;
    private final ProjectService projectService;
    private final ProjectResultService projectResultService;
    private final SprintResultService sprintResultService;
    private final TaskResultService taskResultService;

    @Autowired
    public DataInitializer(UserService userService, TaskService taskService, SprintService sprintService, ProjectService projectService, ProjectResultService projectResultService, SprintResultService sprintResultService, TaskResultService taskResultService) {
        this.userService = userService;
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.projectService = projectService;
        this.projectResultService = projectResultService;
        this.sprintResultService = sprintResultService;
        this.taskResultService = taskResultService;
    }

    private final List<User> DEFAULT_ADMINS = List.of(
            Administrator.builder().firstName("Ivan").lastName("Todorov").email("ivan@example.com").password("Ivan1!").username("vankata").roles(List.of(Role.ADMIN)).build(),
            Administrator.builder().firstName("Admin").lastName("Admin").email("admin@example.com").password("Admin1!").username("admin").roles(List.of(Role.ADMIN)).build()
    );
    private final List<Developer> DEFAULT_DEVS = List.of(
            Developer.builder().firstName("Bogdan").lastName("Kosev").email("bogdan@example.com").password("Bogdan1!").username("bogi4").roles(List.of(Role.DEVELOPER)).build(),
            Developer.builder().firstName("Todor").lastName("Stamatov").email("todor@example.com").password("Todor1!").username("toshko").roles(List.of(Role.DEVELOPER)).build()
    );
    private final ProductOwner DEFAULT_OWNER = ProductOwner.builder().firstName("Ivailo").lastName("Panayotov").email("ivailo@example.com").password("Ivailo1!").username("ivaka").roles(List.of(Role.PRODUCT_OWNER)).build();

    private final List<Task> DEFAULT_TASKS = List.of(
            new Task(TaskType.SPIKE, "Task1", DEFAULT_ADMINS.get(0), 5, DEFAULT_DEVS, "tag1,tag2"),
            new Task(TaskType.UI, "Task2", DEFAULT_ADMINS.get(1), 7, DEFAULT_DEVS, "tag1,tag2")
    );
    private final Sprint DEFAULT_SPRINT = new Sprint(DEFAULT_OWNER);

    private final List<Project> DEFAULT_PROJECTS = List.of(
            new Project("Project1", DEFAULT_OWNER, DEFAULT_DEVS, DEFAULT_SPRINT),
            new Project("Project2", DEFAULT_OWNER, DEFAULT_DEVS, DEFAULT_SPRINT)
    );

    private final List<TaskResult> DEFAULT_TASK_RESULTS = List.of(
            new TaskResult(DEFAULT_TASKS.get(0), 3, DEFAULT_ADMINS.get(0)),
            new TaskResult(DEFAULT_TASKS.get(1), 8, DEFAULT_ADMINS.get(1))
    );

    private final List<SprintResult> DEFAULT_SPRINT_RESULTS = List.of(
            new SprintResult(DEFAULT_SPRINT, DEFAULT_TASK_RESULTS),
            new SprintResult(DEFAULT_SPRINT, DEFAULT_TASK_RESULTS)
    );

    private final List<ProjectResult> DEFAULT_PROJECT_RESULTS = List.of(
            new ProjectResult(DEFAULT_PROJECTS.get(0), 15, DEFAULT_SPRINT_RESULTS),
            new ProjectResult(DEFAULT_PROJECTS.get(1), 25, DEFAULT_SPRINT_RESULTS)
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userService.count() == 0) {
            log.info("Successfully created admins: {}", DEFAULT_ADMINS.stream().map(userService::create).collect(Collectors.toList()));
            log.info("Successfully created devs: {}", DEFAULT_DEVS.stream().map(userService::create).collect(Collectors.toList()));
            log.info("Successfully created product owner: {}", userService.create(DEFAULT_OWNER));
        }
        if(taskService.count() == 0){
            log.info("Successfully created tasks: {}",DEFAULT_TASKS.stream().map(taskService::create).collect(Collectors.toList()));
        }
        if(sprintService.count() == 0){
            log.info("Successfully created sprint: {}",sprintService.create(DEFAULT_SPRINT));
        }
        if(projectService.count() == 0){
            log.info("Successfully created projects: {}",DEFAULT_PROJECTS.stream().map(projectService::create).collect(Collectors.toList()));
        }
        if(projectResultService.count() == 0){
            log.info("Successfully created project results: {}",DEFAULT_PROJECT_RESULTS.stream().map(projectResultService::create).collect(Collectors.toList()));
        }
        if(sprintResultService.count() == 0){
            log.info("Successfully created sprint result: {}",DEFAULT_SPRINT_RESULTS.stream().map(sprintResultService::create).collect(Collectors.toList()));
        }
        if(taskResultService.count() == 0){
            log.info("Successfully created task result: {}",DEFAULT_TASK_RESULTS.stream().map(taskResultService::create).collect(Collectors.toList()));
        }
    }
}
