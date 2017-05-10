package com.campus.diary.mvp.presenter;

import android.util.Log;
import android.view.View;

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

import static com.campus.diary.activity.MainActivity.LIMIT;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CirclePresenter implements CircleContract.Presenter {
    private CircleContract.View view;
    private static int index = 0;

    public CirclePresenter(CircleContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData(final int loadType) {
        if (loadType == 1) {
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
                        Log.i("chenpei", e.toString());
                        view.showToast("网络错误！");
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

    /**
     * @param circleId
     * @return void    返回类型
     * @throws
     * @Title: deleteCircle
     * @Description: 删除动态
     */
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
                        view.showToast("网络错误！");
                        Log.i("chenpei", e.toString());
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
                            view.showToast("删除数据失败，请重试！");
                        }
                    }
                });
    }

    /**
     * @param circlePosition
     * @return void    返回类型
     * @throws
     * @Title: addFavor
     * @Description: 点赞
     */
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
                        view.showToast("网络错误！");
                        Log.i("chenpei", e.toString());
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
                            view.showToast("点赞失败！");
                        }
                    }
                });
    }

    /**
     * @param @param circlePosition
     * @param @param favorId
     * @return void    返回类型
     * @throws
     * @Title: deleteFavor
     * @Description: 取消点赞
     */
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
                        view.showToast("网络错误！");
                        Log.i("chenpei", e.toString());
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
                            view.showToast("删除数据失败，请重试！");
                        }
                    }
                });
    }

    /**
     * @param content
     * @param config  CommentConfig
     * @return void    返回类型
     * @throws
     * @Title: addComment
     * @Description: 增加评论
     */
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
                        view.showToast("网络错误！");
                        Log.i("chenpei", e.toString());
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
                            view.showToast("评论提交失败！");
                        }
                    }
                });
    }

    /**
     * @param @param circlePosition
     * @param @param commentId
     * @return void    返回类型
     * @throws
     * @Title: deleteComment
     * @Description: 删除评论
     */
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
                        view.showToast("网络错误！");
                        Log.i("chenpei", e.toString());
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
                            view.showToast("删除数据失败，请重试！");
                        }
                    }
                });
    }

    /**
     * @param commentConfig
     */
    @Override
    public void showEditTextBody(CommentConfig commentConfig) {
        if (view != null) {
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }

    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle() {
        this.view = null;
    }

    public static Observable<List<CircleItem>> getCircleData() {
        return Observable.create(new Observable.OnSubscribe<List<CircleItem>>() {
            @Override
            public void call(final Subscriber<? super List<CircleItem>> subscriber) {
                try {
                    List<CircleItem> circleData;
                    CircleParameter parameter = new CircleParameter();
                    parameter.limit = LIMIT;
                    parameter.offset = index * LIMIT;
                    DroiError droiError = new DroiError();
                    CircleResult result = DroiCloud.callRestApi("TfO1XdOKxTaxTitvGd9KZXt7drSXm2axjlDIcsFUJ4h6jVUzLQBwBNqIQe3bL_ZY",
                            "/api/v2/getCircleList", DroiCloud.Method.POST, parameter, CircleResult.class, droiError);
                    circleData = result.getCircles();
                    /*DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).orderBy("createTime", false).limit(index * 10).query(CircleItem.class).build();
                    DroiError droiError = new DroiError();
                    List<CircleItem> circleData = query.runQuery(droiError);
                    if (circleData != null && circleData.size() != 0) {
                        for (CircleItem item : circleData) {
                            //query comment data
                            DroiCondition cond = DroiCondition.cond("circleId", DroiCondition.Type.EQ, item.getObjectId());
                            DroiQuery cquery = DroiQuery.Builder.newBuilder().where(cond).query(CommentItem.class).build();
                            List<CommentItem> cdata = cquery.runQuery(null);
                            if (cdata != null && cdata.size() != 0) {
                                item.setCommentList(cdata);
                            }
                            //query favor data
                            DroiQuery fquery = DroiQuery.Builder.newBuilder().where(cond).query(FavorItem.class).build();
                            List<FavorItem> fdata = fquery.runQuery(null);
                            if (fdata != null && fdata.size() != 0) {
                                item.setFavorList(fdata);
                            }
                        }*/

                    //subscriber.onNext(circleData);
                    /*} else {
                        subscriber.onNext(circleData);
                    }*/
                    Log.i("chenpei", "size:" + circleData.size() + ",code:" + result.code);
                    Log.i("chenpei", droiError.toString());
                    if (droiError.isOk() && circleData != null) {
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

    public static Observable<Boolean> deleteCircleData(final String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DroiError droiError = new DroiError();
                    CircleDeleteParameter parameter = new CircleDeleteParameter();
                    parameter.circleId = id;
                    CircleResult result = DroiCloud.callRestApi("TfO1XdOKxTaxTitvGd9KZXt7drSXm2axjlDIcsFUJ4h6jVUzLQBwBNqIQe3bL_ZY",
                            "/api/v2/removeCircle", DroiCloud.Method.POST, parameter, CircleResult.class, droiError);
                    if (droiError.isOk() && result.code == 0) {
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }
                    /*DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
                    DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(CircleItem.class).build();
                    DroiError droiError = new DroiError();
                    List<CircleItem> circleData = query.runQuery(droiError);
                    Boolean isDelete = false;
                    if (circleData != null && circleData.size() != 0) {
                        for (CircleItem item : circleData) {
                            DroiError error = item.delete();
                            if (error.isOk()) {
                                isDelete = true;
                                break;
                            }
                        }
                    }*/

                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<CommentItem> createComment(final String content, final CommentConfig config) {
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
                    }
                } catch (Throwable e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<Boolean> deleteComment(final String id) {
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

    public static Observable<FavorItem> createFavor(final String circleId) {
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
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<Boolean> deleteFavor(final String id) {
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
