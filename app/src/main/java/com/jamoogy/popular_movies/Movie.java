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
    String poster_reference;
    String synopsis;
    double rating;
    String releaseDate;
    String backdrop;

    public Movie(String title, String poster_reference, String synopsis, double rating,
                 String release, String backdrop) {
        this.title = title;
        this.poster_reference = poster_reference;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = release;
        this.backdrop = backdrop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.poster_reference);
        dest.writeString(this.synopsis);
        dest.writeDouble(this.rating);
        dest.writeString(this.releaseDate);
        dest.writeString(this.backdrop);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.poster_reference = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.backdrop = in.readString();
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
