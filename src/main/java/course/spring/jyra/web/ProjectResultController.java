package course.spring.jyra.web;

import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.service.ProjectResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projectresults")
@Slf4j
public class ProjectResultController {
    private final ProjectResultService projectResultService;

    @Autowired
    public ProjectResultController(ProjectResultService projectResultService) {
        this.projectResultService = projectResultService;
    }

    @GetMapping
    public String getProjectResult(Model model) {
        model.addAttribute("project results", projectResultService.findAll());
        log.debug("GET: Project results: {}", projectResultService.findAll());
        return "projectresults";
    }

    @DeleteMapping
    public String deleteProjectResult(@RequestParam("delete") String id) {
        ProjectResult projectResult = projectResultService.findById(id);
        log.debug("DELETE: Project result: {}", projectResult);
        projectResultService.deleteById(id);
        return "redirect:/projectresults";
    }
}
