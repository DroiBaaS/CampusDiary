package com.campus.diary.mvp.presenter;

import android.util.Log;

import com.campus.diary.R;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.SignInContract;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LogInPresenter implements SignInContract.Presenter {

    private final static String TAG = "LogInPresenter";
    private SignInContract.View view;

    public LogInPresenter(SignInContract.View view) {
        this.view = view;
    }

    @Override
    public void authority(String username, String password) {
        if (!checkInput(username, password)) {
            return;
        }
        view.showLoading(view.getResString(R.string.logining));
        signIn(username, password)
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
                    }

                    @Override
                    public void onNext(DroiError droiError) {
                        if (view == null) {
                            return;
                        }
                        if (droiError.isOk()) {
                            view.showToast(view.getResString(R.string.login_success));
                            view.gotoMainActivity();
                        } else {
                            if (droiError.getCode() == DroiError.USER_PASSWORD_INCORRECT) {
                                view.showToast(view.getResString(R.string.wrong_password));
                            } else {
                                Log.i(TAG, "error:" + droiError);
                                view.showToast(view.getResString(R.string.error));
                            }
                        }
                    }
                });
    }

    private static Observable<DroiError> signIn(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    DroiError droiError = new DroiError();
                    DroiUser.login(username, password, User.class, droiError);
                    subscriber.onNext(droiError);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private boolean checkInput(String username, String password) {
        if (view == null) {
            return false;
        }
        if (username.length() < 6) {
            view.showToast(view.getResString(R.string.username_too_short));
            return false;
        } else if (username.contains(" ")) {
            view.showToast(view.getResString(R.string.username_contain_blank));
            return false;
        } else if (password.length() < 6) {
            view.showToast(view.getResString(R.string.password_too_short));
            return false;
        } else if (password.contains(" ")) {
            view.showToast(view.getResString(R.string.password_contain_blank));
            return false;
        }
        return true;
    }
}
