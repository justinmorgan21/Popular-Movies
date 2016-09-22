package com.jamoogy.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jamoogy.popular_movies.data.MovieContract.FavoriteEntry;

/**
 * Created by jmorgan on 9/22/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold favorite movies.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_RATING + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_TMDB_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_TRAILER_URLS + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_REVIEWS + " TEXT NOT NULL);";

                // Not using a foreign key for this scheme
//                // Set up the location column as a foreign key to location table.
//                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
//                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +
//
//                // To assure the application have just one weather entry per day
//                // per location, it's created a UNIQUE constraint with REPLACE strategy
//                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
//                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
