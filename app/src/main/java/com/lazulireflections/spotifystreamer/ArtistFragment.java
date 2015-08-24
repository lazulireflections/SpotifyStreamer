/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lazulireflections.spotifystreamer.Utilities.Artist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.lazulireflections.spotifystreamer.Utilities.ArtistAdapter;
import com.lazulireflections.spotifystreamer.Utilities.Utility;

/**
  * Fragment class for the Artist fragment.
  * Topped with a search field above a list that gets filled with the
  * artists found after a search query.
  */
public class ArtistFragment extends Fragment {
    private String m_searchString;
    private View m_rootView;
    private ArrayList<Artist> m_artistList;

    private SpotifyCallback mCallback;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    /**
     * Callback interface for the selected artist.
     */
    public interface SpotifyCallback {
        void onItemSelected(Uri artistUri, Uri artistName);
    }

    /**
      * Constructs a new ArtistFragment.
      */
    public ArtistFragment() {
    }

    /**
     * Setting the callback to the activity.
     * @param activity The the associated activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (SpotifyCallback)activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement SpotifyCallback");
        }
    }

    /**
     * Creates the view for the Artist fragment after adding and setting up the SearchView.
     * @param inflater Input from the system.
     * @param container Input from the system.
     * @param savedInstanceState Input from the system.
     * @return View for the Artist fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        m_rootView = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        m_artistList = new ArrayList<>();

        SearchView m_search = (SearchView) m_rootView.findViewById(R.id.search);

        m_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                m_searchString = s;
                populateArtistList();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        if(mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return m_rootView;
    }

    /**
      * Saves the artist list instance so that it can be retrieved when the screen is rotated.
      *
      * Saving the activity instance as described on stackoverflow.com
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mPosition != ListView.INVALID_POSITION) {
            savedInstanceState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("artist_list", m_artistList);
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
            m_artistList = savedInstanceState.getParcelableArrayList("artist_list");
            populateArtistAdapter();
        }
    }

    /**
      * Populates the list of artists from Spotify according to the string gotten from the
      * SearchView.
      * Each item of the list creates a artist_item_layout, consisting of an ImageView and a
      * TextView.
      */
    private void populateArtistList() {
        m_artistList.clear();
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        spotify.searchArtists(m_searchString, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (artistsPager.artists.items.size() != 0) {
                    for (int i = 0; i < artistsPager.artists.items.size(); i++) {
                        Artist artist = new Artist(Utility.findImageUrl(
                                artistsPager.artists.items.get(i).images, 200),
                                artistsPager.artists.items.get(i).name,
                                artistsPager.artists.items.get(i).id);
                        m_artistList.add(artist);
                    }
                    populateArtistAdapter();
                } else {
                    Toast.makeText(m_rootView.getContext(),
                            "No artist found, try to refine your search!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /**
      * Fills out the adapter with each item on the artist list
      */
    public void populateArtistAdapter() {
        mListView = (ListView)m_rootView.findViewById(R.id.artist_list);
        int segmentSize;
        if(Utility.getTabletLayout()) {
            segmentSize = (getResources().getDisplayMetrics().widthPixels * 2) / 5;
        } else {
            segmentSize = getResources().getDisplayMetrics().widthPixels;
        }
        ArtistAdapter adapter = new ArtistAdapter(new ArrayList<Artist>(),
                m_rootView, segmentSize);
        mListView.setAdapter(adapter);
        for(int i = 0; i < m_artistList.size(); i++) {
            adapter.add(m_artistList.get(i));
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mCallback.onItemSelected(Uri.parse(m_artistList.get(position).getId()),
                        Uri.parse(m_artistList.get(position).getName()));
                mPosition = position;
            }

        });
    }
}
