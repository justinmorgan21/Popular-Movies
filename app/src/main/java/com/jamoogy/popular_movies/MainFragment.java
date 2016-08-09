package com.jamoogy.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Provides all functionality for the MainActivity.  Handles UI for sort options and grid view
 * of movies.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // Custom Adapter object that holds definitions for how to place Movie objects in the grid.
    private MovieAdapter mMovieGridAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set the root to the inflated view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create an instance of the custom MovieAdapter with the mainActivity context and empty list
        mMovieGridAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView movieGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        // Set the adapter for the main movie grid
        movieGridView.setAdapter(mMovieGridAdapter);

        // Set up the spinner for selecting sort option
        initializeSpinner(rootView);

        // Define the intent to start the DetailActivity when a movie poster is clicked, passing
        // the Movie object represented by the poster as an extra.
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Movie movieAtPos = mMovieGridAdapter.getItem(pos);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.EXTRA_MOVIE), movieAtPos);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            new FetchMoviesTask().execute(getString(R.string.sort_popular));
        } else if (pos == 1) {
            new FetchMoviesTask().execute(getString(R.string.sort_rating));
        }
    }

    // Required for OnItemSelectedListener interface
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Custom async task that takes in the sort option for the grid and fills the grid adapter.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Takes in a single element String array representing the user-selected sort option for the grid.
         * Sets up the URL necessary and queries The Movie DB for a JSON formatted string containing data
         * for a selection of movies.  The JSON string is parsed to create an array of Movie objects which
         * is returned.
         */
        @Override
        protected Movie[] doInBackground(String... sortOption) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDataJsonStr = null;

            final String SORT_BY = sortOption[0];
            final String ID = getString(R.string.movie_api_key);

            try {
                // Construct the URL for The Movie DB query
                // Possible parameters are avaiable at The Movie DB's API page, at
                // https://www.themoviedb.org/documentation/api

                final String TMDB_MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String BASE_URL_WITH_SORT = TMDB_MOVIE_BASE_URL + SORT_BY + "?";
                final String ID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL_WITH_SORT).buildUpon()
                        .appendQueryParameter(ID_PARAM, ID)
                        .build();

                URL url = new URL(builtUri.toString());
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
                movieDataJsonStr = buffer.toString();

                Log.v(LOG_TAG, movieDataJsonStr);

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
                return getMovieDataFromJson(movieDataJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Clear the adapter and fill it with the Movie objects contained in the array parameter
         * @param movies
         */
        @Override
        protected void onPostExecute(Movie[] movies) {
            mMovieGridAdapter.clear();
            if(movies != null) {
                for (Movie movie : movies) {
                    mMovieGridAdapter.add(movie);
                }
            }
        }

        /**
         * Take the String representing a long sequence of movie data in JSON Format and
         * pull out the data we need to construct the Movie objects for the grid adapter.
         */
        private Movie[] getMovieDataFromJson(String movieDataJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_TITLE = "original_title";
            final String TMDB_THUMBNAIL = "backdrop_path";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_RELEASE = "release_date";
            final String TMDB_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

            JSONObject movieDataJson = new JSONObject(movieDataJsonStr);
            JSONArray moviesArray = movieDataJson.getJSONArray(TMDB_RESULTS);

            Movie[] resultMovies = new Movie[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing a single movie
                JSONObject movieFromArray = moviesArray.getJSONObject(i);

                // Extract individual data for the movie and include it in the array
                String title = movieFromArray.getString(TMDB_TITLE);
                String poster_path = TMDB_BASE_IMAGE_URL + movieFromArray.getString(TMDB_POSTER);
                String synopsis = movieFromArray.getString(TMDB_SYNOPSIS);
                double rating = movieFromArray.getDouble(TMDB_RATING);
                String releaseDate = formatDate(movieFromArray.getString(TMDB_RELEASE));
                String thumbnail = TMDB_BASE_IMAGE_URL + movieFromArray.getString(TMDB_THUMBNAIL);

                resultMovies[i] = new Movie(title, poster_path, synopsis, rating, releaseDate, thumbnail);
            }
            return resultMovies;
        }
    }

    /**
     * Take in the date formatted by TheMovieDB as yyyy-MM-dd and reformat as MMM dd, yyyy
     * to be used by the textview for movie release date.
     * @param TMDB_date date format example: 2013-03-24
     * @return date format example: Mar 24, 2013
     */
    private String formatDate(String TMDB_date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(TMDB_date);
            return date.toString().substring(4, 10) + ", " + date.toString().substring(23, 28);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Set up the spinner with two options for movie sorting, popular and top_rated
     * @param rootView
     */
    private void initializeSpinner(View rootView) {
        Spinner sortSpinner = (Spinner) rootView.findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
    }
}
