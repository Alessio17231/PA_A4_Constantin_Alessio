package org.example.movies_spring.controller;

import org.example.movies_spring.dao.MovieDAO;
import org.example.movies_spring.model.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieDAO movieDAO;

    public MovieController(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @GetMapping
    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.findAll();
    }
}