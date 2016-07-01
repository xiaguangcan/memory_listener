package com.xgc.memorylistener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.xgc.memorylistener.floatwindow.FloatWindowManager;
import com.xgc.memorylistener.floatwindow.FloatWindowSmallView;
import com.xgc.memorylistener.module.ProcessInfo;

import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ProcessInfo> plist = new ArrayList<>();
    private ArrayList<ProcessInfo> tempList = new ArrayList<>();
    private ListView lv_memory;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tempList = (ArrayList<ProcessInfo>) msg.obj;

            if(plist !=null) {
                plist.clear();
                plist.addAll(tempList);
            }
            if(mAdapter == null) {
                pb_progressBar.setVisibility(View.GONE);
                mAdapter = new MemoryProcessAdapter(getApplicationContext(),plist);
                lv_memory.setAdapter(mAdapter);
            }else{
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private ProgressBar pb_progressBar;
    private MemoryProcessAdapter mAdapter;
    private Timer timer;
    private FloatWindowManager floatWindowManager;
    // 小窗口布局资源id
    private int layoutResId;
    // 布局根布局id
    private int rootLayoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initTask();
    }

    private void initTask() {
        if (timer == null) {
            timer = new Timer();
            // 每5000毫秒就执行一次刷新任务
            timer.scheduleAtFixedRate(new MemoryListener(getApplicationContext(),handler), 0, 5000);
        }
    }

    private void init() {
        lv_memory = (ListView) findViewById(R.id.lv_memory);
        pb_progressBar = (ProgressBar) findViewById(R.id.pb_progressBar);

        floatWindowManager = FloatWindowManager.getInstance(getApplicationContext());

        layoutResId = R.layout.float_window_small;
        rootLayoutId = R.id.small_window_layout;

        floatWindowManager.createSmallWindow(getApplicationContext(),
                layoutResId, rootLayoutId);
        floatWindowManager
                .setOnClickListener(new FloatWindowSmallView.OnClickListener() {

                    @Override
                    public void click() {
                        FloatWindowManager.getInstance(getApplicationContext()).createBigWindow(getApplicationContext());

                    }
                });
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timer = null;
        super.onDestroy();
    }
}
