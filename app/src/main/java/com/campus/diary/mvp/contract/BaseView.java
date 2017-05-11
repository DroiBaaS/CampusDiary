package com.campus.diary.mvp.contract;

import android.support.annotation.StringRes;

public interface BaseView {
    void showLoading(String msg);

    void hideLoading();

    void showToast(String msg);

    String getResString(@StringRes int resId);
}
