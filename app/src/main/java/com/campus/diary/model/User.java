package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class User extends DroiUser {
    @DroiExpose
    private DroiFile headIcon;
    @DroiExpose
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public DroiFile getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(DroiFile headIcon) {
        this.headIcon = headIcon;
    }
}
