package com.app.appsinrek.player_view.utils;


import com.app.appsinrek.player_view.enums.PostType;

import java.util.ArrayList;
import java.util.List;

public class DummyData {
    public static List<PostModel> getDummyData() {
        List<PostModel> dummyFeedList = new ArrayList<>();
        dummyFeedList.add(new PostModel(PostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"));
        dummyFeedList.add(new PostModel(PostType.TEXT, "The secret to life is to love who you are - warts and all."));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=1"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=2"));
        dummyFeedList.add(new PostModel(PostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=3"));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Today my kitchen is \"family central.\" Life happens there."));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=4"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=5"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=6"));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Look for opportunities in every change in your life."));
        dummyFeedList.add(new PostModel(PostType.TEXT, "The story of life is quicker than the wink of an eye."));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Life really does begin at forty. Up until then, you are just doing research. "));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=7"));
        dummyFeedList.add(new PostModel(PostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"));
        dummyFeedList.add(new PostModel(PostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=8"));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Life is a question and how we live it is our answer."));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=9"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=10"));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Life is a long lesson in humility."));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Accept life as it is. Then work to make it the way you want it to be."));
        dummyFeedList.add(new PostModel(PostType.TEXT, "Where there is love there is life "));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=11"));
        dummyFeedList.add(new PostModel(PostType.IMAGE, "https://picsum.photos/300/200?random=12"));
        dummyFeedList.add(new PostModel(PostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        return dummyFeedList;
    }


    public static String getDummyPlaceholder() {
        return "https://nwtangsoodo.com/wp-content/uploads/sites/191/2015/12/video-placeholder.png";
    }

}
