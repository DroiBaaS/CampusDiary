package com.campus.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.campus.diary.R;
import com.campus.diary.model.User;

public class MineActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mUserAvatar;
    private TextView mUserName;

    //private View mUserInfoItem;
    private View mUpdateItem;
    private View mFeedBackItem;
    private View mAboutItem;
    public static final int REQUEST_LOGIN = 1002;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        addTitle(getString(R.string.mine));
        setBackButton();
        mUserAvatar = (ImageView) findViewById(R.id.avatar);
        mUserName = (TextView) findViewById(R.id.username);
        mUpdateItem = findViewById(R.id.update_item);
        mUpdateItem.setOnClickListener(this);
        mFeedBackItem = findViewById(R.id.feedback_item);
        mFeedBackItem.setOnClickListener(this);
        mAboutItem = findViewById(R.id.about_item);
        mAboutItem.setOnClickListener(this);
        mUserAvatar.setOnClickListener(this);
        mUserName.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onShowView();
    }

    private void onShowView() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && !user.isAnonymous()) {
            String userName = user.getNickName();
            if (user.getHeadIcon() != null) {
                Glide.with(this)
                        .load(user.getHeadIcon().getUri())
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserAvatar);
            } else {
                mUserAvatar.setImageResource(R.drawable.default_account_icon);
            }
            if (!TextUtils.isEmpty(userName)) {
                mUserName.setText(userName);
            }
        } else {
            mUserName.setText(getString(R.string.click_login));
            mUserAvatar.setImageResource(R.drawable.default_account_icon);
        }
    }

    private void showAccountDetailOrLogin() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            showAccountDetail();
        } else {
            showLogin();
        }
    }

    private void showAccountDetail() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void showLogin() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void showAboutUs() {
        Intent intent = new Intent(MineActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void showFeedBack() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user == null || user.isAnonymous()) {
            showLogin();
        } else {
            DroiFeedback.setUserId(user.getObjectId());
            DroiFeedback.callFeedback(MineActivity.this);
        }
    }

    private void manualUpdate() {
        DroiUpdate.manualUpdate(MineActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                showAccountDetailOrLogin();
                break;
            case R.id.update_item:
                manualUpdate();
                break;
            case R.id.feedback_item:
                showFeedBack();
                break;
            case R.id.about_item:
                showAboutUs();
                break;
        }
    }
}
