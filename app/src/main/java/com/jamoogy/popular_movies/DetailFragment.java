package com.jamoogy.popular_movies;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
            Picasso.with(getContext()).load(mDetailMovie.poster_url)
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
//            Picasso.with(getContext()).load(mDetailMovie.backdrop_url)
//                    .into(detailBackdrop);

            Button detailTrailerButton1 = (Button) rootView.findViewById(R.id.detail_trailer_button_1);
            detailTrailerButton1.setOnClickListener(trailerBtnListener);
            Button detailTrailerButton2 = (Button) rootView.findViewById(R.id.detail_trailer_button_2);
            detailTrailerButton2.setOnClickListener(trailerBtnListener);

            String[] reviewsArray = convertStringToArray(mDetailMovie.reviews);
            if (reviewsArray.length > 0) {
                TextView reviewHeaderTextView = (TextView) rootView.findViewById(R.id.detail_review_header);
                reviewHeaderTextView.setText("Reviews");

                String review1 = reviewsArray[0];
                int contentStartIndex = review1.indexOf("content:");
                String review1Author = review1.substring(7, contentStartIndex);
                String review1Content = review1.substring(contentStartIndex + 8);

                TextView review1AuthorTextView = (TextView) rootView.findViewById(R.id.detail_review_1_author);
                review1AuthorTextView.setText(review1Author);
                TextView review1ContentTextView = (TextView) rootView.findViewById(R.id.detail_review_1_content);
                review1ContentTextView.setText(review1Content);

                if (reviewsArray.length > 1) {
                    String review2 = reviewsArray[1];
                    contentStartIndex = review2.indexOf("content:");
                    String review2Author = review2.substring(7, contentStartIndex);
                    String review2Content = review2.substring(contentStartIndex + 8);

                    TextView review2AuthorTextView = (TextView) rootView.findViewById(R.id.detail_review_2_author);
                    review2AuthorTextView.setText(review2Author);
                    TextView review2ContentTextView = (TextView) rootView.findViewById(R.id.detail_review_2_content);
                    review2ContentTextView.setText(review2Content);
                }
            }

            CheckBox favoriteButton = (CheckBox) rootView.findViewById(R.id.detail_favorite_button);
            favoriteButton.setOnClickListener(favBtnListener);

        }
        return rootView;
    }

    private View.OnClickListener favBtnListener = new View.OnClickListener() {
        public void onClick(View button) {
            CheckBox favButton = (CheckBox) button;
            favButton.setChecked(true);
            // insert movie into favorites database
            
        }
    };

    private View.OnClickListener trailerBtnListener = new View.OnClickListener() {
        public void onClick(View button) {
            String url = null;
            String trailersArray[] = convertStringToArray(mDetailMovie.trailerUrls);
            switch(button.getId()) {
                case(R.id.detail_trailer_button_1):
                    url = trailersArray[0];
                    break;
                case(R.id.detail_trailer_button_2):
                    if (trailersArray.length > 1) {
                        url = trailersArray[1];
                    } else {
                        Toast.makeText(
                                getContext(),
                                "No Trailer",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
            }
            if (url != null) {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
                trailerIntent.setData(Uri.parse(url));
                startActivity(trailerIntent);
            }
        }
    };

    private String[] convertStringToArray(String str) {
        if (str.length() == 0) { return new String[0]; }
        String[] result = str.split(Utility.strSeparator);
        return result;
    }
}
