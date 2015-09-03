/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.lazulireflections.spotifystreamer.Utilities.Utility;

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

    /**
     * Make sure that when the up button is pressed that the top track list
     * is displayed if the dialog is open and the artist list is displayed
     * if the top track list is open.
     * @param item Input from system.
     * @return Returns the return value from the super class.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(Utility.getDialog() != null) {
                    Utility.getDialog().dismiss();
                    Utility.setDialog(null);
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
