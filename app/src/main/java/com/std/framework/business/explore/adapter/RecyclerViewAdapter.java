package com.std.framework.business.explore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.std.framework.R;
import com.std.framework.comm.clazz.RecyclerHolder;

/**
 * Description :
 * Author:       lx
 * Create on:  2016/9/6
 * Modify by：lx
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerHolder> {
    private Context context;
    private String[] data;

    public RecyclerViewAdapter(String[] data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerHolder(LayoutInflater.from(context).inflate(R.layout.item_view_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        TextView textView = holder.getView(R.id.tv_name);
        textView.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.length;
    }


}
