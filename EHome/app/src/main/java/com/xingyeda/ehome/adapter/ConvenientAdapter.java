package com.xingyeda.ehome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.ConvenientBean;

import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;

public class ConvenientAdapter extends BaseAdapter {
    private List<ConvenientBean> mList;
    private LayoutInflater mInflater;


    public ConvenientAdapter(Context context, List<ConvenientBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_convenient,
                    parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ConvenientBean bean = mList.get(position);

        viewHolder.mDescription.setText(bean.getmDescription());
        viewHolder.mPhoneNumber.setText(bean.getmPhoneNumber());

        return convertView;
    }


    static class ViewHolder {
       @Bind(R.id.description)
        TextView mDescription;
       @Bind(R.id.phone_number)
        TextView mPhoneNumber;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }



}
