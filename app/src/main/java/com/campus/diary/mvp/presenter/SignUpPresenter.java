package com.campus.diary.mvp.presenter;

import android.util.Log;

import com.campus.diary.R;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiPermission;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.SignUpContract;

import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignUpPresenter implements SignUpContract.Presenter {

    private final static String TAG = "SignUpPresenter";
    private SignUpContract.View view;

    public SignUpPresenter(SignUpContract.View view) {
        this.view = view;
    }

    @Override
    public void authority(Map<String, Object> userInfoMap) {
        if (!checkInput(userInfoMap)) {
            return;
        }
        view.showLoading(view.getResString(R.string.signuping));
        signUp(userInfoMap)
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
                            view.showToast(view.getResString(R.string.signup_success));
                            view.backSignUp();
                        } else {
                            String errString;
                            if (droiError.getCode() == DroiError.USER_ALREADY_EXISTS) {
                                errString = view.getResString(R.string.user_already_exist);
                            } else {
                                errString = view.getResString(R.string.signup_failed);
                                Log.i(TAG, "error:" + droiError);
                            }
                            view.showToast(errString);
                        }
                    }
                });
    }

    public static Observable<DroiError> signUp(final Map<String, Object> userInfoMap) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    User user = User.getCurrentUser(User.class);
                    if (user == null) {
                        user = new User();
                    }
                    user.setUserId(userInfoMap.get("mUserName").toString());
                    user.setPassword(userInfoMap.get("mUserPassword1").toString());
                    user.setNickName(userInfoMap.get("mNickName").toString());
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    user.setPermission(permission);
                    DroiError droiError = user.signUp();
                    subscriber.onNext(droiError);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    protected boolean checkInput(Map<String, Object> userInfoMap) {
        String inputInfo = userInfoMap.get("mUserName").toString();
        String checkResult = null;
        if (inputInfo.trim().length() == 0) {
            checkResult = view.getResString(R.string.username_empty);
        } else if (inputInfo.length() < 6) {
            checkResult = view.getResString(R.string.username_too_short);
        } else if (inputInfo.length() > 20) {
            checkResult = view.getResString(R.string.username_too_long);
        } else if (userInfoMap.get("mUserPassword1").toString().length() < 6) {
            checkResult = view.getResString(R.string.password_too_short);
        } else if (userInfoMap.get("mUserPassword1").toString().length() > 20) {
            checkResult = view.getResString(R.string.password_too_long);
        } else if (!userInfoMap.get("mUserPassword1").toString()
                .equals(userInfoMap.get("mUserPassword2").toString())) {
            checkResult = view.getResString(R.string.wrong_two_password);
        } else if (userInfoMap.get("mNickName").toString().trim().length() == 0) {
            checkResult = view.getResString(R.string.nick_name_empty);
        } else if (userInfoMap.get("mNickName").toString().trim().length() < 6) {
            checkResult = view.getResString(R.string.nick_too_short);
        } else if (userInfoMap.get("mNickName").toString().trim().length() > 20) {
            checkResult = view.getResString(R.string.nick_too_long);
        }

        if (checkResult != null) {
            if (view == null) {
                return false;
            }
            view.showToast(checkResult);
            return false;
        }
        return true;
    }
}
