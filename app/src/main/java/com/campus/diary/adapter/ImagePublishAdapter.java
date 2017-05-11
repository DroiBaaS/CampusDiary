package com.campus.diary.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.campus.diary.R;
import com.campus.diary.model.ImageItem;
import com.campus.diary.utils.CustomConstants;
import com.campus.diary.utils.ImageDisplay;

import java.util.ArrayList;
import java.util.List;

public class ImagePublishAdapter extends BaseAdapter {
    private List<ImageItem> mDataList = new ArrayList<>();
    private Context mContext;

    public ImagePublishAdapter(Context context, List<ImageItem> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public int getCount() {
        if (mDataList == null) {
            return 1;
        } else if (mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return CustomConstants.MAX_IMAGE_SIZE;
        } else {
            return mDataList.size() + 1;
        }
    }

    public Object getItem(int position) {
        if (mDataList != null
                && mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return mDataList.get(position);
        } else if (mDataList == null || position - 1 < 0
                || position > mDataList.size()) {
            return null;
        } else {
            return mDataList.get(position - 1);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.item_publish, null);
        ImageView imageIv = (ImageView) convertView.findViewById(R.id.item_grid_image);
        if (isShowAddItem(position)) {
            imageIv.setImageResource(R.drawable.btn_add_pic);
            imageIv.setBackgroundResource(R.color.bg_gray);
        } else {
            final ImageItem item = mDataList.get(position);
            ImageDisplay.getInstance(mContext).displayBmp(imageIv, item.thumbnailPath, item.sourcePath);
        }
        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = mDataList == null ? 0 : mDataList.size();
        return position == size;
    }

}
