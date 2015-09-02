package com.lazulireflections.spotifystreamer.Utilities;


import android.support.v7.app.ActionBar;

import com.lazulireflections.spotifystreamer.PlayerDialog;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * A class to hold all utility functions and variables that need to be accessible throughout
 * the entire app.
 */
public class Utility {
    private static boolean m_TabletLayout;
    private static PlayerDialog m_dialog;

    /**
     * To determine if the phone layout or the tablet layout is to be used.
     * @return Boolean flag for weather to use phone or tablet layout.
     */
    public static boolean getTabletLayout() {
        return m_TabletLayout;
    }

    /**
     * Set weather the phone layout or the tablet layout is to be used.
     * @param tabletLayout Boolean flag for weather to use phone or tablet layout.
     */
    public static void setTabletLayout(boolean tabletLayout) {
        m_TabletLayout = tabletLayout;
    }

    /**
     * Returns the URL of the artist image and puts in a placeholder
     * for those artists for whom no images are found.
     * @param image List of images for a given artist.
     * @param targetSize The target size for the image.
     * @return The url of the chosen image or placeholder if no image is found.
     */
    public static String findImageUrl(List<Image> image, int targetSize) {
        String url = "";
        int currentSize = -1;
        for(int i = 0; i < image.size(); i++) {
            if((image.get(i).width < currentSize && image.get(i).width >= targetSize) || currentSize == -1) {
                url = image.get(i).url;
                currentSize = image.get(i).width;
            }
        }
        if(url.equals("")) {
            url = "https://lh3.googleusercontent.com/-edYeK6MZ4wk/VY1yOp5vu5I/AAAAAAAAAXE/WNNAlS9rRBA/s65/placeholder.png";
        }
        return url;
    }

    public static void setDialog(PlayerDialog dialog){
        m_dialog = dialog;
    }

    public static PlayerDialog getDialog() {
        return m_dialog;
    }
}
