package course.spring.jyra.init;

import course.spring.jyra.model.*;
import course.spring.jyra.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
            Administrator.builder().firstName("Ivan").lastName("Todorov").email("ivan@example.com").password("Ivan1!").username("vankata").roles(List.of(Role.ADMIN)).imageUrl("/images/vankata.jpg").build(),
            Administrator.builder().firstName("Admin").lastName("Admin").email("admin@example.com").password("Admin1!").username("admin").roles(List.of(Role.ADMIN)).imageUrl("/images/default.jpg").build()
    );
    private final List<Developer> DEFAULT_DEVS = List.of(
            Developer.builder().firstName("Bogdan").lastName("Kosev").email("bogdan@example.com").password("Bogdan1!").username("bogi4").roles(List.of(Role.DEVELOPER)).imageUrl("/images/bogi4.jpg").build(),
            Developer.builder().firstName("Todor").lastName("Stamatov").email("todor@example.com").password("Todor1!").username("toshko").roles(List.of(Role.DEVELOPER)).imageUrl("/images/toshko.jpg").build()
    );
    private final ProductOwner DEFAULT_OWNER = ProductOwner.builder().firstName("Ivailo").lastName("Panayotov").email("ivailo@example.com").password("Ivailo1!").username("ivaka").roles(List.of(Role.PRODUCT_OWNER)).imageUrl("/images/ivaka.jpg").build();

    private final List<Sprint> DEFAULT_SPRINTS = List.of(
            Sprint.builder().title("Sprint1").owner(DEFAULT_OWNER).developers(DEFAULT_DEVS).build(),
            Sprint.builder().title("Sprint2").owner(DEFAULT_OWNER).developers(DEFAULT_DEVS).build(),
            Sprint.builder().title("Sprint3").owner(DEFAULT_OWNER).developers(DEFAULT_DEVS).build()
    );

    private final List<Task> DEFAULT_TASKS_1 = List.of(
            Task.builder().taskType(TaskType.TASK).title("Task1").addedBy(DEFAULT_ADMINS.get(0)).estimatedEffort(5)
                    .sprint(DEFAULT_SPRINTS.get(0)).developersAssigned(DEFAULT_DEVS).description("Task 1 desc")
                    .tags("tag1,tag2").build(),
            Task.builder().taskType(TaskType.STORY).title("Task2").addedBy(DEFAULT_ADMINS.get(1)).estimatedEffort(7)
                    .sprint(DEFAULT_SPRINTS.get(0)).developersAssigned(DEFAULT_DEVS).description("Task 2 desc")
                    .tags("tag1,tag2").build()
    );

    private final List<Task> DEFAULT_TASKS_2 = List.of(
            Task.builder().taskType(TaskType.TASK).title("Task3").addedBy(DEFAULT_ADMINS.get(0)).estimatedEffort(1)
                    .sprint(DEFAULT_SPRINTS.get(1)).developersAssigned(DEFAULT_DEVS).description("Task 3 desc")
                    .tags("tag1,tag2").build(),
            Task.builder().taskType(TaskType.STORY).title("Task4").addedBy(DEFAULT_ADMINS.get(1)).estimatedEffort(1)
                    .sprint(DEFAULT_SPRINTS.get(1)).developersAssigned(DEFAULT_DEVS).description("Task 4 desc")
                    .tags("tag1,tag2").build()
    );

    private final List<Project> DEFAULT_PROJECTS = List.of(
            Project.builder().title("Project1").description("Project1 desc").owner(DEFAULT_OWNER)
                    .developers(DEFAULT_DEVS).currentSprint(DEFAULT_SPRINTS.get(0)).tasksBacklog(DEFAULT_TASKS_1)
                    .tags("tag1,tag2").build(),
            Project.builder().title("Project2").description("Project2 desc").owner(DEFAULT_OWNER)
                    .developers(DEFAULT_DEVS).currentSprint(DEFAULT_SPRINTS.get(1)).tasksBacklog(DEFAULT_TASKS_2)
                    .tags("tag1,tag2").build()
    );

    private final List<TaskResult> DEFAULT_TASK_RESULTS_1 = List.of(
            TaskResult.builder().task(DEFAULT_TASKS_1.get(0)).actualEffort(3).verifiedBy(DEFAULT_ADMINS.get(0))
                    .resultsDescription("Task Result 1 desc").build(),
            TaskResult.builder().task(DEFAULT_TASKS_1.get(1)).actualEffort(5).verifiedBy(DEFAULT_ADMINS.get(0))
                    .resultsDescription("Task Result 2 desc").build()
    );

    private final List<TaskResult> DEFAULT_TASK_RESULTS_2 = List.of(
            TaskResult.builder().task(DEFAULT_TASKS_2.get(0)).actualEffort(7).verifiedBy(DEFAULT_ADMINS.get(1))
                    .resultsDescription("Task Result 3 desc").build(),
            TaskResult.builder().task(DEFAULT_TASKS_2.get(1)).actualEffort(1).verifiedBy(DEFAULT_ADMINS.get(1))
                    .resultsDescription("Task Result 4 desc").build()
    );

    private final List<SprintResult> DEFAULT_SPRINT_RESULTS = List.of(
            SprintResult.builder().sprint(DEFAULT_SPRINTS.get(0)).resultsDescription("Sprint result 1 desc")
                    .taskResults(DEFAULT_TASK_RESULTS_1).build(),
            SprintResult.builder().sprint(DEFAULT_SPRINTS.get(1)).resultsDescription("Sprint result 2 desc")
                    .taskResults(DEFAULT_TASK_RESULTS_2).build()
    );

    private final List<ProjectResult> DEFAULT_PROJECT_RESULTS = List.of(
            ProjectResult.builder().project(DEFAULT_PROJECTS.get(0)).duration(10)
                    .sprintResultList(DEFAULT_SPRINT_RESULTS).build()
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userService.count() == 0) {
            log.info("Successfully created admins: {}", DEFAULT_ADMINS.stream().map(userService::create).collect(Collectors.toList()));
            log.info("Successfully created devs: {}", DEFAULT_DEVS.stream().map(userService::create).collect(Collectors.toList()));
            log.info("Successfully created product owner: {}", userService.create(DEFAULT_OWNER));
        }
        if (taskService.count() == 0) {
            log.info("Successfully created tasks1: {}", DEFAULT_TASKS_1.stream().map(taskService::create).collect(Collectors.toList()));
            log.info("Successfully created tasks2: {}", DEFAULT_TASKS_2.stream().map(taskService::create).collect(Collectors.toList()));
        }
        if (sprintService.count() == 0) {
            log.info("Successfully created sprints: {}", DEFAULT_SPRINTS.stream().map(sprintService::create).collect(Collectors.toList()));
        }
        if (projectService.count() == 0) {
            log.info("Successfully created projects: {}", DEFAULT_PROJECTS.stream().map(projectService::create).collect(Collectors.toList()));
        }
        if (projectResultService.count() == 0) {
            log.info("Successfully created project results: {}", DEFAULT_PROJECT_RESULTS.stream().map(projectResultService::create).collect(Collectors.toList()));
        }
        if (sprintResultService.count() == 0) {
            log.info("Successfully created sprint results: {}", DEFAULT_SPRINT_RESULTS.stream().map(sprintResultService::create).collect(Collectors.toList()));
        }
        if (taskResultService.count() == 0) {
            log.info("Successfully created task results 1: {}", DEFAULT_TASK_RESULTS_1.stream().map(taskResultService::create).collect(Collectors.toList()));
            log.info("Successfully created task results 2: {}", DEFAULT_TASK_RESULTS_2.stream().map(taskResultService::create).collect(Collectors.toList()));
        }
    }
}
