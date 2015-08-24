package com.lazulireflections.spotifystreamer.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class holds the TopTracks entity information and makes it parcelable.
 * TopTracks information consists of the track name, album name, the URL to the track
 * image and the URL to the track preview.
 *
 * Parcelable as part of saving the activity instance as described on stackoverflow.com
 */
public class TopTracks implements Parcelable {
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
    public TopTracks(String thumbnailUrl, String album, String track, String previewUrl) {
        m_thumbnailUrl = thumbnailUrl;
        m_album = album;
        m_track = track;
        m_previewUrl = previewUrl;
    }

    /**
     * Construct a new track.
     * @param parcel The artist info in parcelable form.
     */
    public TopTracks(Parcel parcel) {
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
    @SuppressWarnings("unused")
    public Creator<TopTracks> CREATOR = new Creator<TopTracks>() {
        @Override
        public TopTracks createFromParcel(Parcel source) {
            return new TopTracks(source);
        }

        @Override
        public TopTracks[] newArray(int size) {
            return new TopTracks[size];
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

    /**
     * Returns the url of the track image.
     * @return Url of the track image.
     */
    public String getPreviewUrl() {
        return m_previewUrl;
    }
}
