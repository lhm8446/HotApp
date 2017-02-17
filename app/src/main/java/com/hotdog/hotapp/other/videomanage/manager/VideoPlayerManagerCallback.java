package com.hotdog.hotapp.other.videomanage.manager;


import com.hotdog.hotapp.other.videomanage.PlayerMessageState;
import com.hotdog.hotapp.other.videomanage.meta.MetaData;
import com.hotdog.hotapp.other.videomanage.ui.VideoPlayerView;

/**
 * This callback is used by {@link com.brucetoo.listvideoplay.videomanage.playermessages.PlayerMessage}
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
