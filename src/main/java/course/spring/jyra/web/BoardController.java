package course.spring.jyra.web;

import course.spring.jyra.model.Board;
import course.spring.jyra.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/boards")
@Slf4j
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public String getBoards(Model model) {
        model.addAttribute("boards", boardService.findAll());
        log.debug("GET: Boards: {}", boardService.findAll());
        return "all-boards";
    }

    @PostMapping
    public String addBoard(@ModelAttribute("board") Board board) {
        boardService.create(board);
        log.debug("POST: Board: {}", board);
        return "redirect:/boards";
    }

    @DeleteMapping
    public String deleteBoard(@RequestParam("delete") String id) {
        Board board = boardService.findById(id);
        log.debug("DELETE: Board: {}", board);
        boardService.deleteById(id);
        return "redirect:/boards";
    }

    @GetMapping("/{boardId}")
    public String getBoardById(Model model, @PathVariable("boardId") String id) {
        model.addAttribute("board", boardService.findById(id));
        log.debug("GET: Board with Id=%s : {}", id, boardService.findById(id));
        return "single-board";
    }

    @PutMapping
    public String updateBoard(@RequestParam("update") String id) {
        Board board = boardService.findById(id);
        log.debug("UPDATE: Board: {}", board);
        boardService.update(board);
        return "redirect:/boards";
    }
}
