package com.campus.diary.mvp.presenter;

import android.util.Log;
import android.view.View;

import com.campus.diary.R;
import com.campus.diary.model.CircleDeleteParameter;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CircleParameter;
import com.campus.diary.model.CircleResult;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavorItem;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.CircleContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCloud;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiPermission;
import com.droi.sdk.core.DroiQuery;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.campus.diary.activity.MainActivity.TYPE_PULL_REFRESH;

public class CirclePresenter implements CircleContract.Presenter {
    private final static String TAG = "CirclePresenter";
    private CircleContract.View view;
    private static int index = 0;
    private static final String API_KEY = "TfO1XdOKxTaxTitvGd9KZXt7drSXm2axjlDIcsFUJ4h6jVUzLQBwBNqIQe3bL_ZY";
    public static final int LIMIT = 10;

    public CirclePresenter(CircleContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData(final int loadType) {
        if (loadType == TYPE_PULL_REFRESH) {
            index = 0;
        }
        getCircleData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CircleItem>>() {
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
                    public void onNext(List<CircleItem> data) {
                        if (view == null) {
                            return;
                        }
                        view.update2loadData(loadType, data);
                    }
                });
    }

    @Override
    public void deleteCircle(final String circleId) {
        deleteCircleData(circleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteCircle(circleId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }

    @Override
    public void addFavor(final int circlePosition, String circleId) {
        createFavor(circleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FavorItem>() {
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
                    public void onNext(FavorItem data) {
                        if (view == null) {
                            return;
                        }
                        if (data != null) {
                            view.update2AddFavor(circlePosition, data);
                        } else {
                            view.showToast(view.getResString(R.string.favor_failed));
                        }
                    }
                });
    }

    @Override
    public void deleteFavor(final int circlePosition, final String favorId) {
        deleteFavor(favorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteFavor(circlePosition, favorId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }

    @Override
    public void addComment(String content, final CommentConfig config) {
        if (config == null) {
            return;
        }
        createComment(content, config)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentItem>() {
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
                    public void onNext(CommentItem data) {
                        if (view == null) {
                            return;
                        }
                        if (data != null) {
                            view.update2AddComment(config.circlePosition, data);
                        } else {
                            view.showToast(view.getResString(R.string.comment_failed));
                        }
                    }
                });
    }

    @Override
    public void deleteComment(final int circlePosition, final String commentId) {
        deleteComment(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteComment(circlePosition, commentId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }

    @Override
    public void showEditTextBody(CommentConfig commentConfig) {
        if (view != null) {
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }

    public void recycle() {
        this.view = null;
    }

    private static Observable<List<CircleItem>> getCircleData() {
        return Observable.create(new Observable.OnSubscribe<List<CircleItem>>() {
            @Override
            public void call(final Subscriber<? super List<CircleItem>> subscriber) {
                try {
                    List<CircleItem> circleData;
                    CircleParameter parameter = new CircleParameter();
                    parameter.limit = LIMIT;
                    parameter.offset = index * LIMIT;
                    DroiError droiError = new DroiError();
                    CircleResult result = DroiCloud.callRestApi(API_KEY, "/api/v2/getCircleList",
                            DroiCloud.Method.POST, parameter, CircleResult.class, droiError);
                    circleData = result.getCircles();
                    if (droiError.isOk()) {
                        index++;
                        subscriber.onNext(circleData);
                    } else {
                        subscriber.onError(new Exception(droiError.toString()));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Observable<Boolean> deleteCircleData(final String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DroiError droiError = new DroiError();
                    CircleDeleteParameter parameter = new CircleDeleteParameter();
                    parameter.circleId = id;
                    CircleResult result = DroiCloud.callRestApi(API_KEY, "/api/v2/removeCircle",
                            DroiCloud.Method.POST, parameter, CircleResult.class, droiError);
                    if (droiError.isOk() && result.code == 0) {
                        subscriber.onNext(true);
                    } else if (!droiError.isOk()) {
                        subscriber.onError(new Exception(droiError.toString()));
                    } else {
                        subscriber.onNext(false);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Observable<CommentItem> createComment(final String content, final CommentConfig config) {
        return Observable.create(new Observable.OnSubscribe<CommentItem>() {
            @Override
            public void call(final Subscriber<? super CommentItem> subscriber) {
                try {
                    CommentItem item = new CommentItem();
                    item.setContent(content);
                    item.setUser(User.getCurrentUser(User.class));
                    item.setCircleId(config.circleId);
                    if (config.commentType == CommentConfig.Type.REPLY) {
                        item.setToReplyUser(config.replyUser);
                    }
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    permission.setPublicWritePermission(false);
                    item.setPermission(permission);
                    DroiError droiError = item.save();
                    if (droiError.isOk()) {
                        subscriber.onNext(item);
                    } else {
                        subscriber.onError(new Exception(droiError.toString()));
                    }
                } catch (Throwable e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Observable<Boolean> deleteComment(final String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
                    DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(CommentItem.class).build();
                    DroiError droiError = new DroiError();
                    List<CommentItem> data = query.runQuery(droiError);
                    Boolean isDelete = false;
                    if (data != null && data.size() != 0) {
                        for (CommentItem item : data) {
                            DroiError error = item.delete();
                            if (error.isOk()) {
                                isDelete = true;
                                break;
                            }
                        }
                    }
                    subscriber.onNext(isDelete);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Observable<FavorItem> createFavor(final String circleId) {
        return Observable.create(new Observable.OnSubscribe<FavorItem>() {
            @Override
            public void call(final Subscriber<? super FavorItem> subscriber) {
                try {
                    FavorItem item = new FavorItem();
                    item.setUser(User.getCurrentUser(User.class));
                    item.setCircleId(circleId);
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    permission.setPublicWritePermission(false);
                    item.setPermission(permission);
                    DroiError droiError = item.save();
                    if (droiError.isOk()) {
                        subscriber.onNext(item);
                    } else {
                        subscriber.onError(new Exception(droiError.toString()));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Observable<Boolean> deleteFavor(final String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
                    DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(FavorItem.class).build();
                    DroiError droiError = new DroiError();
                    List<FavorItem> data = query.runQuery(droiError);
                    Boolean isDelete = false;
                    if (data != null && data.size() != 0) {
                        for (FavorItem item : data) {
                            DroiError error = item.delete();
                            if (error.isOk()) {
                                isDelete = true;
                                break;
                            }
                        }
                    }
                    subscriber.onNext(isDelete);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }
}
