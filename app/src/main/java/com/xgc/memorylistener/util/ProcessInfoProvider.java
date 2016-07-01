package com.xgc.memorylistener.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.xgc.memorylistener.R;
import com.xgc.memorylistener.module.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessInfoProvider {

    public static int getProcessCount(Context ctx) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    public static long getCurrentMem(Context ctx) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    public static long getTotalMem(Context ctx) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/meminfo")));
            char[] cs = br.readLine().toCharArray();
            StringBuffer data = new StringBuffer();
            for (char c : cs) {
                if (Character.isDigit(c)) {
                    data.append(c);
                }
            }
            return Long.parseLong(data.toString()) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<ProcessInfo> getProcessInfo(Context ctx) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();

        ArrayList<ProcessInfo> plist = new ArrayList<ProcessInfo>();

        for (RunningAppProcessInfo runningProcess : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            String packageName = runningProcess.processName;
            processInfo.packageName = packageName;

            int pid = runningProcess.pid;
            android.os.Debug.MemoryInfo[] memoryInfo = am
                    .getProcessMemoryInfo(new int[]{pid});
            long memory = memoryInfo[0].getTotalPrivateDirty() * 1024;
            processInfo.memory = memory;

            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = appInfo.loadIcon(pm);
                String label = appInfo.loadLabel(pm).toString();

                processInfo.icon = icon;
                processInfo.name = label;

                int flags = appInfo.flags;

                if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isUseApp = false;
                } else {
                    processInfo.isUseApp = true;
                }
            } catch (NameNotFoundException e) {
                processInfo.name = packageName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.isUseApp = false;
                e.printStackTrace();
            }
            if (processInfo.isUseApp) {
                plist.add(processInfo);
            }
        }
        return plist;
    }

    public static void killBackgroundProcess(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningProcess : appProcesses) {
            String packageName = runningProcess.processName;
            if (packageName.equals(ctx.getPackageName())) {
                continue;
            }
            am.killBackgroundProcesses(packageName);
        }
    }
}
