package com.campus.diary.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.campus.diary.R;
import com.campus.diary.adapter.CircleAdapter;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavorItem;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.CircleContract;
import com.campus.diary.mvp.presenter.CirclePresenter;
import com.campus.diary.utils.CommonUtils;
import com.campus.diary.view.CommentListView;
import com.campus.diary.view.DivItemDecoration;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.campus.diary.mvp.presenter.CirclePresenter.LIMIT;

public class MainActivity extends BaseActivity implements CircleContract.View {

    private CircleAdapter circleAdapter;
    private LinearLayout editTextBody;
    private EditText editText;
    private ImageView sendIv;
    private String currentUserId = "";
    private int screenHeight;
    private int editTextBodyHeight;
    private int titleHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;

    private CirclePresenter presenter;
    private CommentConfig commentConfig;
    private SuperRecyclerView recyclerView;
    private RelativeLayout bodyLayout;
    private LinearLayoutManager layoutManager;

    public final static int TYPE_PULL_REFRESH = 1;
    private final static int TYPE_UPLOAD_REFRESH = 2;
    public static final int PUBLISH_DIARY = 1001;
    public static final int REQUEST_LOGIN = 1002;

    private SwipeRefreshLayout.OnRefreshListener refreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new CirclePresenter(this);
        initView();
        refreshView();
        DroiUpdate.update(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getCurrentUser(User.class);
        if (user == null || !currentUserId.equals(user.getUserId())) {
            if (user != null) {
                currentUserId = user.getUserId();
            }
            circleAdapter.notifyDataSetChanged();
            refreshView();
        }
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.recycle();
        }

        super.onDestroy();
    }

    void refreshView() {
        recyclerView.getSwipeToRefresh().post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setRefreshing(true);
                refreshListener.onRefresh();
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "InlinedApi"})
    private void initView() {
        initTitle();
        recyclerView = (SuperRecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (editTextBody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadData(TYPE_PULL_REFRESH);
            }
        };
        recyclerView.setRefreshListener(refreshListener);
        circleAdapter = new CircleAdapter(this);
        circleAdapter.setCirclePresenter(presenter);
        recyclerView.setAdapter(circleAdapter);

        editTextBody = (LinearLayout) findViewById(R.id.editTextBodyLl);
        editText = (EditText) findViewById(R.id.circleEt);
        sendIv = (ImageView) findViewById(R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    String content = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(getApplicationContext(), "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });
        setViewTreeObserver();
    }

    private void initTitle() {
        addTitle(getString(R.string.app_name));
        setRightButton(getString(R.string.publish_circle), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = DroiUser.getCurrentUser(User.class);
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                    startActivityForResult(intent, PUBLISH_DIARY);
                } else {
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });
    }

    private void setViewTreeObserver() {
        bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = editTextBody.getHeight();
                RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
                titleHeight = titleBar.getHeight();
                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                if (layoutManager != null && commentConfig != null) {
                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CircleAdapter.HEAD_VIEW_SIZE, getListViewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (editTextBody != null && editTextBody.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        List<CircleItem> circleItems = circleAdapter.getDatas();
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId.equals(circleItems.get(i).getObjectId())) {
                circleItems.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddFavor(int circlePosition, FavorItem addItem) {
        if (addItem != null) {
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            if (item.getFavorList() == null) {
                List<FavorItem> favors = new ArrayList<>();
                item.setFavorList(favors);
            }
            item.getFavorList().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void update2DeleteFavor(int circlePosition, String favortId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<FavorItem> items = item.getFavorList();
        for (int i = 0; i < items.size(); i++) {
            if (favortId.equals(items.get(i).getObjectId())) {
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {
        if (addItem != null) {
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            if (item.getCommentList() == null) {
                List<CommentItem> comments = new ArrayList<>();
                item.setCommentList(comments);
            }
            item.getCommentList().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
        editText.setText("");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<CommentItem> items = item.getCommentList();
        for (int i = 0; i < items.size(); i++) {
            if (commentId.equals(items.get(i).getObjectId())) {
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        editTextBody.setVisibility(visibility);
        measureCircleItemHighAndCommentItemOffset(commentConfig);
        if (View.VISIBLE == visibility) {
            editText.requestFocus();
            CommonUtils.showSoftInput(editText.getContext(), editText);
        } else if (View.GONE == visibility) {
            CommonUtils.hideSoftInput(editText.getContext(), editText);
        }
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (datas == null) {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
            return;
        }
        if (loadType == TYPE_PULL_REFRESH) {
            recyclerView.setRefreshing(false);
            circleAdapter.setDatas(datas);
        } else if (loadType == TYPE_UPLOAD_REFRESH) {
            circleAdapter.getDatas().addAll(datas);
        }
        circleAdapter.notifyDataSetChanged();
        if (circleAdapter.getDatas().size() < 45 + CircleAdapter.HEAD_VIEW_SIZE) {
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    if (circleAdapter.getDatas().size() % LIMIT == 0) {
                        presenter.loadData(TYPE_UPLOAD_REFRESH);
                    } else {
                        recyclerView.hideMoreProgress();
                    }
                }
            }, 1);
        } else {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    /**
     * 测量偏移量
     *
     * @param commentConfig
     * @return
     */
    private int getListViewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        int listViewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight - titleHeight;
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            listViewOffset = listViewOffset + selectCommentItemOffset;
        }
        return listViewOffset;
    }

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + CircleAdapter.HEAD_VIEW_SIZE - firstPosition);
        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }


    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void hideLoading() {
        recyclerView.setRefreshing(false);
        recyclerView.hideMoreProgress();
    }

    @Override
    public void showToast(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getString(resId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PUBLISH_DIARY) {
                refreshView();
            }
        }
    }
}
