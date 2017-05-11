package com.campus.diary.mvp.contract;

import java.util.Map;

public interface SignUpContract {

    interface View extends BaseView {
        Map<String, Object> getSignUpInfo();

        void gotoSignInView();
    }

    interface Presenter {
        void authority(Map<String, Object> userInfoMap);
    }
}
