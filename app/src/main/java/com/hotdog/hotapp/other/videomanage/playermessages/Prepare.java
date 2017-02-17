package com.hotdog.hotapp.other.videomanage.playermessages;

import android.media.MediaPlayer;

import com.hotdog.hotapp.other.videomanage.PlayerMessageState;
import com.hotdog.hotapp.other.videomanage.manager.VideoPlayerManagerCallback;
import com.hotdog.hotapp.other.videomanage.ui.VideoPlayerView;


/**
 * This PlayerMessage calls {@link MediaPlayer#prepare()} on the instance that is used inside {@link VideoPlayerView}
 */
public class Prepare extends PlayerMessage {

    public Prepare(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.prepare();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.PREPARING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PREPARED;
    }
}
