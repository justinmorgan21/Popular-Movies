package com.jamoogy.popular_movies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jmorgan on 8/7/2016.
 * Object class to hold member data for a movie
 */
public class Movie implements Serializable, Parcelable {
    String title;
    String poster_url;
    String synopsis;
    double rating;
    String releaseDate;
    String backdrop_url;
    int tmdbId;
    String trailerUrls;  // converted from array to string for database
    String reviews; // converted from array to string for database

    public Movie(String title, String poster_reference, String synopsis, double rating,
                 String release, String backdrop, int id) {//}, String trailer_reference) {
        this.title = title;
        this.poster_url = poster_reference;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = release;
        this.backdrop_url = backdrop;
        this.tmdbId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.poster_url);
        dest.writeString(this.synopsis);
        dest.writeDouble(this.rating);
        dest.writeString(this.releaseDate);
        dest.writeString(this.backdrop_url);
        dest.writeInt(this.tmdbId);
        dest.writeString(this.trailerUrls);
        dest.writeString(this.reviews);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.poster_url = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.backdrop_url = in.readString();
        this.tmdbId = in.readInt();
        this.trailerUrls = in.readString();
        this.reviews = in.readString();
    }

    public void setTrailers(String urls) {
        this.trailerUrls = urls;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
