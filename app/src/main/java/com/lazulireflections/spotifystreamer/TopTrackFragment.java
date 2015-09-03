/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lazulireflections.spotifystreamer.Utilities.TopTracks;
import com.lazulireflections.spotifystreamer.Utilities.TrackAdapter;
import com.lazulireflections.spotifystreamer.Utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
  * A fragment class for the Top 10 tracks fragment
  */
public class TopTrackFragment extends Fragment {
    private View m_rootView;
    private ArrayList<TopTracks> m_topTracksList;
    private Uri m_uriArtistNr;
    private Uri m_uriArtistName;

    public static String TOP_TRACK_URI = "TopTrackUri";
    public static String TOP_TRACK_ARTIST_NAME_URI = "ArtistNameUri";

    /**
     * Constructs a new TopTrackFragment
     */
    public TopTrackFragment() {

    }

    /**
     * Creates the view for the TopTrack fragment.
     * @param inflater Input from the system.
     * @param container Input from the system.
     * @param savedInstanceState Input from the system.
     * @return View for the TopTrack fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null) {

            m_uriArtistNr = arguments.getParcelable(TOP_TRACK_URI);
            m_uriArtistName = arguments.getParcelable(TOP_TRACK_ARTIST_NAME_URI);
        }

        m_rootView = inflater.inflate(R.layout.fragment_top_track, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        m_topTracksList = new ArrayList<TopTracks>();
        if(m_uriArtistNr != null) {
            populateTrackList(m_uriArtistNr.toString(), m_uriArtistName.toString());
        }
        return m_rootView;
    }

    /**
      * Saves the track list instance so that it can be retrieved when the screen is rotated.
      *
      * Saving the activity instance as described on stackoverflow.com
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("track_fragment", m_topTracksList);
        if(m_uriArtistName != null || m_uriArtistNr != null) {
            savedInstanceState.putString("artistname", m_uriArtistName.toString());
            savedInstanceState.putString("artistnr", m_uriArtistNr.toString());
        }
    }

    /**
      * Creates the view, if the view is being re-created then the instance that was saved
      * gets loaded.
      *
      * Saving the activity instance as described on stackoverflow.com
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            m_topTracksList = savedInstanceState.getParcelableArrayList("track_fragment");
            if(m_uriArtistName != null || m_uriArtistNr != null) {
                m_uriArtistName = Uri.parse(savedInstanceState.getString("artistname"));
                m_uriArtistNr = Uri.parse(savedInstanceState.getString("artistnr"));
                populateTrackAdapter(m_uriArtistName.toString());
            }
        }
    }

    /**
      * Populates the list of tracks from Spotify according to the string gotten from the
      * ID of the chosen artist.
      * Each item of the list creates a track_item_layout, consisting of an ImageView and
      * two TextViews.
      * @param id The Spotify id of the selected artist
      * @param name The name of the selected artist
      */
    private void populateTrackList(final String id, final String name) {
        m_topTracksList.clear();
        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
        spotify.getArtistTopTrack(id, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                if(tracks.tracks.size() != 0) {
                    for (int i = 0; i < tracks.tracks.size(); i++) {
                        TopTracks topTracks = new TopTracks(Utility.findImageUrl(
                                tracks.tracks.get(i).album.images, 200),
                                tracks.tracks.get(i).album.name,
                                tracks.tracks.get(i).name,
                                tracks.tracks.get(i).preview_url);
                        m_topTracksList.add(topTracks);
                    }
                    populateTrackAdapter(name);
                } else {
                    Toast.makeText(m_rootView.getContext(),
                            "No tracks found for this artist, please try another artist instead!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /**
      * Fills out the adapter with each item on the tracks list and sets on click
      * functionality to fire up the player dialog.
      */
    public void populateTrackAdapter(final String name) {
        ListView listView = (ListView)m_rootView.findViewById(R.id.track_fragment);
        TrackAdapter m_trackAdapter = new TrackAdapter(new ArrayList<TopTracks>(),
                m_rootView, getResources().getDisplayMetrics().widthPixels);
        listView.setAdapter(m_trackAdapter);
        for(int i = 0; i < m_topTracksList.size(); i++) {
            m_trackAdapter.add(m_topTracksList.get(i));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PlayerDialog playerDialog = new PlayerDialog();
                Bundle arguments = new Bundle();
                arguments.putInt("location", position);
                arguments.putParcelableArrayList("tracklist", m_topTracksList);
                arguments.putString("artistname", name);
                playerDialog.setArguments(arguments);
                if(Utility.getTabletLayout()) {
                    playerDialog.show(getFragmentManager(), "player");
                } else {
                    Utility.setDialog(playerDialog);
                    getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, playerDialog).commit();
                }
            }
        });
    }
}
