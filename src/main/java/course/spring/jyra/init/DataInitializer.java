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
    private final BoardService boardService;

    @Autowired
    public DataInitializer(UserService userService, TaskService taskService, SprintService sprintService, ProjectService projectService, ProjectResultService projectResultService, SprintResultService sprintResultService, TaskResultService taskResultService, BoardService boardService) {
        this.userService = userService;
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.projectService = projectService;
        this.projectResultService = projectResultService;
        this.sprintResultService = sprintResultService;
        this.taskResultService = taskResultService;
        this.boardService = boardService;
    }

    private List<User> defaultAdmins = List.of(
            Administrator.builder().firstName("Ivan").lastName("Todorov").email("ivan@example.com").password("Ivan1!").username("vankata").roles(List.of(Role.ADMIN)).imageUrl("/images/vankata.jpg").build(),
            Administrator.builder().firstName("Admin").lastName("Admin").email("admin@example.com").password("Admin1!").username("admin").roles(List.of(Role.ADMIN)).imageUrl("/images/default.jpg").build()
    );

    private List<Developer> defaultDevs = List.of(
            Developer.builder().firstName("Bogdan").lastName("Kosev").email("bogdan@example.com").password("Bogdan1!").username("bogi4").roles(List.of(Role.DEVELOPER)).imageUrl("/images/bogi4.jpg").build(),
            Developer.builder().firstName("Todor").lastName("Stamatov").email("todor@example.com").password("Todor1!").username("toshko").roles(List.of(Role.DEVELOPER)).imageUrl("/images/toshko.jpg").build()
    );
    private ProductOwner defaultOwner = ProductOwner.builder().firstName("Ivailo").lastName("Panayotov").email("ivailo@example.com").password("Ivailo1!").username("ivaka").roles(List.of(Role.PRODUCT_OWNER)).imageUrl("/images/ivaka.jpg").build();

    private List<Sprint> defaultSprints;
    private List<Task> defaultTasks1;
    private List<Task> defaultTasks2;
    private List<Task> defaultTasks3;
    private List<Project> defaultProjects;
    private List<TaskResult> defaultTaskResults1;
    private List<TaskResult> defaultTaskResults2;
    private List<SprintResult> defaultSprintResults;
    private ProjectResult defaultProjectResult;
    private List<Board> defaultBoards;
    private boolean updateSprint = false, updateTask = false, updateProject = false, updateDev = false, updateOwner = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userService.count() == 0) {
            defaultAdmins = defaultAdmins.stream().map(userService::create).collect(Collectors.toList());
            defaultDevs = defaultDevs.stream().map(userService::create).map(user -> (Developer) user).collect(Collectors.toList());
            defaultOwner = (ProductOwner) userService.create(defaultOwner);

            log.info("Successfully created admins: {}", defaultAdmins);
            log.info("Successfully created devs: {}", defaultDevs);
            log.info("Successfully created product owner: {}", defaultOwner);

            updateDev = true;
            updateOwner = true;
        }

        if (projectService.count() == 0) {
            defaultProjects = List.of(
                    Project.builder().title("Project1").description("Project1 desc").ownerId(defaultOwner.getId())
                            .developersIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .tags("tag1,tag2").build(),
                    Project.builder().title("Project2").description("Project2 desc").ownerId(defaultOwner.getId())
                            .developersIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .tags("tag1,tag2").build()
            );

            defaultProjects = defaultProjects.stream().map(projectService::create).collect(Collectors.toList());

            log.info("Successfully created projects: {}", defaultProjects);

            updateProject = true;
        }

        if (sprintService.count() == 0) {
            defaultSprints = List.of(
                    Sprint.builder().title("Sprint1").ownerId(defaultOwner.getId())
                            .developersIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .projectId(defaultProjects.get(0).getId()).build(),
                    Sprint.builder().title("Sprint2").ownerId(defaultOwner.getId())
                            .developersIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .projectId(defaultProjects.get(0).getId()).build(),
                    Sprint.builder().title("Sprint3").ownerId(defaultOwner.getId())
                            .developersIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .projectId(defaultProjects.get(1).getId()).build()
            );

            defaultSprints = defaultSprints.stream().map(sprintService::create).collect(Collectors.toList());

            log.info("Successfully created sprints: {}", defaultSprints);

            updateSprint = true;
        }

        if (taskService.count() == 0) {
            defaultTasks1 = List.of(
                    Task.builder().taskType(TaskType.TASK).title("Task1").addedById(defaultAdmins.get(0).getId()).estimatedEffort(5)
                            .sprintId(defaultSprints.get(0).getId()).
                            developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 1 desc").tags("tag1,tag2").status(TaskStatus.DONE).build(),
                    Task.builder().taskType(TaskType.STORY).title("Task2").addedById(defaultAdmins.get(1).getId()).estimatedEffort(7)
                            .sprintId(defaultSprints.get(0).getId())
                            .developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 2 desc").tags("tag1,tag2").status(TaskStatus.DONE).build()
            );

            defaultTasks2 = List.of(
                    Task.builder().taskType(TaskType.EPIC).title("Task3").addedById(defaultAdmins.get(0).getId()).estimatedEffort(1)
                            .sprintId(defaultSprints.get(1).getId())
                            .developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 3 desc").tags("tag1,tag2").status(TaskStatus.DONE).build(),
                    Task.builder().taskType(TaskType.SUBTASK).title("Task4").addedById(defaultAdmins.get(1).getId()).estimatedEffort(5)
                            .sprintId(defaultSprints.get(1).getId())
                            .developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 4 desc").tags("tag1,tag2").status(TaskStatus.DONE).build()
            );

            defaultTasks3 = List.of(
                    Task.builder().taskType(TaskType.TASK).title("Task5").addedById(defaultAdmins.get(0).getId()).estimatedEffort(3)
                            .sprintId(defaultSprints.get(2).getId())
                            .developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 5 desc").tags("tag1,tag2").status(TaskStatus.IN_PROGRESS).build(),
                    Task.builder().taskType(TaskType.BUG).title("Task6").addedById(defaultAdmins.get(1).getId()).estimatedEffort(8)
                            .sprintId(defaultSprints.get(2).getId())
                            .developersAssignedIds(defaultDevs.stream().map(Developer::getId).collect(Collectors.toList()))
                            .description("Task 6 desc").tags("tag1,tag2").build()
            );

            defaultTasks1 = defaultTasks1.stream().map(taskService::create).collect(Collectors.toList());
            defaultTasks2 = defaultTasks2.stream().map(taskService::create).collect(Collectors.toList());
            defaultTasks3 = defaultTasks3.stream().map(taskService::create).collect(Collectors.toList());

            log.info("Successfully created tasks1: {}", defaultTasks1);
            log.info("Successfully created tasks2: {}", defaultTasks2);
            log.info("Successfully created tasks3: {}", defaultTasks3);


            updateTask = true;
        }

        if (taskResultService.count() == 0) {
            defaultTaskResults1 = List.of(
                    TaskResult.builder().taskId(defaultTasks1.get(0).getId()).actualEffort(3).verifiedById(defaultAdmins.get(0).getId())
                            .resultsDescription("Task Result 1 desc").build(),
                    TaskResult.builder().taskId(defaultTasks1.get(1).getId()).actualEffort(5).verifiedById(defaultAdmins.get(0).getId())
                            .resultsDescription("Task Result 2 desc").build()
            );

            defaultTaskResults2 = List.of(
                    TaskResult.builder().taskId(defaultTasks2.get(0).getId()).actualEffort(1).verifiedById(defaultAdmins.get(1).getId())
                            .resultsDescription("Task Result 3 desc").build(),
                    TaskResult.builder().taskId(defaultTasks2.get(1).getId()).actualEffort(7).verifiedById(defaultAdmins.get(1).getId())
                            .resultsDescription("Task Result 4 desc").build()
            );

            defaultTaskResults1 = defaultTaskResults1.stream().map(taskResultService::create).collect(Collectors.toList());
            defaultTaskResults2 = defaultTaskResults2.stream().map(taskResultService::create).collect(Collectors.toList());

            log.info("Successfully created task results 1: {}", defaultTaskResults1);
            log.info("Successfully created task results 2: {}", defaultTaskResults2);
        }

        if (sprintResultService.count() == 0) {
            defaultSprintResults = List.of(
                    SprintResult.builder().sprintId(defaultSprints.get(0).getId()).resultsDescription("Sprint result 1 desc")
                            .taskResultsIds(defaultTaskResults1.stream().map(TaskResult::getId).collect(Collectors.toList()))
                            .build(),
                    SprintResult.builder().sprintId(defaultSprints.get(1).getId()).resultsDescription("Sprint result 2 desc")
                            .taskResultsIds(defaultTaskResults2.stream().map(TaskResult::getId).collect(Collectors.toList()))
                            .build()
            );

            defaultSprintResults = defaultSprintResults.stream().map(sprintResultService::create).collect(Collectors.toList());

            log.info("Successfully created sprint results: {}", defaultSprintResults);
        }

        if (projectResultService.count() == 0) {
            defaultProjectResult = ProjectResult.builder().projectId(defaultProjects.get(0).getId()).duration(10).resultsDescription("project result desc")
                    .sprintResultListIds(defaultSprintResults.stream().map(SprintResult::getId).collect(Collectors.toList()))
                    .build();

            defaultProjectResult = projectResultService.create(defaultProjectResult);

            log.info("Successfully created project result: {}", defaultProjectResult);
        }

//        Is not checked for mistakes
        if (boardService.count() == 0) {
            defaultBoards = List.of(
                    Board.builder().projectId(defaultProjects.get(0).getId()).sprintId(defaultSprints.get(0).getId())
                            .doneIds(defaultTasks1.stream().map(Task::getId).collect(Collectors.toList())).build(),
                    Board.builder().projectId(defaultProjects.get(1).getId()).sprintId(defaultSprints.get(2).getId())
                            .toDoIds(defaultTasks2.stream().map(Task::getId).collect(Collectors.toList())).build()
            );
            defaultBoards = defaultBoards.stream().map(boardService::create).collect(Collectors.toList());

            log.info("Successfully created boards: {}", defaultBoards);
        }

//        updated sprint
        if (updateSprint) {
            Sprint sprint1 = defaultSprints.get(0);
            Sprint sprint2 = defaultSprints.get(1);
            Sprint sprint3 = defaultSprints.get(2);
            defaultTasks1.forEach(task -> sprint1.getTasksIds().add(task.getId()));
            defaultTasks2.forEach(task -> sprint2.getTasksIds().add(task.getId()));
            defaultTasks3.forEach(task -> sprint3.getTasksIds().add(task.getId()));
            defaultTaskResults1.forEach(taskResult -> sprint1.getCompletedTaskResultsIds().add(taskResult.getId()));
            defaultTaskResults2.forEach(taskResult -> sprint2.getCompletedTaskResultsIds().add(taskResult.getId()));
            sprint1.setSprintResultId(defaultSprintResults.get(0).getId());
            sprint2.setSprintResultId(defaultSprintResults.get(1).getId());
            sprintService.update(sprint1);
            sprintService.update(sprint2);
            sprintService.update(sprint3);
        }

//        updated task
        if (updateTask) {
            Task task1 = defaultTasks1.get(0);
            Task task2 = defaultTasks1.get(1);
            Task task3 = defaultTasks2.get(0);
            Task task4 = defaultTasks2.get(1);
            task1.setTaskResultId(defaultTaskResults1.get(0).getId());
            task2.setTaskResultId(defaultTaskResults1.get(1).getId());
            task3.setTaskResultId(defaultTaskResults2.get(0).getId());
            task4.setTaskResultId(defaultTaskResults2.get(1).getId());
            taskService.update(task1);
            taskService.update(task2);
            taskService.update(task3);
            taskService.update(task4);
        }

//        updated projects
        if (updateProject) {
            Project project1 = defaultProjects.get(0);
            Project project2 = defaultProjects.get(1);
            project2.setCurrentSprintId(defaultSprints.get(2).getId());
            defaultTasks3.forEach(task -> project2.getTasksBacklogIds().add(task.getId()));
            defaultSprintResults.forEach(sprintResult -> project1.getPreviousSprintResultsIds().add(sprintResult.getId()));
            project1.setProjectResultId(defaultProjectResult.getId());
            defaultTasks2.forEach(task -> project1.getTasksBacklogIds().add(task.getId()));
            project2.setCurrentSprintId(defaultSprints.get(2).getId());
            projectService.update(project1);
            projectService.update(project2);
        }

//        updated developers
        if (updateDev) {
            Developer developer1 = defaultDevs.get(0);
            Developer developer2 = defaultDevs.get(1);
            defaultTasks1.forEach(task -> developer1.getAssignedTasksIds().add(task.getId()));
            defaultTasks1.forEach(task -> developer2.getAssignedTasksIds().add(task.getId()));
            defaultTasks2.forEach(task -> developer1.getAssignedTasksIds().add(task.getId()));
            defaultTasks2.forEach(task -> developer2.getAssignedTasksIds().add(task.getId()));
            defaultTasks3.forEach(task -> developer1.getAssignedTasksIds().add(task.getId()));
            defaultTasks3.forEach(task -> developer2.getAssignedTasksIds().add(task.getId()));
            defaultTaskResults1.forEach(taskResult -> developer1.getCompletedTaskResultsIds().add(taskResult.getId()));
            defaultTaskResults1.forEach(taskResult -> developer2.getCompletedTaskResultsIds().add(taskResult.getId()));
            defaultTaskResults2.forEach(taskResult -> developer1.getCompletedTaskResultsIds().add(taskResult.getId()));
            defaultTaskResults2.forEach(taskResult -> developer2.getCompletedTaskResultsIds().add(taskResult.getId()));
            userService.update(developer1);
            userService.update(developer2);
        }

//        updated owner
        if (updateOwner) {
            ProductOwner productOwner = defaultOwner;
            defaultProjects.forEach(project -> productOwner.getProjectsIds().add(project.getId()));
            productOwner.getCompletedProjectResultsIds().add(defaultProjectResult.getId());
            userService.update(productOwner);
        }
    }
}
