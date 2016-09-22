package com.jamoogy.popular_movies;

/**
 * Created by jmorgan on 9/18/2016.
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Custom async task that takes in the detail movie's id and fills the movie reviews.
 */
public class FetchReviewsTask extends AsyncTask<Movie, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final Context mContext;

    public FetchReviewsTask(Context context) { mContext = context; }
    /**
     * Takes in a single Movie object.
     * Sets up the URL necessary and queries The Movie DB for a JSON formatted string containing data
     * related to reviews of a movie.  The JSON string is parsed to create an array of reviews which
     * is converted to a string and used to set the param movies' reviews field.
     */
    @Override
    protected Void doInBackground(Movie... movies) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewDataJsonStr = null;
        String reviews = null;
        // Build themoviedb URL for movie param reviews using id
        URL url = null;

        Movie movie = movies[0];

        final String ID = mContext.getString(R.string.movie_api_key);

        try {
            // Construct the URL for The Movie DB query
            // Possible parameters are avaiable at The Movie DB's API page, at
            // https://www.themoviedb.org/documentation/api

            final String TMDB_MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String BASE_URL_WITH_MOVIE_ID_AND_REVIEW_PARAMETER = TMDB_MOVIE_BASE_URL + movie.tmdbId + "/reviews?";
            final String ID_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL_WITH_MOVIE_ID_AND_REVIEW_PARAMETER).buildUpon()
                    .appendQueryParameter(ID_PARAM, ID)
                    .build();

            url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built Uri: " + builtUri.toString());
            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            reviewDataJsonStr = buffer.toString();

            Log.v(LOG_TAG, reviewDataJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            reviews = getMovieReviewsFromJson(reviewDataJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        movie.setReviews(reviews);
        return null;
    }

    private String getMovieReviewsFromJson(String reviewDataJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        JSONObject reviewDataJson = new JSONObject(reviewDataJsonStr);
        JSONArray reviewsArray = reviewDataJson.getJSONArray(TMDB_RESULTS);

        String reviews = "";
        for(int i = 0; i < reviewsArray.length(); i++) {
            // Get the JSON object representing a single movie
            JSONObject reviewFromArray = reviewsArray.getJSONObject(i);

            // Extract individual data for the movie and include it in the array
            reviews += "author:" +
                            reviewFromArray.getString(TMDB_AUTHOR) +
                             " content:" + reviewFromArray.getString(TMDB_CONTENT);

            // add a separator that can be used to split the string later when extracted from database
            if (i < reviewsArray.length() - 1) {
                reviews += Utility.strSeparator;
            }
        }
        return reviews;
    }
}
