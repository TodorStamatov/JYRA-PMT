package course.spring.jyra.service.impl;

import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.Role;
import course.spring.jyra.model.User;
import course.spring.jyra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(""));
        return users;
    }

    @Override
    public User findById(long id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found", id)));
        found.setPassword("");
        return found;
    }

    @Override
    public User findByUsername(String username) {
        User found = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User '%s' not found.", username)));
        found.setPassword("");
        return found;
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
        if(user.getRoles() == null || user.getRoles().size()==0){
            user.setRoles(List.of(Role.DEVELOPER));
        }

        User created = userRepository.insert(user);
        created.setPassword("");
        return created;
    }

    @Override
    public User update(User user) {
        User oldUser = findById(user.getId());

        // prevent username changing
        if(user.getUsername() != null && !user.getUsername().equals(oldUser.getUsername())) {
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
        return user;
    }

    @Override
    public User deleteById(long id) {
        User oldUser = findById(id);
        userRepository.deleteById(id);
        oldUser.setPassword("");
        return oldUser;
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
