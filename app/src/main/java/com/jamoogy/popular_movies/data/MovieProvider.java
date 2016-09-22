package com.jamoogy.popular_movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by jmorgan on 9/22/2016.
 */
public class MovieProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int FAVORITES = 100;
    static final int FAVORITES_WITH_ID = 101;

//    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;
//
//    static {
//        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
//
//
//    }

    private static final String sMovieId =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry.COLUMN_TMDB_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITES,
                FAVORITES
        );
        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITES + "/#",
                FAVORITES_WITH_ID
        );
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }
            case FAVORITES_WITH_ID:
            {
                int movieId = MovieContract.FavoriteEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_TMDB_MOVIE_ID + " = ?",
                        new String[] {Integer.toString(movieId)},
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        if (match == FAVORITES) {
            long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
            if (_id > 0)
                returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null)
            selection = "1";
        if (match == FAVORITES) {
            rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if (match == FAVORITES) {
            rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
            if (rowsUpdated == 0)
                throw new android.database.SQLException("Failed to update row from " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 || selection == null)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (match == FAVORITES) {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        } else {
            return super.bulkInsert(uri, values);
        }
    }
}
