package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

import java.util.List;

public class CircleResult extends DroiObject {

    @DroiExpose
    private List<CircleItem> data;
    @DroiExpose
    public int code;
    @DroiExpose
    public String desc;

    public List<CircleItem> getCircles() {
        return data;
    }
}
