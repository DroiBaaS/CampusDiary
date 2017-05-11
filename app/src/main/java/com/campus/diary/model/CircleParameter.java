package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

public class CircleParameter extends DroiObject {
    @DroiExpose
    public int offset;
    @DroiExpose
    public int limit;
}
