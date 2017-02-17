package com.hotdog.hotapp.other.videomanage.playermessages;


import com.hotdog.hotapp.other.videomanage.PlayerMessageState;
import com.hotdog.hotapp.other.videomanage.manager.VideoPlayerManagerCallback;
import com.hotdog.hotapp.other.videomanage.ui.VideoPlayerView;

/**
 * This is generic PlayerMessage for setDataSource
 */
public abstract class SetDataSourceMessage extends PlayerMessage {

    public SetDataSourceMessage(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_DATA_SOURCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.DATA_SOURCE_SET;
    }
}
