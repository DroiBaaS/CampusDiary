package com.campus.diary.mvp.contract;

public interface LogInContract {

    interface View extends BaseView {
        void gotoMainActivity();
    }

    interface Presenter {
        void authority(String userName, String password);
    }
}
