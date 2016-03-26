package com.huangbaoche.hbcframe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.huangbaoche.hbcframe.viewholder.ZBaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class ZBaseAdapter<T, V> extends RecyclerView.Adapter<ZBaseViewHolder> {

    protected Context context;
    protected OnItemClickListener onItemClickListener;
    protected List<T> datas;
    protected int dataCount;

    public ZBaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ZBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, initResource(), null);
        return getVH(view);
    }

    @Override
    public void onBindViewHolder(final ZBaseViewHolder holder, final int position) {
        //设置点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        getView(position, (V) holder);
    }

    protected abstract int initResource();

    protected abstract ZBaseViewHolder getVH(View view);

    protected abstract void getView(int position, V vh);

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void addDatas(List<T> data) {
        if (datas == null) {
            datas = new ArrayList<T>();
        }
        datas.addAll(data);
        notifyDataSetChanged();
    }

    /*
    有些要求需要在刷新第一页数据的时候清空列表或者adapter数据
    这时候，只需要在请求网络的第一页数据时候，调用此方法，即可清空数据
     */
    public void removeAll() {
        if (datas != null) {
            datas.removeAll(datas);
        }
        notifyDataSetChanged();
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getDataCount() {
        return dataCount;
    }
}
