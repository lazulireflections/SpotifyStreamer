package com.lazulireflections.spotifystreamer.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class holds the Artist entity information and makes it parcelable.
 * Artist information consists of the artists name and the URL to the artists
 * image, to be used as a thumbnail.
 *
 * Parcelable as part of saving the activity instance as described on stackoverflow.com
 */
public class Artist implements Parcelable {
    private String m_thumbnailUrl;
    private String m_name;
    private String m_id;

    /**
     * Constructs a new artist.
     * @param thumbnailUrl The URL to the artists thumbnail image.
     * @param name The artists name.
     */
    public Artist(String thumbnailUrl, String name, String id){
        m_thumbnailUrl = thumbnailUrl;
        m_name = name;
        m_id = id;
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
    @SuppressWarnings("unused")
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

    /**
     * Returns the Spotify ID for the selected artist.
     * @return Spotify ID for the selected artist.
     */
    public String getId() {
        return m_id;
    }
}
