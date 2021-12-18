package course.spring.jyra.init;

import course.spring.jyra.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final List<User> DEFAULT_USERS = List.of(
            Administrator.builder().firstName("Ivan").lastName("Todorov").email("ivan@example.com").password("Ivan1!").username("vankata").build(),
            Administrator.builder().firstName("Admin").lastName("Admin").email("admin@example.com").password("Admin1!").username("admin").build()
    );
    private final List<Developer> DEFAULT_DEVS = List.of(
            Developer.builder().firstName("Bogdan").lastName("Kosev").email("bogdan@example.com").password("Bogdan1!").username("bogi4").build(),
            Developer.builder().firstName("Todor").lastName("Stamatov").email("todor@example.com").password("Todor1!").username("toshko").build()
    );
    ProductOwner productOwner = ProductOwner.builder().firstName("Ivailo").lastName("Panayotov").email("ivailo@example.com").password("Ivailo1!").username("ivaka").build();
    private final List<Task> DEFAULT_TASKS = List.of(
            new Task(Kind.RESEARCH, "Task1", DEFAULT_USERS.get(0), 5, DEFAULT_DEVS, "tag1,tag2"),
            new Task(Kind.DESIGN, "Task2", DEFAULT_USERS.get(1), 7, DEFAULT_DEVS, "tag1,tag2")
    );
    private final Sprint DEFAULT_SPRINT = new Sprint(productOwner);

    private final List<Project> DEFAULT_PROJECTS = List.of(
            new Project("Project1", productOwner, DEFAULT_DEVS, DEFAULT_SPRINT),
            new Project("Project2", productOwner, DEFAULT_DEVS, DEFAULT_SPRINT)
    );

    private final List<TaskResult> DEFAULT_TASK_RESULTS = List.of(
            new TaskResult(DEFAULT_TASKS.get(0), 3, DEFAULT_USERS.get(0)),
            new TaskResult(DEFAULT_TASKS.get(1), 8, DEFAULT_USERS.get(1))
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


    }
}
