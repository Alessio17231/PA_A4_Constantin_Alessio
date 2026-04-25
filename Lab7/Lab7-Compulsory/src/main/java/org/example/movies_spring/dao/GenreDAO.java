package org.example.movies_spring.dao;

import org.example.movies_spring.model.Genre;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDAO {

    private final DataSource dataSource;

    public GenreDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Genre> findAll() throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT id, name FROM genres";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        }
        return genres;
    }

    public Optional<Genre> findById(int id) throws SQLException {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        }
        return Optional.empty();
    }

    public void create(String name) throws SQLException {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }
}