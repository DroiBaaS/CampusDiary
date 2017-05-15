package com.campus.diary.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.adapter.ImageGridAdapter;
import com.campus.diary.model.ImageItem;
import com.campus.diary.utils.CustomConstants;
import com.campus.diary.utils.IntentConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageChooseActivity extends BaseActivity {
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String mBucketName;
    private int availableSize;
    private GridView mGridView;
    private ImageGridAdapter mAdapter;
    private Button mFinishBtn;
    private HashMap<String, ImageItem> selectedImgs = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_choose);

        mDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                IntentConstants.EXTRA_IMAGE_LIST);
        if (mDataList == null) mDataList = new ArrayList<>();
        mBucketName = getIntent().getStringExtra(
                IntentConstants.EXTRA_BUCKET_NAME);
        if (TextUtils.isEmpty(mBucketName)) {
            mBucketName = getString(R.string.please_choose);
        }
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);
        initView();
        initListener();
    }

    private void initView() {
        addTitle(mBucketName);
        setRightButton(getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImageGridAdapter(ImageChooseActivity.this, mDataList);
        mGridView.setAdapter(mAdapter);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        String completeString = String.format(getString(R.string.complete_num),
                selectedImgs.size(), availableSize);
        mFinishBtn.setText(completeString);
        mAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mFinishBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(
                        IntentConstants.EXTRA_IMAGE_LIST,
                        new ArrayList<>(selectedImgs
                                .values()));
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ImageItem item = mDataList.get(position);
                if (item.isSelected) {
                    item.isSelected = false;
                    selectedImgs.remove(item.imageId);
                } else {
                    if (selectedImgs.size() >= availableSize) {
                        String maxChoose = String.format(getString(R.string.max_choose),
                                availableSize);
                        Toast.makeText(getApplicationContext(),
                                maxChoose, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.isSelected = true;
                    selectedImgs.put(item.imageId, item);
                }
                String completeString = String.format(getString(R.string.complete_num),
                        selectedImgs.size(), availableSize);
                mFinishBtn.setText(completeString);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}