package org.example.movies_spring.controller;

import org.example.movies_spring.dao.GenreDAO;
import org.example.movies_spring.model.Genre;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreDAO genreDAO;

    public GenreController(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }

    @GetMapping
    public List<Genre> getAll() throws SQLException {
        return genreDAO.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Genre> getById(@PathVariable int id) throws SQLException {
        return genreDAO.findById(id);
    }

    @PostMapping
    public String create(@RequestParam String name) throws SQLException {
        genreDAO.create(name);
        return "Gen adaugat: " + name;
    }
}