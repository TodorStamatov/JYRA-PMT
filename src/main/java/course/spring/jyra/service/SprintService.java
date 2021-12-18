package course.spring.jyra.service;


import course.spring.jyra.model.Sprint;

import java.util.List;

public interface SprintService {
    List<Sprint> findAll();
    Sprint findById(long id);
    Sprint create(Sprint sprint);
    Sprint deleteById(long id);
    Sprint update(Sprint sprint);
    long count();
}
