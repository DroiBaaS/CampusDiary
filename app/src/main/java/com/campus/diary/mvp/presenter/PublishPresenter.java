package com.campus.diary.mvp.presenter;

import android.util.Log;

import com.campus.diary.R;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.ImageItem;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.PublishContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PublishPresenter implements PublishContract.Presenter {

    private final static String TAG= "PublishPresenter";
    private PublishContract.View view;

    public PublishPresenter(PublishContract.View view) {
        this.view = view;
    }

    @Override
    public void sendData(List<ImageItem> items, String content) {
        view.showLoading(view.getResString(R.string.sending));
        createCircleItem(items, content)
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
                            view.gotoMainActivity();
                        } else {
                            Log.i(TAG, "error:" + droiError);
                            view.showToast(view.getResString(R.string.send_failed));
                        }
                    }
                });
    }


    private static List<DroiFile> createPhotos(List<ImageItem> items) {
        List<DroiFile> photos = new ArrayList<>();
        for (ImageItem item : items) {
            DroiPermission permission = new DroiPermission();
            permission.setPublicReadPermission(true);
            permission.setPublicWritePermission(false);
            DroiFile file = new DroiFile(new File(item.sourcePath));
            file.setPermission(permission);
            photos.add(file);
        }
        return photos;
    }

    private static Observable<DroiError> createCircleItem(final List<ImageItem> items, final String content) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    CircleItem data = new CircleItem();
                    data.setContent(content);
                    data.setUser(User.getCurrentUser(User.class));
                    data.setType(2); //保留字段
                    data.setPhotos(createPhotos(items));
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    permission.setPublicWritePermission(false);
                    data.setPermission(permission);
                    DroiError droiError = data.save();
                    subscriber.onNext(droiError);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }
}
