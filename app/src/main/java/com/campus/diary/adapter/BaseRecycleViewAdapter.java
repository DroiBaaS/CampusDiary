package com.campus.diary.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecycleViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> datas = new ArrayList<>();

    public List<T> getDatas() {
        if (datas == null)
            datas = new ArrayList<>();
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
