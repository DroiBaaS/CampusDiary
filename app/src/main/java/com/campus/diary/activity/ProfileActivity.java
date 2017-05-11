package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.campus.diary.R;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.ProfileContract;
import com.campus.diary.mvp.presenter.ProfilePresenter;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

public class ProfileActivity extends BaseActivity implements ProfileContract.View, View.OnClickListener {
    private TextView userNameText, changeNickTv;
    private ImageView headImageView;
    private View selectPic;
    private ProgressDialog progressDialog;
    private ProfilePresenter profileLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileLogic = new ProfilePresenter(this);
        initUI();
        profileLogic.getHeadIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        addTitle(R.string.profile);
        setBackButton();
        progressDialog = new ProgressDialog(this);
        userNameText = (TextView) findViewById(R.id.user_id);
        headImageView = (ImageView) findViewById(R.id.head_pic);
        findViewById(R.id.head).setOnClickListener(this);
        findViewById(R.id.profile_logout).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        Button btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        selectPic = findViewById(R.id.select_pic);
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                selectPic.setVisibility(View.VISIBLE);
                break;
            case R.id.profile_logout:
                DroiAnalytics.onEvent(this, "logout");
                profileLogic.logout();
                break;
            case R.id.change_password:
                Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.btn_take_photo:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_pick_photo:
                try {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    } else {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                    }
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                hidePopMenu();
                break;
            default:
                break;
        }
    }

    public void hidePopMenu() {
        selectPic.setVisibility(View.GONE);
    }

    @Override
    public void refreshProfile() {
        User user = User.getCurrentUser(User.class);
        if (!user.isAnonymous() && user.getHeadIcon() != null) {
            Glide.with(this)
                    .load(user.getHeadIcon().getUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(headImageView);
        }
        userNameText.setText(user.getUserId());
    }

    @Override
    public void refreshNickname(String nickname) {
        changeNickTv.setText(nickname);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        hidePopMenu();
        profileLogic.uploadHeadIcon(this, resultCode, data);
    }

    @Override
    public void showToast(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastByResID(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(String msg) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getString(resId);
    }
}
