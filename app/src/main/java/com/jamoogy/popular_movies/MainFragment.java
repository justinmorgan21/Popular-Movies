package com.jamoogy.popular_movies;

import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Provides all functionality for the MainActivity.  Handles UI for sort options and grid view
 * of movies.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // Custom Adapter object that holds definitions for how to place Movie objects in the grid.
    private MovieAdapter mMovieGridAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "grid_position";
    private static final String SAVED_MOVIE_ARRAY = "saved_movies";
    private GridView mGridView;
    private int mSpinnerPosition = 0;

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
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), mMovieGridAdapter);
        String spinner_option_selected = null;
        switch (mSpinnerPosition) {
            case 0:
                spinner_option_selected = getString(R.string.sort_popular);
                break;
            case 1:
                spinner_option_selected = getString(R.string.sort_rating);
                break;
        }
        moviesTask.execute(spinner_option_selected);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (mSpinnerPosition != pos) {
            mSpinnerPosition = pos;
            updateMovies();
        }
        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
            //mGridView.setSelection(mPosition);
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