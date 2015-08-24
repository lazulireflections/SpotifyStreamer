package com.lazulireflections.spotifystreamer.Utilities;

import android.util.TypedValue;
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
 * Custom adapter class for artists that overrides the getView of the regular
 * ArrayAdapter and sets up the layout elements of the layout for the artists.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {
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
    private View m_view;

    /**
     * Constructor for the array adapter where layout elements' parameters are
     * initialized.
     * @param artist The artist for which the ListItem element is being generated.
     */
    public ArtistAdapter(ArrayList<Artist> artist, View view, int displayWidth) {
        super(view.getContext(), 0, artist);
        m_view = view;

        int layoutWidthSegments = displayWidth / 120;
        m_imageWidth = layoutWidthSegments * 20;
        m_imageHeight = layoutWidthSegments * 20;
        m_imageLeftPadding = layoutWidthSegments * 11;
        m_imageTopPadding = layoutWidthSegments * 6;
        m_imageRightPadding = layoutWidthSegments * 3;
        m_imageBottomPadding = layoutWidthSegments * 6;
        m_scaleType = ImageView.ScaleType.CENTER;
        m_textWidth = layoutWidthSegments * 79;
        m_textHeight = layoutWidthSegments * 20;
        m_textLeftPadding = layoutWidthSegments * 2;
        m_textTopPadding = layoutWidthSegments * 6;
        m_textRightPadding = layoutWidthSegments * 5;
        m_textBottomPadding = layoutWidthSegments * 6;
        m_textSize = layoutWidthSegments * 6;
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
        artistName.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_textSize);
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
            convertView = LayoutInflater.from(m_view.getContext())
                    .inflate(R.layout.artist_item_layout, parent, false);
        }
        ImageView thumbnailView = (ImageView)convertView.findViewById(R.id.artist_image);
        TextView nameView = (TextView)convertView.findViewById(R.id.artist_name);
        setLayout(thumbnailView, nameView);
        Picasso.with(m_view.getContext()).load(artist.getThumbnailUrl())
                .resize(m_imageWidth, m_imageHeight).centerCrop().into(thumbnailView);
        nameView.setText(artist.getName());
        return convertView;
    }
}
