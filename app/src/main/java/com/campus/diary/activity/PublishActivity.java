package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.adapter.ImagePublishAdapter;
import com.campus.diary.model.ImageItem;
import com.campus.diary.mvp.contract.PublishContract;
import com.campus.diary.mvp.presenter.PublishPresenter;
import com.campus.diary.utils.CommonUtils;
import com.campus.diary.utils.CustomConstants;
import com.campus.diary.utils.IntentConstants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends BaseActivity implements PublishContract.View, View.OnClickListener {

    private GridView mGridView;
    private EditText contentEdit;
    private ImagePublishAdapter mAdapter;
    private PublishContract.Presenter publishLogic;
    public static List<ImageItem> mDataList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    View selectPic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        publishLogic = new PublishPresenter(PublishActivity.this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyDataChanged();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
        mDataList.clear();
    }

    public void initView() {
        addTitle(getString(R.string.publish_circle));
        setBackButton();
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        selectPic = findViewById(R.id.select_pic);
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        progressDialog = new ProgressDialog(PublishActivity.this);
        contentEdit = (EditText) findViewById(R.id.contentText);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    CommonUtils.hideSoftInput(PublishActivity.this, contentEdit);
                    selectPic.setVisibility(View.VISIBLE);
                } else {
                    Intent intent = new Intent(PublishActivity.this,
                            ImageZoomActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) mDataList);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivity(intent);
                }
            }
        });
        setRightButton(getString(R.string.send), new OnClickListener() {

            public void onClick(View v) {
                String content = contentEdit.getText().toString();
                if (TextUtils.isEmpty(content.trim()) && mDataList.size() == 0) {
                    showToast(getString(R.string.content_photo_all_empty));
                    return;
                }
                publishLogic.sendData(mDataList, content);
            }
        });
    }

    private int getDataSize() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private int getAvailableSize() {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0) {
            return availSize;
        }
        return 0;
    }

    private static final int TAKE_PICTURE = 0x000000;
    public static final int CHOOSE_PHOTO = 0x000001;
    private String path = "";

    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/CampusDiary/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        Uri cameraUri = FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".provider", vFile);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        path = vFile.getPath();
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        openCameraIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(
                    openCameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, cameraUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        if (openCameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(path)) {
                    ImageItem item = new ImageItem();
                    item.sourcePath = path;
                    mDataList.add(item);
                }
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    List<ImageItem> incomingDataList = (List<ImageItem>) data
                            .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
                    if (incomingDataList != null && incomingDataList.size() != 0) {
                        mDataList.addAll(incomingDataList);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                takePhoto();
                selectPic.setVisibility(View.GONE);
                break;
            case R.id.btn_pick_photo:
                Intent intent = new Intent(PublishActivity.this,
                        ImageBucketChooseActivity.class);
                intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                        getAvailableSize());
                startActivityForResult(intent, CHOOSE_PHOTO);
                selectPic.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel:
                selectPic.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String result) {
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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
    public void gotoMainActivity() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getString(resId);
    }
}