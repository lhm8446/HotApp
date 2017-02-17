package com.hotdog.hotapp.other.videomanage.playermessages;

import android.media.MediaPlayer;

import com.hotdog.hotapp.other.videomanage.manager.VideoPlayerManagerCallback;
import com.hotdog.hotapp.other.videomanage.ui.VideoPlayerView;
import com.hotdog.hotapp.other.videomanage.PlayerMessageState;


/**
 * This PlayerMessage calls {@link MediaPlayer#release()} on the instance that is used inside {@link VideoPlayerView}
 */
public class Release extends PlayerMessage {

    public Release(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.release();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RELEASING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RELEASED;
    }
}
