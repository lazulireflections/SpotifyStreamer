/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.lazulireflections.spotifystreamer.Utilities.Utility;

/**
  * Activity class for the main activity, displaying the artist search.
  */
public class MainActivity extends ActionBarActivity implements ArtistFragment.SpotifyCallback {
    private static String TOP_TRACK_FRAGMENT_TAG = "TTFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.top_track_container) != null) {
            Utility.setTabletLayout(true);
            if(savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.top_track_container, new TopTrackFragment(), TOP_TRACK_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            Utility.setTabletLayout(false);
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Implementing the callback interface from ArtistFragment, calling the TopTrackFragment
     * differently depending on if the phone or tablet layout is being used.
     * @param artistUri Uri for the artist's Spotify ID.
     * @param artistNameUri Uri for the artist's name.
     */
    @Override
    public void onItemSelected(Uri artistUri, Uri artistNameUri) {
        if(Utility.getTabletLayout()) {
            Bundle args = new Bundle();
            args.putParcelable(TopTrackFragment.TOP_TRACK_URI, artistUri);
            args.putParcelable(TopTrackFragment.TOP_TRACK_ARTIST_NAME_URI, artistNameUri);

            TopTrackFragment topTrack = new TopTrackFragment();
            topTrack.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.top_track_container, topTrack, TOP_TRACK_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent topTrackIntent = new Intent(this, TopTrackActivity.class);
            topTrackIntent.putExtra(TopTrackFragment.TOP_TRACK_URI, artistUri);
            topTrackIntent.putExtra(TopTrackFragment.TOP_TRACK_ARTIST_NAME_URI, artistNameUri);
            startActivity(topTrackIntent);
        }
    }
}
