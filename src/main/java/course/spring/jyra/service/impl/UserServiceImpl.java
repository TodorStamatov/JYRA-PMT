package course.spring.jyra.service.impl;

import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.ProjectService;
import course.spring.jyra.service.TaskService;
import course.spring.jyra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProjectService projectService, TaskService taskService, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(""));
        return users;
    }

    @Override
    public User findById(String id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found", id)));
        found.setPassword("");
        return found;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User '%s' not found.", username)));
    }

    @Override
    public User create(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(user1 -> {
                    throw new InvalidEntityException(String.format("User with username '%s' already exists.", user.getUsername()));
                });

        user.setId(null);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setActive(true);
        if (user.getRoles() == null || user.getRoles().size() == 0) {
            user.setRoles(List.of(Role.DEVELOPER));
        }

        User created = userRepository.insert(user);
        created.setPassword("");
        return created;
    }

    @Override
    public User update(User user, String oldId) {
        User oldUser = userRepository.findById(oldId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID='%s' not found.", oldId)));

        user.setId(oldUser.getId());
        user.setUsername(oldUser.getUsername());

        if (user.getPassword() == null || user.getPassword().length() == 0) {
            user.setPassword(oldUser.getPassword());

        } else {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User editor = findByUsername(auth.getName());
        if (!(editor instanceof Administrator) && !user.getPassword().equals(oldUser.getPassword())) {
            user.setStatus(UserStatus.ACTIVE);
        }
        user.setImageUrl(oldUser.getImageUrl());
        user.setCreated(oldUser.getCreated());
        user.setModified(LocalDateTime.now());

        if (user.getRoles().contains(Role.ADMIN)) {
            Administrator updated = Administrator.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
                    .email(user.getEmail()).username(user.getUsername()).password(user.getPassword())
                    .roles(user.getRoles()).contacts(user.getContacts()).status(user.getStatus())
                    .imageUrl(user.getImageUrl()).active(true).created(user.getCreated()).modified(user.getModified()).build();
            userRepository.save(updated);
            updated.setPassword("");
            return updated;
        } else if (user.getRoles().contains(Role.PRODUCT_OWNER)) {
            ProductOwner oldOwner = (ProductOwner) findById(oldId);
            ProductOwner updated = ProductOwner.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
                    .email(user.getEmail()).username(user.getUsername()).password(user.getPassword())
                    .roles(user.getRoles()).contacts(user.getContacts()).status(user.getStatus())
                    .imageUrl(user.getImageUrl()).active(true).created(user.getCreated()).modified(user.getModified())
                    .projectsIds(oldOwner.getProjectsIds()).completedProjectResultsIds(oldOwner.getCompletedProjectResultsIds()).build();
            userRepository.save(updated);
            updated.setPassword("");
            return updated;
        } else if (user.getRoles().contains(Role.DEVELOPER)) {
            Developer oldDev = (Developer) findById(oldId);
            Developer updated = Developer.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
                    .email(user.getEmail()).username(user.getUsername()).password(user.getPassword())
                    .roles(user.getRoles()).contacts(user.getContacts()).status(user.getStatus())
                    .imageUrl(user.getImageUrl()).active(true).created(user.getCreated()).modified(user.getModified())
                    .assignedTasksIds(oldDev.getAssignedTasksIds()).completedTaskResultsIds(oldDev.getCompletedTaskResultsIds()).build();
            userRepository.save(updated);
            updated.setPassword("");
            return updated;
        } else {
            throw new InvalidClientDataException(String.format("User with ID=%s has no roles set", user.getId()));
        }
    }

    @Override
    public User update(User user) {
//        userRepository is used here directly because if our public method is used, the users' passwords are set to null and this breaks the whole DB.
        User oldUser = userRepository.findById(user.getId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found", user.getId())));

        // prevent username changing
        if (user.getUsername() != null && !user.getUsername().equals(oldUser.getUsername())) {
            throw new InvalidEntityException("Username of a user could not ne changed.");
        }

        if (user.getPassword() == null || user.getPassword().length() == 0) {
            user.setPassword(oldUser.getPassword());
        } else {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }

        user.setCreated(oldUser.getCreated());
        user.setModified(LocalDateTime.now());
        User updated = userRepository.save(user);
        updated.setPassword("");
        return updated;
    }

    @Override
    public User deleteById(String id) {
        User oldUser = findById(id);
        userRepository.deleteById(id);
        oldUser.setPassword("");
        return oldUser;
    }

    @Override
    public String printProjects(String id) {
        StringBuilder stringBuilder = new StringBuilder();
        User user = findById(id);
        if (!(user instanceof ProductOwner)) {
            throw new InvalidEntityException(String.format("User with ID=%s is not product owner", id));
        }
        ProductOwner productOwner = (ProductOwner) user;
        productOwner.getProjectsIds().forEach(projectId ->
                stringBuilder.append(String.format("%s , ", projectService.findById(projectId).getTitle())));
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

    public String printAssignedTasks(String id) {
        StringBuilder stringBuilder = new StringBuilder();
        User user = findById(id);
        if (!(user instanceof Developer)) {
            throw new InvalidEntityException(String.format("User with ID=%s is not developer", id));
        }
        Developer developer = (Developer) user;
        developer.getAssignedTasksIds().forEach(taskId ->
                stringBuilder.append(String.format("%s , ", taskService.findById(taskId).getTitle())));
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

//    @Override
//    public List<User> findBySearch(String keywords) {
//        String[] words = keywords.split(" ");
//        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(words);
//        Query query = TextQuery.queryText(criteria);
//        return mongoTemplate.find(query, User.class);
//    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
