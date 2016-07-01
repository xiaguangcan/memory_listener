package com.xgc.memorylistener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.xgc.memorylistener.module.ProcessInfo;
import com.xgc.memorylistener.util.ProcessInfoProvider;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by xgc on 16/7/1.
 */
public class MemoryListener extends TimerTask {
    private Context context;
    private Handler handler ;

    public MemoryListener(Context context,Handler handler){
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
      List<ProcessInfo> tempList = ProcessInfoProvider
                .getProcessInfo(context);
        Message message = Message.obtain(handler);
        message.obj = tempList;
        handler.sendMessage(message);

    }
}
