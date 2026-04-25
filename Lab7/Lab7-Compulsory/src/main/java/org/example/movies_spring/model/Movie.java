package org.example.movies_spring.model;

public class Movie {
    private int id;
    private String title;
    private String releaseDate;

    public Movie(int id, String title, String releaseDate) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{id=" + id + ", title='" + title + "', releaseDate='" + releaseDate + "'}";
    }
}