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
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
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
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
  * Fragment class for the Artist fragment.
  * Topped with a search field above a list that gets filled with the
  * artists found after a search query.
  */
public class ArtistFragment extends Fragment {
    private String m_searchString;
    private View m_rootView;
    private ArrayList<Artist> m_artistList;

    /**
      * Constructs a new ArtistFragment.
      */
    public ArtistFragment() {
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
        return m_rootView;
    }

    /**
      * Saves the artist list instance so that it can be retrieved when the screen is rotated.
      * @param savedInstanceState Input from the system.
      */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("artist_list", m_artistList);
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
            m_artistList = savedInstanceState.getParcelableArrayList("artist_list");
            populateArtistAdapter();
        }
    }

    /**
      * This class holds the Artist entity information and makes it parcelable.
      * Artist information consists of the artists name and the URL to the artists
      * image, to be used as a thumbnail.
      */
    public class Artist implements Parcelable {
        private String m_thumbnailUrl;
        private String m_name;

        /**
          * Constructs a new artist.
          * @param thumbnailUrl The URL to the artists thumbnail image.
          * @param name The artists name.
          */
        public Artist(String thumbnailUrl, String name){
            m_thumbnailUrl = thumbnailUrl;
            m_name = name;
        }

        /**
          * Constructs a new artist.
          * @param parcel The artist info on parcelable form.
          */
        public Artist(Parcel parcel) {
            m_thumbnailUrl = parcel.readString();
            m_name = parcel.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /**
          * Creates an artist parcel.
          * @param dest Input from system.
          * @param flags Input from system.
          */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_thumbnailUrl);
            dest.writeString(m_name);
        }

        /**
          * Implemented because Parcelable requires it, not used.
          */
        public Creator<Artist> CREATOR = new Creator<Artist>() {
            @Override
            public Artist createFromParcel(Parcel source) {
                return new Artist(source);
            }

            @Override
            public Artist[] newArray(int size) {
                return new Artist[size];
            }
        };

        /**
          * Returns the thumbnail URL.
          * @return The URL to the artists thumbnail image.
          */
        public String getThumbnailUrl() {
            return m_thumbnailUrl;
        }

        /**
          * Returns the artists name.
          * @return The name of the artist.
          */
        public String getName() {
            return m_name;
        }

    }

    /**
      * Custom adapter class for artists that overrides the getView of the regular
      * ArrayAdapter and sets up the layout elements of the layout for the artists.
      */
    public class ArtistAdapter extends ArrayAdapter<Artist> {
        private int m_layoutWidthSegments;
        private int m_imageWidth;
        private int m_imageHeight;
        private int m_imageLeftPadding;
        private int m_imageTopPadding;
        private int m_imageRightPadding;
        private int m_imageBottomPadding;
        private ImageView.ScaleType m_scaleType;
        private int m_textWidth;
        private int m_textHeight;
        private int m_textLeftPadding;
        private int m_textTopPadding;
        private int m_textRightPadding;
        private int m_textBottomPadding;
        private int m_textSize;
        private int m_gravity;

        /**
          * Constructor for the array adapter where layout elements' parameters are
          * initialized.
          * @param artist The artist for which the ListItem element is being generated.
          */
        public ArtistAdapter(ArrayList<Artist> artist) {
            super(m_rootView.getContext(), 0, artist);
            m_layoutWidthSegments = getResources().getDisplayMetrics().widthPixels / 120;
            m_imageWidth = m_layoutWidthSegments * 20;
            m_imageHeight = m_layoutWidthSegments * 20;
            m_imageLeftPadding = m_layoutWidthSegments * 11;
            m_imageTopPadding = m_layoutWidthSegments * 6;
            m_imageRightPadding = m_layoutWidthSegments * 3;
            m_imageBottomPadding = m_layoutWidthSegments * 6;
            m_scaleType = ImageView.ScaleType.CENTER;
            m_textWidth = m_layoutWidthSegments * 79;
            m_textHeight = m_layoutWidthSegments * 20;
            m_textLeftPadding = m_layoutWidthSegments * 2;
            m_textTopPadding = m_layoutWidthSegments * 6;
            m_textRightPadding = m_layoutWidthSegments * 5;
            m_textBottomPadding = m_layoutWidthSegments * 6;
            m_textSize = 24;
            m_gravity = Gravity.CENTER_VERTICAL;
        }

        /**
         * Sets the layout parameters of the layout elements.
         * @param artistImage The ImageView for the thumbnail of the artist.
         * @param artistName The TextView for the name of the artist.
         */
        private void setLayout(ImageView artistImage, TextView artistName) {
            artistImage.setMaxWidth(m_imageWidth);
            artistImage.setMinimumWidth(m_imageWidth);
            artistImage.setMaxHeight(m_imageHeight);
            artistImage.setMinimumHeight(m_imageHeight);
            artistImage.setPadding(m_imageLeftPadding, m_imageTopPadding,
                    m_imageRightPadding, m_imageBottomPadding);
            artistImage.setScaleType(m_scaleType);
            artistName.setWidth(m_textWidth);
            artistName.setHeight(m_textHeight);
            artistName.setPadding(m_textLeftPadding, m_textTopPadding,
                    m_textRightPadding, m_textBottomPadding);
            artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, m_textSize);
            artistName.setGravity(m_gravity);
        }

        /**
         * Returns the view for an artist item using the artist_item_layout.
         * @param position Position in parent ListView
         * @param convertView The view that is turned into an artist item view.
         * @param parent The parent layout item.
         * @return The converted convertView.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Artist artist = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(m_rootView.getContext())
                        .inflate(R.layout.artist_item_layout, parent, false);
            }
            ImageView thumbnailView = (ImageView)convertView.findViewById(R.id.artist_image);
            TextView nameView = (TextView)convertView.findViewById(R.id.artist_name);
            setLayout(thumbnailView, nameView);
            Picasso.with(m_rootView.getContext()).load(artist.getThumbnailUrl())
                    .resize(m_imageWidth, m_imageHeight).centerCrop().into(thumbnailView);
            nameView.setText(artist.getName());
            return convertView;
        }
    }

    /**
      * Returns the URL of the artist image and puts in a placeholder
      * for those artists for whom no images are found.
      * @param image List of images for a given artist.
      * @return The url of the chosen image or placeholder if no image is found.
      */
    private String findArtistsImageUrl(List<Image> image) {
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
                if(artistsPager.artists.items.size() != 0) {
                    for (int i = 0; i < artistsPager.artists.items.size(); i++) {
                        Artist artist = new Artist(findArtistsImageUrl(
                                artistsPager.artists.items.get(i).images),
                                artistsPager.artists.items.get(i).name);
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
        ListView listView = (ListView)m_rootView.findViewById(R.id.artist_list);
        ArtistAdapter adapter = new ArtistAdapter(new ArrayList<Artist>());
        listView.setAdapter(adapter);
        for(int i = 0; i < m_artistList.size(); i++) {
            adapter.add(m_artistList.get(i));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(m_rootView.getContext(),
                        TopTrackActivity.class).putExtra("position", position)
                        .putExtra("name", m_searchString);
                startActivity(intent);

            }

        });
    }
}
