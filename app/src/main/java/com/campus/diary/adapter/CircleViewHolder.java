package com.campus.diary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campus.diary.R;
import com.campus.diary.view.CommentListView;
import com.campus.diary.view.ExpandTextView;
import com.campus.diary.view.PraiseListView;
import com.campus.diary.view.SnsPopupWindow;

abstract class CircleViewHolder extends RecyclerView.ViewHolder {

    final static int TYPE_URL = 1;
    final static int TYPE_IMAGE = 2;
    final static int TYPE_VIDEO = 3;

    int viewType;
    ImageView headIv;
    TextView nameTv;
    TextView urlTipTv;
    ExpandTextView contentTv;
    TextView timeTv;
    TextView deleteBtn;
    ImageView snsBtn;
    PraiseListView praiseListView;
    LinearLayout digCommentBody;
    View digLine;
    CommentListView commentList;
    SnsPopupWindow snsPopupWindow;

    CircleViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);
        initSubView(viewType, viewStub);
        headIv = (ImageView) itemView.findViewById(R.id.headIv);
        nameTv = (TextView) itemView.findViewById(R.id.nameTv);
        digLine = itemView.findViewById(R.id.lin_dig);
        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        urlTipTv = (TextView) itemView.findViewById(R.id.urlTipTv);
        timeTv = (TextView) itemView.findViewById(R.id.timeTv);
        deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
        snsBtn = (ImageView) itemView.findViewById(R.id.snsBtn);
        praiseListView = (PraiseListView) itemView.findViewById(R.id.praiseListView);
        digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
        commentList = (CommentListView) itemView.findViewById(R.id.commentList);
        snsPopupWindow = new SnsPopupWindow(itemView.getContext());
    }

    public abstract void initSubView(int viewType, ViewStub viewStub);
}
