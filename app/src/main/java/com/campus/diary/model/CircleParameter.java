package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 2017/5/9.
 */

public class CircleParameter extends DroiObject {
    @DroiExpose
    public int offset;
    @DroiExpose
    public int limit;
}
