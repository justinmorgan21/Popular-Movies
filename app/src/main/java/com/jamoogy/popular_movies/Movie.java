package com.jamoogy.popular_movies;

import java.io.Serializable;

/**
 * Created by jmorgan on 8/7/2016.
 */
public class Movie implements Serializable {
    String title;
    String poster_reference;
    String synopsis;
    double rating;
    String releaseDate;
    String backdrop;

    public Movie(String title, String poster_reference, String synopsis, double rating, String release, String backdrop) {
        this.title = title;
        this.poster_reference = poster_reference;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = release;
        this.backdrop = backdrop;
    }
}
