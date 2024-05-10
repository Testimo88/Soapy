package com.android.soapy.receiver;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import com.android.soapy.MainActivity;

import java.util.List;


public class BootCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_AVM_SYSTEMUI = "android.intent.action.BOOT_COMPLETED";
    public static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (ACTION_AVM_SYSTEMUI.equalsIgnoreCase(action)) {
                Log.d(TAG, "开机启动了接收到了:" + context.getPackageName());
//                goAnyApp(context, "com.android.soapy");
                initBootSet(context);
            }
        }
    }

    private void initBootSet(Context context) {
        Intent intent = new Intent("com.lztek.tools.action.BOOT_SETUP");
        intent.putExtra("packageName", "com.android.soapy");
        //intent.putExtra("delaySeconds", 5); // 开机启动完成5秒后运行指定apk
        intent.setPackage("com.lztek.bootmaster.autoboot7"); // Android 8.0 or above
        context.sendBroadcast(intent);
    }

    private void goAnyApp(Context context, String pkg) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(pkg);
            ComponentName component = intent.getComponent();
            if (component == null) {
                Log.d(TAG, "startRemoteActivity component == null");
                return;
            }
            Intent startInent = new Intent();
            Log.d(TAG, "packageName: " + component.getPackageName()
                    + "getClassName: " + component.getClassName());
            startInent.setComponent(new ComponentName(component.getPackageName(), component.getClassName()));
            startInent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startInent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private boolean getCurrentTask(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
//        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {
//            if (process.baseActivity.getPackageName().equals(context.getPackageName())
//                    || process.topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }


}
