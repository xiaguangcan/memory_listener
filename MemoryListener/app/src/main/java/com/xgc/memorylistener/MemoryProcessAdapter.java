package com.xgc.memorylistener;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgc.memorylistener.module.ProcessInfo;

import java.util.List;

/**
 * Created by xgc on 16/7/1.
 */
public class MemoryProcessAdapter extends BaseAdapter {
    private Context context;
    private List<ProcessInfo> plist;


    public MemoryProcessAdapter(Context context, List<ProcessInfo> list) {
        this.context = context;
        this.plist = list;
    }

    @Override
    public int getCount() {
        return plist.size();
    }

    @Override
    public ProcessInfo getItem(int position) {
        return plist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder = null;
        if (convertView == null) {
            vHolder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.item_processinfo, null);
            vHolder.iv_icon = (ImageView) convertView
                    .findViewById(R.id.iv_icon);
            vHolder.tv_memory = (TextView) convertView
                    .findViewById(R.id.tv_memory);
            vHolder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        ProcessInfo pInfo = getItem(position);
        vHolder.tv_name.setText(pInfo.name);
        vHolder.iv_icon.setImageDrawable(pInfo.icon);
        vHolder.tv_memory.setText(Formatter.formatFileSize(
                context, pInfo.memory));
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_memory;
        public TextView tv_name;
    }
}
