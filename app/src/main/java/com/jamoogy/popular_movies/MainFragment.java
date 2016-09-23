package com.jamoogy.popular_movies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.jamoogy.popular_movies.data.MovieContract;

import java.util.ArrayList;

/**
 * Provides all functionality for the MainActivity.  Handles UI for sort options and grid view
 * of movies.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {//}, LoaderManager.LoaderCallbacks<Cursor> {

    // Custom Adapter object that holds definitions for how to place Movie objects in the grid.
    private MovieAdapter mMovieGridAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "grid_position";
    private static final String SAVED_MOVIE_ARRAY = "saved_movies";
    private GridView mGridView;
    private static final int POPULAR = 0;
    private static final int TOP_RATED = 1;
    private static final int FAVORITES = 2;
    private int mSpinnerPosition;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_POSTER_URL,
            MovieContract.FavoriteEntry.COLUMN_SYNOPSIS,
            MovieContract.FavoriteEntry.COLUMN_RATING,
            MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoriteEntry.COLUMN_BACKDROP_URL,
            MovieContract.FavoriteEntry.COLUMN_TMDB_MOVIE_ID,
            MovieContract.FavoriteEntry.COLUMN_TRAILER_URLS,
            MovieContract.FavoriteEntry.COLUMN_REVIEWS
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE  = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_SYNOPSIS = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_RELEASE_DATE = 5;
    static final int COL_MOVIE_BACKDROP = 6;
    static final int COL_MOVIE_TMDB_ID = 7;
    static final int COL_MOVIE_TRAILERS = 8;
    static final int COL_MOVIE_REVIEWS = 9;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set the root to the inflated view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Parcelable[] saved_movies = null;

        if (savedInstanceState != null
                && savedInstanceState.containsKey(SELECTED_KEY)
                && savedInstanceState.containsKey(SAVED_MOVIE_ARRAY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            saved_movies = savedInstanceState.getParcelableArray(SAVED_MOVIE_ARRAY);
        }

        // Create an instance of the custom MovieAdapter with the mainActivity context and empty list
        mMovieGridAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        // Set the adapter for the main movie grid
        mGridView.setAdapter(mMovieGridAdapter);
        if (saved_movies != null) {
            for (int i = 0; i < saved_movies.length; i++) {
                mMovieGridAdapter.add((Movie)saved_movies[i]);
            }
        }

        // Set up the spinner for selecting sort option
        initializeSpinner(rootView);

        // Define the intent to start the DetailActivity when a movie poster is clicked, passing
        // the Movie object represented by the poster as an extra.
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Movie movieAtPos = mMovieGridAdapter.getItem(pos);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.EXTRA_MOVIE), (Parcelable)movieAtPos);
                startActivity(detailIntent);
                mPosition = pos;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void updateMovies() {
        switch (mSpinnerPosition) {
            case POPULAR:
                // reload adapter with popular movies
                new FetchMoviesTask(getActivity(), mMovieGridAdapter)
                        .execute(getString(R.string.sort_popular));
                break;
            case TOP_RATED:
                // reload adapter with top rated movies
                new FetchMoviesTask(getActivity(), mMovieGridAdapter)
                        .execute(getString(R.string.sort_rating));
                break;
            case FAVORITES:
                // clear adapter, and query favorite database to add Movies back
                mMovieGridAdapter.clear();
                addFavorites();
                break;
        }
    }

    private void addFavorites() {
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            String title = cursor.getString(COL_MOVIE_TITLE);
            String poster_url = cursor.getString(COL_MOVIE_POSTER);
            String synopsis = cursor.getString(COL_MOVIE_SYNOPSIS);
            double rating = cursor.getDouble(COL_MOVIE_RATING);
            String releaseDate = cursor.getString(COL_MOVIE_RELEASE_DATE);
            String backdrop_url = cursor.getString(COL_MOVIE_BACKDROP);
            int tmdbId = cursor.getInt(COL_MOVIE_TMDB_ID);
            String trailerUrls = cursor.getString(COL_MOVIE_TRAILERS);
            String reviews = cursor.getString(COL_MOVIE_REVIEWS);
            Movie movieFromDatabase = new Movie(
                    title,
                    poster_url,
                    synopsis,
                    rating,
                    releaseDate,
                    backdrop_url,
                    tmdbId
            );
            movieFromDatabase.setTrailers(trailerUrls);
            movieFromDatabase.setReviews(reviews);
            mMovieGridAdapter.add( movieFromDatabase );
        }
        mMovieGridAdapter.notifyDataSetChanged();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (mSpinnerPosition != pos) {
            mSpinnerPosition = pos;
            updateMovies();
        }
        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    // Required for OnItemSelectedListener interface
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onSaveInstanceState(Bundle out) {
        mPosition = mGridView.getFirstVisiblePosition();
        if (mPosition != GridView.INVALID_POSITION) {
            out.putInt(SELECTED_KEY, mPosition);
            Movie[] movies = new Movie[mMovieGridAdapter.getCount()];
            for (int i = 0; i < movies.length; i++) {
                movies[i] = mMovieGridAdapter.getItem(i);
            }
            out.putParcelableArray(SAVED_MOVIE_ARRAY, movies);
        }
        super.onSaveInstanceState(out);
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