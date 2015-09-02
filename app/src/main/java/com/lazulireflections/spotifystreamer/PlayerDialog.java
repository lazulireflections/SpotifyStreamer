package com.lazulireflections.spotifystreamer;

import android.support.v7.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lazulireflections.spotifystreamer.Utilities.TopTracks;
import com.lazulireflections.spotifystreamer.Utilities.Utility;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Dialog class for the media player dialog.
 */
public class PlayerDialog extends DialogFragment {
    private View m_rootView;
    private MediaPlayer m_mediaPlayer;
    private boolean m_playing;
    private int m_layoutSegments;
    private int m_duration;
    private int m_trackPosition;

    private TextView m_artistNameTextView;
    private TextView m_albumNameTextView;
    private ImageView m_albumImageImageView;
    private TextView m_trackNameTextView;
    private SeekBar m_progressScrubBar;
    private TextView m_trackLengthTextView;
    private ImageButton m_previousButton;
    private ImageButton m_playPauseButton;
    private ImageButton m_nextButton;
    private int m_trackIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_rootView = inflater.inflate(R.layout.dialog_player, container, false);
        if(savedInstanceState != null) {
            m_trackIndex = savedInstanceState.getInt("track_index");
        } else {
            m_trackIndex = getArguments().getInt("location");
        }
        ArrayList<TopTracks> m_topTracks = getArguments().getParcelableArrayList("tracklist");
        String artistName = getArguments().getString("artistname");
        initializeDialog();
        populateDialog(m_trackIndex, m_topTracks, artistName);
        return m_rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getActionBar().hide();
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        m_mediaPlayer.stop();
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
        m_mediaPlayer.stop();
        savedInstanceState.putInt("track_index", m_trackIndex);
    }

    /**
     * As this dialog plays only demos I choose to cancel the playing of the track if the
     * dialog is closed.
     * @param dialogInterface Input from system.
     */
    @Override
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        m_mediaPlayer.stop();
        m_mediaPlayer.release();
        m_mediaPlayer = null;
    }

    /**
     * Initializing the view elements of the dialog.
     */
    public void initializeDialog() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        m_layoutSegments = getResources().getDisplayMetrics().widthPixels;
        if(dm.heightPixels < m_layoutSegments) {
            m_layoutSegments = dm.heightPixels;
        }
        m_layoutSegments = m_layoutSegments / 120;

        LinearLayout playerMainLayout = (LinearLayout) m_rootView
                .findViewById(R.id.player_layout_container);
        playerMainLayout.setMinimumWidth(m_layoutSegments * 120);
        playerMainLayout.setPadding(m_layoutSegments * 35, m_layoutSegments * 5,
                m_layoutSegments * 35, m_layoutSegments * 5);

        m_artistNameTextView = (TextView)m_rootView.findViewById(R.id.player_artist_name);
        m_artistNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_layoutSegments * 4);

        m_albumNameTextView = (TextView)m_rootView.findViewById(R.id.player_album_name);
        m_albumNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_layoutSegments * 4);
        m_albumNameTextView.setPadding(0, m_layoutSegments * 2, 0, 0);

        m_albumImageImageView = (ImageView)m_rootView.findViewById(R.id.player_album_art);
        m_albumImageImageView.setPadding(0, m_layoutSegments * 4, 0, 0);

        m_trackNameTextView = (TextView)m_rootView.findViewById(R.id.player_track_name);
        m_trackNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_layoutSegments * 4);
        m_trackNameTextView.setPadding(0, m_layoutSegments * 2, 0, 0);

        m_progressScrubBar = (SeekBar)m_rootView.findViewById(R.id.player_seekBar);
        m_progressScrubBar.setPadding(m_layoutSegments, 0, m_layoutSegments, 0);
        m_progressScrubBar.setMinimumWidth(m_layoutSegments * 50);
        m_progressScrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (m_mediaPlayer != null && fromUser) {
                    m_trackPosition = (int) (((double) m_duration / (double) m_progressScrubBar.getMax()) * (double) progress);
                    m_mediaPlayer.seekTo(m_trackPosition * 1000);
                    updateScrub();
                }
            }
        });

        TextView trackStartTimeTextView = (TextView) m_rootView.findViewById(R.id.player_start_time);
        trackStartTimeTextView.setText("00:00");
        trackStartTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_layoutSegments * 4);
        trackStartTimeTextView.setPadding(0, 0, 0, 0);

        m_trackLengthTextView = (TextView)m_rootView.findViewById(R.id.player_track_length);
        m_trackLengthTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_layoutSegments * 4);
        m_trackLengthTextView.setPadding(0, 0, 0, 0);

        m_previousButton = (ImageButton)m_rootView.findViewById(R.id.player_previous);
        m_previousButton.setPadding(0, m_layoutSegments, 0, m_layoutSegments);
        m_previousButton.setMinimumWidth(m_layoutSegments * 12);
        m_previousButton.setMaxWidth(m_layoutSegments * 12);

        m_playPauseButton = (ImageButton)m_rootView.findViewById(R.id.player_play_pause);

        m_playPauseButton.setPadding(0, m_layoutSegments, 0, m_layoutSegments);
        m_playPauseButton.setMinimumWidth(m_layoutSegments * 12);
        m_playPauseButton.setMaxWidth(m_layoutSegments * 12);
        m_playPauseButton.setTag(android.R.drawable.ic_media_play);
        m_playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(m_playPauseButton.getTag().toString()) ==
                        android.R.drawable.ic_media_play) {
                    m_playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    m_playPauseButton.setTag(android.R.drawable.ic_media_pause);
                    m_playing = false;
                    m_mediaPlayer.pause();
                } else {
                    m_playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    m_playPauseButton.setTag(android.R.drawable.ic_media_play);
                    m_playing = true;
                    m_mediaPlayer.start();
                }
            }
        });

        m_nextButton = (ImageButton)m_rootView.findViewById(R.id.player_next);
        m_nextButton.setPadding(0, m_layoutSegments, 0, m_layoutSegments);
        m_nextButton.setMinimumWidth(m_layoutSegments * 12);
        m_nextButton.setMaxWidth(m_layoutSegments * 12);
    }

    /**
     * Fill in the display data into the dialog's views.
     * @param position Position of the selected track.
     * @param artistList List of tracks for the selected artist.
     * @param artistName Name of the selected artist.
     */
    public void populateDialog(final int position, final ArrayList<TopTracks> artistList,
                               final String artistName) {
        m_trackIndex = position;
        m_mediaPlayer = new MediaPlayer();
        m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            m_mediaPlayer.stop();
            m_mediaPlayer.setDataSource(m_rootView.getContext(),
                    Uri.parse(artistList.get(position).getPreviewUrl()));
            m_mediaPlayer.prepare();
            m_playing = true;
            m_mediaPlayer.start();
            new BackgroundThread().execute();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        m_artistNameTextView.setText(artistName);
        m_albumNameTextView.setText(artistList.get(position).getAlbum());
        Picasso.with(m_rootView.getContext()).load(artistList.get(position).getThumbnailURL())
                .resize(m_layoutSegments * 50, m_layoutSegments * 50)
                .centerCrop().into(m_albumImageImageView);
        m_trackNameTextView.setText(artistList.get(position).getTrack());

        m_duration = m_mediaPlayer.getDuration() / 1000;
        int sec = m_duration % 60;
        int min = (m_duration - sec) / 60;
        String durationString = min + ":" + sec;
        m_trackLengthTextView.setText(durationString);

        m_previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                m_playPauseButton.setTag(android.R.drawable.ic_media_play);
                m_mediaPlayer.stop();
                if (position == 0) {
                    populateDialog(artistList.size() - 1, artistList, artistName);
                } else {
                    populateDialog(position - 1, artistList, artistName);
                }
            }
        });
        m_nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                m_playPauseButton.setTag(android.R.drawable.ic_media_play);
                m_mediaPlayer.stop();
                if (position == artistList.size() - 1) {
                    populateDialog(0, artistList, artistName);
                } else {
                    populateDialog(position + 1, artistList, artistName);
                }
            }
        });
    }

    /**
     * Updating the scrub bar, needs to be here and not in the background thread.
     */
    private void updateScrub() {
        if(m_trackPosition == 0)
        {
            m_progressScrubBar.setProgress(0);
        } else {
            m_progressScrubBar.setProgress((int)((double)m_trackPosition *
                    ((double)m_progressScrubBar.getMax() / (double)m_duration)));
        }
    }

    /**
     * Async class to update the scrub bar in the background.
     */
    private class BackgroundThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... value) {
            while(m_playing) {
                if (m_mediaPlayer != null) {
                    m_trackPosition = m_mediaPlayer.getCurrentPosition() / 1000;
                    updateScrub();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
