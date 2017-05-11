package com.campus.diary.adapter;

import android.view.View;
import android.view.ViewStub;

import com.campus.diary.R;
import com.campus.diary.view.MultiImageView;

class ImageViewHolder extends CircleViewHolder {

    MultiImageView multiImageView;

    ImageViewHolder(View itemView) {
        super(itemView, TYPE_IMAGE);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if (viewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = viewStub.inflate();
        MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multi_image_view);
        if (multiImageView != null) {
            this.multiImageView = multiImageView;
        }
    }
}
