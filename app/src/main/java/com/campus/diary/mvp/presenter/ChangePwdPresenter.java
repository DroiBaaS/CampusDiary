package com.campus.diary.mvp.presenter;

import android.util.Log;

import com.campus.diary.R;
import com.campus.diary.mvp.contract.ChangePwdContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePwdPresenter implements ChangePwdContract.Presenter {
    private final static String TAG = "ChangePwdPresenter";

    private ChangePwdContract.View view;

    public ChangePwdPresenter(ChangePwdContract.View view) {
        this.view = view;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String newPasswordAgain) {
        if (!checkInput(oldPassword, newPassword, newPasswordAgain)) {
            return;
        }
        view.showLoading(view.getResString(R.string.changing));
        changeUserPassword(oldPassword, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DroiError>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(DroiError droiError) {
                        if (view == null) {
                            return;
                        }
                        if (droiError.isOk()) {
                            view.showToast(view.getResString(R.string.change_success));
                            view.back();
                        } else {
                            String errString;
                            if (droiError.getCode() == DroiError.USER_PASSWORD_INCORRECT) {
                                errString = view.getResString(R.string.wrong_old_password);
                            } else {
                                Log.i(TAG, "error:" + droiError);
                                errString = view.getResString(R.string.change_failed);
                            }
                            view.showToast(errString);
                        }
                    }
                });
    }

    private static Observable<DroiError> changeUserPassword(final String oldPassword, final String newPassword) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    DroiUser myUser = DroiUser.getCurrentUser();
                    if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
                        DroiError droiError = myUser.changePassword(oldPassword, newPassword);
                        subscriber.onNext(droiError);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private boolean checkInput(String oldPassword, String newPassword, String newPasswordAgain) {
        if (oldPassword.length() < 6) {
            view.showToast(view.getResString(R.string.password_too_short));
            return false;
        } else if (newPassword.length() < 6) {
            view.showToast(view.getResString(R.string.password_too_short));
            return false;
        } else if (newPassword.contains(" ") || oldPassword.contains(" ")) {
            view.showToast(view.getResString(R.string.password_contain_blank));
            return false;
        } else if (!newPassword.equals(newPasswordAgain)) {
            view.showToast(view.getResString(R.string.wrong_two_password));
            return false;
        }
        return true;
    }
}
