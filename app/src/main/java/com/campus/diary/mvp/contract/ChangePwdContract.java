package com.campus.diary.mvp.contract;

public interface ChangePwdContract {

    interface View extends BaseView {
        void back();
    }

    interface Presenter {
        void changePassword(String oldPassword, String newPassword, String newPasswordAgain);
    }
}
