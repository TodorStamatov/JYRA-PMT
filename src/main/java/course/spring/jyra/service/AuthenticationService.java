package course.spring.jyra.service;

import course.spring.jyra.model.User;

public interface AuthenticationService {
    User register(User user);

    User login(String username, String password);
}
