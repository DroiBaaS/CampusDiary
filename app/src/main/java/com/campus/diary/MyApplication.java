package com.campus.diary;

import android.app.Application;

import com.campus.diary.model.CircleDeleteParameter;
import com.campus.diary.model.CircleParameter;
import com.campus.diary.model.CircleResult;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.feedback.GlideEngine;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavorItem;
import com.campus.diary.model.User;

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
        DroiFeedback.initialize(this, "KBKd3lpUMlVdw9qCdNXUB9KN97QxQ5GdP54XIVPCUQrvuTgxVnyIdTWU6YM09OAS");
        DroiFeedback.setImageEngine(new GlideEngine());
        DroiUpdate.initialize(this, "LGjbho67XpofRw7Pjc3r8YO_xfXzhjhHJFDvq9SKyl4vE25N_GYFPjMclYF_N3eY");
        DroiAnalytics.initialize(this);
        DroiPush.initialize(this, "QJYciJT3vPpJyxexAhJuF8w0WROOJ7JuZjZe3mQRsQQaSLjQyEWlED9mTThKw9Xk");
    }
}
