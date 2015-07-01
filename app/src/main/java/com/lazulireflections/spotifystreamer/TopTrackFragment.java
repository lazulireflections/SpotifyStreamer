/*
 * Copyright (C) 2015 Spotify streamer project implementation from
 * the Udacity Android Nanodegree course.
 */

package com.lazulireflections.spotifystreamer;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
  * A fragment class for the Top 10 tracks fragment
  */
public class TopTrackFragment extends Fragment {
    private View m_rootView;
    private ArrayList<Track> m_trackList;

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
        m_rootView = inflater.inflate(R.layout.fragment_top_track, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        m_trackList = new ArrayList<>();
        Intent intent = getActivity().getIntent();
        Bundle extras;
        if(intent != null && intent.hasExtra("position")) {
            extras = intent.getExtras();
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Top 10 tracks");
            ((ActionBarActivity)getActivity()).getSupportActionBar()
                    .setSubtitle(extras.getString("name"));
            populateTrackList(extras.getInt("position"), extras.getString("name"));
        }
        return m_rootView;
    }

    /**
      * Saves the track list instance so that it can be retrieved when the screen is rotated.
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("track_list", m_trackList);
    }

    /**
      * Creates the view, if the view is being re-created then the instance that was saved
      * gets loaded.
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            m_trackList = savedInstanceState.getParcelableArrayList("track_list");
            populateTrackAdapter();
        }
    }

    /**
      * This class holds the Track entity information and makes it parcelable.
      * Track information consists of the track name, album name, the URL to the track
      * image and the URL to the track preview.
      */
    public class Track implements Parcelable {
        private String m_thumbnailUrl;
        private String m_album;
        private String m_track;
        private String m_previewUrl;

        /**
         * Constructs a new track.
         * @param thumbnailUrl The URL to the track thumbnail
         * @param album The album name the track comes from.
         * @param track The track name.
         * @param previewUrl The URL to the track preview.
         */
        public Track(String thumbnailUrl, String album, String track, String previewUrl) {
            m_thumbnailUrl = thumbnailUrl;
            m_album = album;
            m_track = track;
            m_previewUrl = previewUrl;
        }

        /**
          * Construct a new track.
          * @param parcel The artist info in parcelable form.
          */
        public Track(Parcel parcel) {
            m_thumbnailUrl = parcel.readString();
            m_album = parcel.readString();
            m_thumbnailUrl = parcel.readString();
            m_previewUrl = parcel.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /**
          * Creates a track parcel.
          * @param dest Input from system.
          * @param flags Input from system.
          */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_thumbnailUrl);
            dest.writeString(m_album);
            dest.writeString(m_track);
            dest.writeString(m_previewUrl);
        }

        /**
         * Implemented because Parcelable requires it, not used.
         */
        public Creator<Track> CREATOR = new Creator<Track>() {
            @Override
            public Track createFromParcel(Parcel source) {
                return new Track(source);
            }

            @Override
            public Track[] newArray(int size) {
                return new Track[size];
            }
        };

        /**
          * Returns the tracks thumbnail URL.
          * @return The URL to the tracks thumbnail image.
          */
        public String getThumbnailURL() {
            return m_thumbnailUrl;
        }

        /**
          * Returns the album name.
          * @return Name of the tracks album.
          */
        public String getAlbum() {
            return m_album;
        }

        /**
          * Returns the track name.
          * @return Name of the track.
          */
        public String getTrack() {
            return m_track;
        }
    }

    /**
      * Custom adapter class for tracks that overrides the getView of the regular
      * ArrayAdapter and sets up the layout elements for the tracks.
      */
    public class TrackAdapter extends ArrayAdapter<Track> {
        private int m_layoutWidthSegment;
        private int m_imageWidth;
        private int m_imageHeight;
        private int m_imageLeftPadding;
        private int m_imageTopPadding;
        private int m_imageRightPadding;
        private int m_imageBottomPadding;
        private ImageView.ScaleType m_imageScaleType;
        private int m_albumWidth;
        private int m_albumHeight;
        private int m_albumLeftPadding;
        private int m_albumTopPadding;
        private int m_albumRightPadding;
        private int m_albumBottomPadding;
        private int m_albumTextSize;
        private int m_albumGravity;
        private int m_trackWidth;
        private int m_trackHeight;
        private int m_trackLeftPadding;
        private int m_trackTopPadding;
        private int m_trackRightPadding;
        private int m_trackBottomPadding;
        private int m_trackTextSize;
        private int m_trackGravity;

        /**
          * Constructor for the array adapter where the layout elements' parameters are
          * initialized.
          * @param track The track for which the ListItem element is being generated.
          */
        public TrackAdapter(ArrayList<Track> track) {
            super(m_rootView.getContext(), 0, track);
            m_layoutWidthSegment = getResources().getDisplayMetrics().widthPixels / 120;
            m_imageWidth = m_layoutWidthSegment * 20;
            m_imageHeight = m_layoutWidthSegment * 20;
            m_imageLeftPadding = m_layoutWidthSegment * 11;
            m_imageTopPadding = m_layoutWidthSegment * 6;
            m_imageRightPadding = m_layoutWidthSegment * 3;
            m_imageBottomPadding = m_layoutWidthSegment * 6;
            m_imageScaleType = ImageView.ScaleType.CENTER;
            m_albumWidth = m_layoutWidthSegment * 79;
            m_albumHeight = m_layoutWidthSegment * 15;
            m_albumLeftPadding = m_layoutWidthSegment * 2;
            m_albumTopPadding = m_layoutWidthSegment * 6;
            m_albumRightPadding = m_layoutWidthSegment * 5;
            m_albumBottomPadding = 0;
            m_albumTextSize = 18;
            m_albumGravity = Gravity.BOTTOM;
            m_trackWidth = m_layoutWidthSegment * 79;
            m_trackHeight = m_layoutWidthSegment * 15;
            m_trackLeftPadding = m_layoutWidthSegment * 2;
            m_trackTopPadding = 0;
            m_trackRightPadding = m_layoutWidthSegment * 5;
            m_trackBottomPadding = m_layoutWidthSegment * 6;
            m_trackTextSize = 15;
            m_trackGravity = Gravity.TOP;
        }

        /**
         * Sets the layout parameters of the layout elements.
         * @param albumImage The ImageView for the album image.
         * @param albumName The TextView for the album name.
         * @param trackName The TextView for the trackName.
         */
        public void setLayout(ImageView albumImage, TextView albumName, TextView trackName) {
            albumImage.setMaxWidth(m_imageWidth);
            albumImage.setMinimumWidth(m_imageWidth);
            albumImage.setMaxHeight(m_imageHeight);
            albumImage.setMinimumHeight(m_imageHeight);
            albumImage.setPadding(m_imageLeftPadding, m_imageTopPadding,
                    m_imageRightPadding, m_imageBottomPadding);
            albumImage.setScaleType(m_imageScaleType);
            albumName.setWidth(m_albumWidth);
            albumName.setHeight(m_albumHeight);
            albumName.setPadding(m_albumLeftPadding, m_albumTopPadding,
                    m_albumRightPadding, m_albumBottomPadding);
            albumName.setTextSize(m_albumTextSize);
            albumName.setGravity(m_albumGravity);
            trackName.setWidth(m_trackWidth);
            trackName.setHeight(m_trackHeight);
            trackName.setPadding(m_trackLeftPadding, m_trackTopPadding,
                    m_trackRightPadding, m_trackBottomPadding);
            trackName.setTextSize(m_trackTextSize);
            trackName.setGravity(m_trackGravity);
        }

        /**
         * Returns the view for a track item using the track_item_layout.
         * @param position Position in parent ListView.
         * @param convertView The view that is turned into a track item view.
         * @param parent The parent layout item.
         * @return The converted convertView.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Track track = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(m_rootView.getContext())
                        .inflate(R.layout.track_item_layout, parent, false);
            }
            ImageView thumbnailView = (ImageView)convertView.findViewById(R.id.track_image);
            TextView albumName = (TextView)convertView.findViewById(R.id.album);
            TextView trackName = (TextView)convertView.findViewById(R.id.track);
            setLayout(thumbnailView, albumName, trackName);
            Picasso.with(m_rootView.getContext()).load(track.getThumbnailURL())
                    .resize(m_imageWidth, m_imageHeight).centerCrop().into(thumbnailView);
            albumName.setText(track.getAlbum());
            trackName.setText(track.getTrack());
            return convertView;
        }
    }

    /**
      * Returns the URL of the album image and puts in a placeholder
      * for the tracks where no album image was found.
      * @param image List of album images for the track.
      * @return The URL of the chosen image or placeholder if no image is found.
      */
    private String findTracksImageUrl(List<Image> image) {
        String url = "";
        int currentSize = -1;
        for(int i = 0; i < image.size(); i++) {
            if((image.get(i).width < currentSize && image.get(i).width >= 200) || currentSize == -1) {
                url = image.get(i).url;
                currentSize = image.get(i).width;
            }
        }
        if(url.equals("")) {
            url = "https://lh3.googleusercontent.com/-edYeK6MZ4wk/VY1yOp5vu5I/AAAAAAAAAXE/WNNAlS9rRBA/s65/placeholder.png";
        }
        return url;
    }

    /**
      * Populates the list of tracks from Spotify according to the string gotten from the
      * ID of the chosen artist.
      * Each item of the list creates a track_item_layout, consisting of an ImageView and
      * two TextViews.
      * @param position The position of the chosen artist from the artist fragment.
      * @param name Name of the artist chosen in the artist fragment.
      */
    private void populateTrackList(final int position, String name) {
        m_trackList.clear();
        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        spotify.searchArtists(name, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                String artistId = artistsPager.artists.items.get(position).id;
                HashMap<String, Object> options = new HashMap<>();
                options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
                spotify.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, Response response) {
                        if(tracks.tracks.size() != 0) {
                            for (int i = 0; i < tracks.tracks.size(); i++) {
                                Track track = new Track(findTracksImageUrl(tracks.tracks.
                                        get(i).album.images), tracks.tracks.get(i).album.name,
                                        tracks.tracks.get(i).name,
                                        tracks.tracks.get(i).preview_url);
                                m_trackList.add(track);
                            }
                            populateTrackAdapter();
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
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /**
      * Fills out the adapter with each item on the tracks list.
      */
    public void populateTrackAdapter() {
        ListView listView = (ListView)m_rootView
                .findViewById(R.id.track_list);
        TrackAdapter adapter = new TrackAdapter(new ArrayList<Track>());
        listView.setAdapter(adapter);
        for(int i = 0; i < m_trackList.size(); i++) {
            adapter.add(m_trackList.get(i));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO: Play track when selected
            }

        });
    }
}
