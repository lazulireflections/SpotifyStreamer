package com.lazulireflections.spotifystreamer.Utilities;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazulireflections.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom adapter class for tracks that overrides the getView of the regular
 * ArrayAdapter and sets up the layout elements for the tracks.
 */
public class TrackAdapter extends ArrayAdapter<TopTracks> {
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
    private View m_view;

    /**
     * Constructor for the array adapter where the layout elements' parameters are
     * initialized.
     * @param topTracks The topTracks for which the ListItem element is being generated.
     */
    public TrackAdapter(ArrayList<TopTracks> topTracks, View view, int displayWidth) {
        super(view.getContext(), 0, topTracks);
        m_view = view;
        int layoutWidthSegment = displayWidth / 120;
        m_imageWidth = layoutWidthSegment * 20;
        m_imageHeight = layoutWidthSegment * 20;
        m_imageLeftPadding = layoutWidthSegment * 11;
        m_imageTopPadding = layoutWidthSegment * 6;
        m_imageRightPadding = layoutWidthSegment * 3;
        m_imageBottomPadding = layoutWidthSegment * 6;
        m_imageScaleType = ImageView.ScaleType.CENTER;
        m_albumWidth = layoutWidthSegment * 79;
        m_albumHeight = layoutWidthSegment * 15;
        m_albumLeftPadding = layoutWidthSegment * 2;
        m_albumTopPadding = layoutWidthSegment * 6;
        m_albumRightPadding = layoutWidthSegment * 5;
        m_albumBottomPadding = 0;
        m_albumTextSize = 18;
        m_albumGravity = Gravity.BOTTOM;
        m_trackWidth = layoutWidthSegment * 79;
        m_trackHeight = layoutWidthSegment * 15;
        m_trackLeftPadding = layoutWidthSegment * 2;
        m_trackTopPadding = 0;
        m_trackRightPadding = layoutWidthSegment * 5;
        m_trackBottomPadding = layoutWidthSegment * 6;
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
        TopTracks topTracks = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(m_view.getContext())
                    .inflate(R.layout.track_item_layout, parent, false);
        }
        ImageView thumbnailView = (ImageView)convertView.findViewById(R.id.track_image);
        TextView albumName = (TextView)convertView.findViewById(R.id.album);
        TextView trackName = (TextView)convertView.findViewById(R.id.top_tracks);
        setLayout(thumbnailView, albumName, trackName);
        Picasso.with(m_view.getContext()).load(topTracks.getThumbnailURL())
                .resize(m_imageWidth, m_imageHeight).centerCrop().into(thumbnailView);
        albumName.setText(topTracks.getAlbum());
        trackName.setText(topTracks.getTrack());
        return convertView;
    }
}
