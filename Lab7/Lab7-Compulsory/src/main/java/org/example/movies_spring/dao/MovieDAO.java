package org.example.movies_spring.dao;

import org.example.movies_spring.model.Movie;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieDAO {

    private final DataSource dataSource;

    public MovieDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> findAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT id, title, release_date FROM movies";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("release_date")
                ));
            }
        }
        return movies;
    }
}