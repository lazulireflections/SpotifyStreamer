/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

/**
  * Activity class for the top 10 track activity, displaying the top 10 tracks for
  * a selected artist.
  */
public class TopTrackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_track);
        if(savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(TopTrackFragment.TOP_TRACK_URI,
                    getIntent().getParcelableExtra(TopTrackFragment.TOP_TRACK_URI));
            arguments.putParcelable(TopTrackFragment.TOP_TRACK_ARTIST_NAME_URI,
                    getIntent().getParcelableExtra(TopTrackFragment.TOP_TRACK_ARTIST_NAME_URI));

            TopTrackFragment topTrackFragment = new TopTrackFragment();
            topTrackFragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.top_track_container, topTrackFragment).commit();
        }
    }
}
