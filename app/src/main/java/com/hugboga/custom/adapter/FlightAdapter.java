package com.hugboga.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.FlightBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 航班列表适配器
 */
public class FlightAdapter extends BaseAdapter {
    public List<FlightBean> mList = new ArrayList<FlightBean>();
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public FlightAdapter(Activity activity) {
        mInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addList(List<FlightBean> list) {
        this.mList.addAll(list);
    }

    public void setList(List<FlightBean> list) {
        if (list != null) {
            this.mList = list;
        } else {
            this.mList.clear();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_flight, null, false);
            holder.title = (TextView) convertView.findViewById(R.id.item_flight_title);
            holder.fromtime = (TextView) convertView.findViewById(R.id.from_city_time);
            holder.fromaddress = (TextView) convertView.findViewById(R.id.from_city);
            holder.endtime = (TextView) convertView.findViewById(R.id.end_city_time);
            holder.endaddress = (TextView) convertView.findViewById(R.id.end_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FlightBean bean = mList.get(position);

        holder.title.setText(bean.company + "  " + bean.flightNo);
        holder.fromtime.setText(bean.depTime);
        holder.endtime.setText(bean.arrivalTime);
        holder.fromaddress.setText(bean.depAirportName);
        holder.endaddress.setText(bean.arrAirportName);
        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView fromtime;
        public TextView fromaddress;
        public TextView endtime;
        public TextView endaddress;

    }
}
