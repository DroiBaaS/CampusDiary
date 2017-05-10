package com.campus.diary;

import android.app.Application;

import com.campus.diary.model.CircleDeleteParameter;
import com.campus.diary.model.CircleParameter;
import com.campus.diary.model.CircleResult;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavorItem;
import com.campus.diary.model.User;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Core.initialize(this);
        DroiObject.registerCustomClass(User.class);
        DroiObject.registerCustomClass(FavorItem.class);
        DroiObject.registerCustomClass(CommentItem.class);
        DroiObject.registerCustomClass(CircleItem.class);
        DroiObject.registerCustomClass(CircleResult.class);
        DroiObject.registerCustomClass(CircleParameter.class);
        DroiObject.registerCustomClass(CircleDeleteParameter.class);
        DroiFeedback.initialize(this);
        DroiUpdate.initialize(this);
        DroiAnalytics.initialize(this);
        DroiPush.initialize(this);
    }
}
