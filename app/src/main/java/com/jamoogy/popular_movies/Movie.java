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
    int id;
    String[] trailer_references;

    public Movie(String title, String poster_reference, String synopsis, double rating,
                 String release, String backdrop, int id) {//}, String trailer_reference) {
        this.title = title;
        this.poster_reference = poster_reference;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = release;
        this.backdrop = backdrop;
        this.id = id;
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
        dest.writeInt(this.id);
        dest.writeStringArray(this.trailer_references);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.poster_reference = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.backdrop = in.readString();
        this.id = in.readInt();
        //in.readStringArray(this.trailer_references);
        this.trailer_references = in.createStringArray();
    }

    public void setTrailers(String[] urls) {
        trailer_references = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            trailer_references[i] = urls[i];
        }
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
