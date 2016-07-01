package com.xgc.memorylistener.floatwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xgc.memorylistener.MemoryListener;
import com.xgc.memorylistener.MemoryProcessAdapter;
import com.xgc.memorylistener.R;
import com.xgc.memorylistener.module.ProcessInfo;
import com.xgc.memorylistener.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Timer;


public class FloatWindowBigView extends LinearLayout {

	// 记录大悬浮窗的宽
	public int viewWidth;
	// 记录大悬浮窗的高
	public int viewHeight;

	public WindowManager.LayoutParams bigWindowParams;

	private Context context;

	private ArrayList<ProcessInfo> plist = new ArrayList<>();
	private ArrayList<ProcessInfo> tempList =new ArrayList<>();
	private MemoryProcessAdapter mAdapter;
	private Timer timer;
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
				mAdapter = new MemoryProcessAdapter(context,plist);
				lv_memory.setAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	private ListView lv_memory;

	public FloatWindowBigView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);

		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;

		bigWindowParams = new WindowManager.LayoutParams();
		// 设置显示的位置，默认的是屏幕中心
		bigWindowParams.x = ScreenUtils.getScreenWidth(context) / 2 - viewWidth
				/ 2;
		bigWindowParams.y = ScreenUtils.getScreenHeight(context) / 2
				- viewHeight / 2;
		bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		bigWindowParams.format = PixelFormat.RGBA_8888;

		// 设置交互模式
		bigWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		bigWindowParams.width = ScreenUtils.getScreenWidth(context);
		bigWindowParams.height = viewHeight;
		view.setBackgroundColor(Color.parseColor("#88000000"));

		initView();
		initData();

	}

	private void initData() {
		initTask();
	}

	private void initTask() {
		if (timer == null) {
			timer = new Timer();
			// 每5000毫秒就执行一次刷新任务
			timer.scheduleAtFixedRate(new MemoryListener(context,handler), 0, 5000);
		}
	}

	private void initView() {
		Button tv_back = (Button) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				timer.cancel();
				timer=null;
				FloatWindowManager.getInstance(context).removeBigWindow();
			}
		});

		lv_memory = (ListView) findViewById(R.id.lv_memory);
	}

}
