package course.spring.jyra.service;

import course.spring.jyra.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(String id);

    User findByUsername(String username);

    User create(User user);

    User deleteById(String id);

    User update(User user);

    String printProjects(String id);

    String printAssignedTasks(String id);

    long count();
}
