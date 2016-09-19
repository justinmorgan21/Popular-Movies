package com.jamoogy.popular_movies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Provides all functionality for the DetailActivity.  Handles the UI for showing the detail info
 * for a movie clicked on in the main movie grid.
 */
public class DetailFragment extends Fragment {

    private Movie mDetailMovie;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the intent that was passed from the initiating activity
        Intent detailIntent = getActivity().getIntent();
        //if (detailIntent != null && detailIntent.hasExtra(getString(R.string.EXTRA_MOVIE))) {
        String extraTag = getString(R.string.EXTRA_MOVIE);
        if (detailIntent.hasExtra(extraTag)) {
            // Get the clicked Movie object that was added as an extra to the intent
            mDetailMovie = (Movie)detailIntent.getParcelableExtra(getString(R.string.EXTRA_MOVIE));
            Resources resources = getResources();
            //FetchTrailersTask trailersTask = new FetchTrailersTask(getContext());
            //trailersTask.execute(mDetailMovie);
            // Set the poster image
            ImageView detailPoster = (ImageView) rootView.findViewById(R.id.detail_poster);
            Picasso.with(getContext()).load(mDetailMovie.poster_reference)
                    .into(detailPoster);

            // Set the title
            TextView detailTitle = (TextView) rootView.findViewById(R.id.detail_title);
            detailTitle.setText(mDetailMovie.title);

            // Set the release date
            TextView detailRelease = (TextView) rootView.findViewById(R.id.detail_release);
            String releaseDateDisplay = resources.getString(R.string.release_date_display, mDetailMovie.releaseDate);
            detailRelease.setText(releaseDateDisplay);

            // Set the user rating
            TextView detailRating = (TextView) rootView.findViewById(R.id.detail_rating);
            String ratingDisplay = resources.getString(R.string.rating_display, Double.toString(mDetailMovie.rating));
            detailRating.setText(ratingDisplay);

            // Set the overview
            TextView detailOverview = (TextView) rootView.findViewById(R.id.detail_synopsis);
            detailOverview.setText(mDetailMovie.synopsis);

            // Set the backdrop
//            ImageView detailBackdrop = (ImageView) rootView.findViewById(R.id.detail_backdrop);
//            Picasso.with(getContext()).load(detailMovie.backdrop)
//                    .into(detailBackdrop);

            Button detailTrailerButton = (Button) rootView.findViewById(R.id.trailer_button);
            

            String s = "" + mDetailMovie.trailer_references[0];


        }
        return rootView;
    }
}
