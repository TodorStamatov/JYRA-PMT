package course.spring.jyra.service.impl;

import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.Role;
import course.spring.jyra.model.User;
import course.spring.jyra.service.AuthenticationService;
import course.spring.jyra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User register(User user) {
        if (user.getRoles().contains(Role.ADMIN)) {
            throw new InvalidEntityException("Admins cannot be registered.");
        }
        return userService.create(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userService.findByUsername(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
