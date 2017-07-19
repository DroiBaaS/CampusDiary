package com.campus.diary.model;

import android.text.TextUtils;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

import java.util.List;

public class CircleItem extends DroiObject {

    public final static int TYPE_URL = 1;
    public final static int TYPE_IMG = 2;
    @DroiExpose
    private String content;
    @DroiExpose
    private int type;//1:链接  2:图片
    @DroiExpose
    private String linkImg;
    @DroiExpose
    private String linkTitle;
    @DroiReference
    private List<DroiFile> photos;
    @DroiReference
    private User user;
    @DroiReference
    private List<CommentItem> commentList;
    @DroiReference
    private List<FavorItem> favorList;

    private boolean isExpand;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<FavorItem> getFavorList() {
        return favorList;
    }

    public void setFavorList(List<FavorItem> favors) {
        this.favorList = favors;
    }

    public List<CommentItem> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentItem> comments) {
        this.commentList = comments;
    }

    public String getCreateTime() {
        return getCreationTime().toLocaleString();
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public boolean hasFavor() {
        return favorList != null && favorList.size() > 0;
    }

    public boolean hasComment() {
        return commentList != null && commentList.size() > 0;
    }

    public String getCurUserFavorId(String curUserId) {
        String favorId = "";
        if (!TextUtils.isEmpty(curUserId) && hasFavor()) {
            for (FavorItem favor : getFavorList()) {
                if (curUserId.equals(favor.getUser().getObjectId())) {
                    favorId = favor.getObjectId();
                    return favorId;
                }
            }
        }
        return favorId;
    }

    public List<DroiFile> getPhotos() {
        return photos;
    }

    public void setPhotos(List<DroiFile> photos) {
        this.photos = photos;
    }

}
