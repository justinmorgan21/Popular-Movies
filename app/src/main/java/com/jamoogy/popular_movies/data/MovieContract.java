package com.jamoogy.popular_movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jmorgan on 9/22/2016.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.jamoogy.popular_movies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.jamoogy.popular_movies/favorites/ is a valid path for
    // looking at movie data.
    public static final String PATH_FAVORITES = "favorites";

    /* Inner class that defines the table contents of the weather table */
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        // no foreign keys used in this database scheme
        // Column with the foreign key into the location table.
        //public static final String COLUMN_ _KEY = "???_id";

        // Title of the movie
        public static final String COLUMN_TITLE = "title";

        // URL path for TMDB main movie image
        public static final String COLUMN_POSTER_URL = "poster_url";

        // Description of the movie
        public static final String COLUMN_SYNOPSIS = "synopsis";

        // User rating out of 10 given as a double
        public static final String COLUMN_RATING = "rating";

        // Date of release converted to "yyyy-MM-dd" format
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // URL path for the TMDB backdrop movie image
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";

        // Unique movie ID as returned by the TMDB API
        public static final String COLUMN_TMDB_MOVIE_ID = "tmdb_movie_id";

        // Custom formatted string converted from array to hold all Youtube
        // trailer urls using separator in Utility
        public static final String COLUMN_TRAILER_URLS = "trailer_urls";

        // Custom formatted string converted from array to hold all reviews
        // using separator in Utility
        public static final String COLUMN_REVIEWS = "reviews";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteWithMovieId(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }

        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

//        /*
//            Student: Fill in this buildWeatherLocation function
//         */
//        public static Uri buildFavoriteWithMovieId(String locationSetting) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//        }
//
//        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }
//
//        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendPath(Long.toString(normalizeDate(date))).build();
//        }
//
//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static long getDateFromUri(Uri uri) {
//            return Long.parseLong(uri.getPathSegments().get(2));
//        }
//
//        public static long getStartDateFromUri(Uri uri) {
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0)
//                return Long.parseLong(dateString);
//            else
//                return 0;
//        }
    }
}
