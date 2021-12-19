package course.spring.jyra.service.impl;

import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.User;
import course.spring.jyra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(()->new EntityNotFoundException(String.format("User with ID=%s not found.", id)));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s not found.", username)));
    }

    @Override
    public User create(User user) {
        user.setId(null);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        return userRepository.insert(user);
    }

    @Override
    public User deleteById(long id) {
        User oldUser=findById(id);
        userRepository.deleteById(id);
        return oldUser;
    }

    @Override
    //Ne e vqrno
    public User update(User user) {
        User oldUser=findById(user.getId());
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
