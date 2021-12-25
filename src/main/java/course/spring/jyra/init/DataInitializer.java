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

    private List<User> DEFAULT_ADMINS = List.of(
            Administrator.builder().firstName("Ivan").lastName("Todorov").email("ivan@example.com").password("Ivan1!").username("vankata").roles(List.of(Role.ADMIN)).imageUrl("images/vankata.jpg").build(),
            Administrator.builder().firstName("Admin").lastName("Admin").email("admin@example.com").password("Admin1!").username("admin").roles(List.of(Role.ADMIN)).imageUrl("images/default.jpg").build()
    );

    private List<Developer> DEFAULT_DEVS = List.of(
            Developer.builder().firstName("Bogdan").lastName("Kosev").email("bogdan@example.com").password("Bogdan1!").username("bogi4").roles(List.of(Role.DEVELOPER)).imageUrl("images/bogi4.jpg").build(),
            Developer.builder().firstName("Todor").lastName("Stamatov").email("todor@example.com").password("Todor1!").username("toshko").roles(List.of(Role.DEVELOPER)).imageUrl("images/toshko.jpg").build()
    );
    private ProductOwner DEFAULT_OWNER = ProductOwner.builder().firstName("Ivailo").lastName("Panayotov").email("ivailo@example.com").password("Ivailo1!").username("ivaka").roles(List.of(Role.PRODUCT_OWNER)).imageUrl("images/ivaka.jpg").build();

//
//    private void setTasksToDevs() {
//        DEFAULT_DEVS.get(0).setAssignedTasksIds(DEFAULT_TASKS_1.stream().map(Task::getId).collect(Collectors.toList()));
//        DEFAULT_DEVS.get(0).setCompletedTaskResultsIds(DEFAULT_TASK_RESULTS_1.stream().map(TaskResult::getId).collect(Collectors.toList()));
//        DEFAULT_DEVS.get(1).setAssignedTasksIds(DEFAULT_TASKS_2.stream().map(Task::getId).collect(Collectors.toList()));
//        DEFAULT_DEVS.get(1).setCompletedTaskResultsIds(DEFAULT_TASK_RESULTS_2.stream().map(TaskResult::getId).collect(Collectors.toList()));
//    }
//
//    private void setProjectsToOwner() {
//        DEFAULT_OWNER.setProjectsIds(DEFAULT_PROJECTS.stream().map(Project::getId).collect(Collectors.toList()));
//        DEFAULT_OWNER.setCompletedProjectResultsIds(DEFAULT_PROJECT_RESULTS.stream().map(ProjectResult::getId).collect(Collectors.toList()));
//    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userService.count() == 0) {
            DEFAULT_ADMINS = DEFAULT_ADMINS.stream().map(userService::create).collect(Collectors.toList());
            DEFAULT_DEVS = DEFAULT_DEVS.stream().map(userService::create).map(user -> (Developer) user).collect(Collectors.toList());
            DEFAULT_OWNER = (ProductOwner) userService.create(DEFAULT_OWNER);

            log.info("Successfully created admins: {}", DEFAULT_ADMINS);
            log.info("Successfully created devs: {}", DEFAULT_DEVS);
            log.info("Successfully created product owner: {}", DEFAULT_OWNER);
        }

        List<Sprint> DEFAULT_SPRINTS = List.of(
                Sprint.builder().title("Sprint1").ownerId(DEFAULT_OWNER.getId())
                        .developersIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList())).build(),
                Sprint.builder().title("Sprint2").ownerId(DEFAULT_OWNER.getId())
                        .developersIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList())).build(),
                Sprint.builder().title("Sprint3").ownerId(DEFAULT_OWNER.getId())
                        .developersIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList())).build()
        );

        if (sprintService.count() == 0) {
            DEFAULT_SPRINTS = DEFAULT_SPRINTS.stream().map(sprintService::create).collect(Collectors.toList());

            log.info("Successfully created sprints: {}", DEFAULT_SPRINTS);
        }

        List<Task> DEFAULT_TASKS_1 = List.of(
                Task.builder().taskType(TaskType.TASK).title("Task1").addedById(DEFAULT_ADMINS.get(0).getId()).estimatedEffort(5)
                        .sprintId(DEFAULT_SPRINTS.get(0).getId()).
                        developersAssignedIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .description("Task 1 desc").tags("tag1,tag2").build(),
                Task.builder().taskType(TaskType.STORY).title("Task2").addedById(DEFAULT_ADMINS.get(1).getId()).estimatedEffort(7)
                        .sprintId(DEFAULT_SPRINTS.get(0).getId())
                        .developersAssignedIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .description("Task 2 desc").tags("tag1,tag2").build()
        );

//        Here we add the sprint to the task and later we need to make the reverse binding - add the tasks (once they are created) to this sprints
        List<Task> DEFAULT_TASKS_2 = List.of(
                Task.builder().taskType(TaskType.TASK).title("Task3").addedById(DEFAULT_ADMINS.get(0).getId()).estimatedEffort(1)
                        .sprintId(DEFAULT_SPRINTS.get(1).getId())
                        .developersAssignedIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .description("Task 3 desc").tags("tag1,tag2").build(),
                Task.builder().taskType(TaskType.STORY).title("Task4").addedById(DEFAULT_ADMINS.get(1).getId()).estimatedEffort(1)
                        .sprintId(DEFAULT_SPRINTS.get(1).getId())
                        .developersAssignedIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .description("Task 4 desc").tags("tag1,tag2").build()
        );

        if (taskService.count() == 0) {
            DEFAULT_TASKS_1 = DEFAULT_TASKS_1.stream().map(taskService::create).collect(Collectors.toList());
            DEFAULT_TASKS_2 = DEFAULT_TASKS_2.stream().map(taskService::create).collect(Collectors.toList());

            log.info("Successfully created tasks1: {}", DEFAULT_TASKS_1);
            log.info("Successfully created tasks2: {}", DEFAULT_TASKS_2);
        }

//        The reverse binding

        //TODO: update sprintResultsIds and projectResultId
        List<Project> DEFAULT_PROJECTS = List.of(
                Project.builder().title("Project1").description("Project1 desc").ownerId(DEFAULT_OWNER.getId())
                        .developersIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .currentSprint(DEFAULT_SPRINTS.get(0))
                        .tasksBacklogIds(DEFAULT_TASKS_1.stream().map(Task::getAddedById).collect(Collectors.toList()))
                        .tags("tag1,tag2").build(),
                Project.builder().title("Project2").description("Project2 desc").ownerId(DEFAULT_OWNER.getId())
                        .developersIds(DEFAULT_DEVS.stream().map(Developer::getId).collect(Collectors.toList()))
                        .currentSprint(DEFAULT_SPRINTS.get(1))
                        .tasksBacklogIds(DEFAULT_TASKS_2.stream().map(Task::getAddedById).collect(Collectors.toList()))
                        .tags("tag1,tag2").build()
        );

        if (projectService.count() == 0) {
            log.info("Successfully created projects: {}", DEFAULT_PROJECTS.stream().map(projectService::create).collect(Collectors.toList()));
        }

        List<TaskResult> DEFAULT_TASK_RESULTS_1 = List.of(
                TaskResult.builder().taskId(DEFAULT_TASKS_1.get(0).getId()).actualEffort(3).verifiedById(DEFAULT_ADMINS.get(0).getId())
                        .resultsDescription("Task Result 1 desc").build(),
                TaskResult.builder().taskId(DEFAULT_TASKS_1.get(1).getId()).actualEffort(5).verifiedById(DEFAULT_ADMINS.get(0).getId())
                        .resultsDescription("Task Result 2 desc").build()
        );

        List<TaskResult> DEFAULT_TASK_RESULTS_2 = List.of(
                TaskResult.builder().taskId(DEFAULT_TASKS_2.get(0).getId()).actualEffort(7).verifiedById(DEFAULT_ADMINS.get(1).getId())
                        .resultsDescription("Task Result 3 desc").build(),
                TaskResult.builder().taskId(DEFAULT_TASKS_2.get(1).getId()).actualEffort(1).verifiedById(DEFAULT_ADMINS.get(1).getId())
                        .resultsDescription("Task Result 4 desc").build()
        );

        if (taskResultService.count() == 0) {
            log.info("Successfully created task results 1: {}", DEFAULT_TASK_RESULTS_1.stream().map(taskResultService::create).collect(Collectors.toList()));
            log.info("Successfully created task results 2: {}", DEFAULT_TASK_RESULTS_2.stream().map(taskResultService::create).collect(Collectors.toList()));
        }

        List<SprintResult> DEFAULT_SPRINT_RESULTS = List.of(
                SprintResult.builder().sprintId(DEFAULT_SPRINTS.get(0).getId()).resultsDescription("Sprint result 1 desc")
                        .taskResultsIds(DEFAULT_TASK_RESULTS_1.stream().map(TaskResult::getId).collect(Collectors.toList()))
                        .build(),
                SprintResult.builder().sprintId(DEFAULT_SPRINTS.get(1).getId()).resultsDescription("Sprint result 2 desc")
                        .taskResultsIds(DEFAULT_TASK_RESULTS_2.stream().map(TaskResult::getId).collect(Collectors.toList()))
                        .build()
        );

        if (sprintResultService.count() == 0) {
            log.info("Successfully created sprint results: {}", DEFAULT_SPRINT_RESULTS.stream().map(sprintResultService::create).collect(Collectors.toList()));
        }

        List<ProjectResult> DEFAULT_PROJECT_RESULTS = List.of(
                ProjectResult.builder().projectId(DEFAULT_PROJECTS.get(0).getId()).duration(10).resultsDescription("project result desc")
                        .sprintResultListIds(DEFAULT_SPRINT_RESULTS.stream().map(SprintResult::getId).collect(Collectors.toList()))
                        .build()
        );

        if (projectResultService.count() == 0) {
            log.info("Successfully created project results: {}", DEFAULT_PROJECT_RESULTS.stream().map(projectResultService::create).collect(Collectors.toList()));
        }

//        updated sprint
        Sprint sprint1 = DEFAULT_SPRINTS.get(0);
        Sprint sprint2 = DEFAULT_SPRINTS.get(1);
        DEFAULT_TASKS_1.forEach(task -> sprint1.getTasksIds().add(task.getId()));
        DEFAULT_TASKS_2.forEach(task -> sprint2.getTasksIds().add(task.getId()));
        DEFAULT_TASK_RESULTS_1.forEach(taskResult -> sprint1.getCompletedTaskResultsIds().add(taskResult.getId()));
        DEFAULT_TASK_RESULTS_2.forEach(taskResult -> sprint2.getCompletedTaskResultsIds().add(taskResult.getId()));
        sprint1.setSprintResultId(DEFAULT_SPRINT_RESULTS.get(0).getId());
        sprint2.setSprintResultId(DEFAULT_SPRINT_RESULTS.get(1).getId());
        sprintService.update(sprint1);
        sprintService.update(sprint2);

//        updated task
        Task task1 = DEFAULT_TASKS_1.get(0);
        Task task2 = DEFAULT_TASKS_1.get(1);
        Task task3 = DEFAULT_TASKS_2.get(0);
        Task task4 = DEFAULT_TASKS_2.get(1);
        task1.setTaskResultId(DEFAULT_TASK_RESULTS_1.get(0).getId());
        task2.setTaskResultId(DEFAULT_TASK_RESULTS_1.get(1).getId());
        task3.setTaskResultId(DEFAULT_TASK_RESULTS_2.get(0).getId());
        task4.setTaskResultId(DEFAULT_TASK_RESULTS_2.get(1).getId());
        taskService.update(task1);
        taskService.update(task2);
        taskService.update(task3);
        taskService.update(task4);

//        updated projects
        Project project1 = DEFAULT_PROJECTS.get(0);
        DEFAULT_SPRINT_RESULTS.forEach(sprintResult -> project1.getPreviousSprintResultsIds().add(sprintResult.getId()));
        project1.setProjectResultId(DEFAULT_PROJECT_RESULTS.get(0).getId());
        projectService.update(project1);
    }
}
