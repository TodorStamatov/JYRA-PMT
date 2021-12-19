package course.spring.jyra.service;

import course.spring.jyra.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(long id);

    User findByUsername(String username);

    User create(User user);

    User deleteById(long id);

    User update(User user);

    long count();
}
