package com.campus.diary.mvp.contract;

import android.content.Context;
import android.content.Intent;

public interface ProfileContract {

    interface View extends BaseView {
        void showToastByResID(int strId);

        void refreshProfile();

        void refreshNickname(String nickname);

        void finishActivity();
    }

    interface Presenter {
        void uploadHeadIcon(Context context, int resultCode, Intent data);

        void logout();

        void getHeadIcon();

        void updateNickname(String name);
    }
}
