package com.hotdog.hotapp.other.listvideoplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce Too
 * On 10/20/16.
 * At 10:13
 */

public class ListDataGenerater {

    public static List<VideoModel> datas = new ArrayList<>();

    public static List<VideoModel> getListData() {
        return datas;
    }

    public static void clearDatas() {
        datas.clear();
    }

    public static void addDatas(VideoModel videoModel) {

        datas.add(videoModel);
    }
}
