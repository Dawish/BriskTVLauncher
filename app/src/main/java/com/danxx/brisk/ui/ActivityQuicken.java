package com.danxx.brisk.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;

import com.danxx.brisk.R;
import com.danxx.brisk.model.TaskInfo;
import com.danxx.brisk.model.TaskInfoProvider;
import com.danxx.brisk.utils.Tools;
import com.danxx.library.widget.WaveLoadingView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 加速
 */
public class ActivityQuicken extends AppCompatActivity {
    private static final String TAG = "ActivityQuicken";
    private WaveLoadingView waveLoadingView;
    private Button cleanBtn;
    private static final int MSG_CLEAN = -1;

    protected static final int LOAD_FINISH = 0;
    /*加速完成*/
    public final int CLEAR_FINISH = 1;
    /*不需要加速*/
    public final int CLEAR_NEEDENT = 2;
    /*正在运行的进程*/
    private List<ActivityManager.RunningAppProcessInfo> appProcessInfo;
    private ActivityManager activityManager;
    /*进程信息*/
    private List<TaskInfo> UserTaskInfo;
    /*内存信息*/
    private ActivityManager.MemoryInfo info;
    /*加速前设备剩余内存*/
    private static float MemorySurPlus;
    /*设备总共内存*/
    private static float TotalMemory;
    /*清理了多大的内存*/
    private String clearmemory;
    /*实时内存重用百分比*/
    private int currentMemoryPercent;
    /**/
    private String percentnum;
    private HandlerThread handlerThread;
    private HandlerThreadHandler handlerThreadHandler;
    /*是否停止线程*/
    private boolean stopThread=false;

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MSG_CLEAN:
                    Log.d("danxx", "MSG_CLEAN");
                    waveLoadingView.setProgressValue(0);
                    waveLoadingView.setTopTitle("");
                    handlerThreadHandler.postDelayed(new QuickenTask(), 1000);
                    break;
                case CLEAR_FINISH:
                    Log.d("danxx" ,"CLEAR_FINISH-->"+currentMemoryPercent);
                    waveLoadingView.setProgressValue(currentMemoryPercent);
                    Tools.showLongToast(ActivityQuicken.this, "为您腾出 " + floatStrToInt(clearmemory) + "M 空间");
                    waveLoadingView.setTopTitle("内存占用:" + currentMemoryPercent + "%");
                    break;
                case CLEAR_NEEDENT:
                    Log.d("danxx" ,"CLEAR_NEEDENT-->"+currentMemoryPercent);
                    waveLoadingView.setProgressValue(currentMemoryPercent);
                    waveLoadingView.setTopTitle("内存占用:"+currentMemoryPercent+"%");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initData();
        initView();
    }

    /**
     * 在显示UI前获取运行的进程和设备的内存情况
     */
    private void initData() {
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        GetSurplusMemory();
        TotalMemory = GetTotalMemory();
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        handlerThreadHandler = new HandlerThreadHandler(handlerThread.getLooper());
    }

    private void initView() {
        waveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        float temp = MemorySurPlus/TotalMemory;
        currentMemoryPercent = (int)(temp*100);
        waveLoadingView.setProgressValue(currentMemoryPercent);
        waveLoadingView.setTopTitle("内存占用:" + (int)(temp*100)+"%");
        cleanBtn = (Button) findViewById(R.id.cleanBtn);
    }

    /**
     * 为加速线程自定义handler
     */
    class HandlerThreadHandler extends Handler{
        public HandlerThreadHandler(){
        }

        public HandlerThreadHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
            super.handleMessage(msg);
        }
    }


    /*
     * 通过handlerThread的handler的post方法可以把QuickenTask放在handlerThread中执行
     */
    class QuickenTask implements Runnable{
        public QuickenTask() {
            Log.d("danxx" ,"添加任务");
        }
        Handler handler;
        /**
         * 传入UI线程的handler
         * @param handler
         */
        public QuickenTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Log.d("danxx", "开始任务");
            if(stopThread){

            }else{
                getRunningApp();
                TaskInfoProvider taskInfoProvider = new TaskInfoProvider(ActivityQuicken.this);
                UserTaskInfo = taskInfoProvider.GetAllTask(appProcessInfo);
                KillTask();
            }
        }
    }

    public void setProgressValue(float value){
        waveLoadingView.setProgressValue((int)value);
    }

    // 得到当前运行的进程数目
    public List<ActivityManager.RunningAppProcessInfo> getRunningApp() {
        appProcessInfo = activityManager.getRunningAppProcesses();
        return appProcessInfo;

    }

    // 得到清理前剩余的内存
    public long GetSurplusMemory() {
        info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        long MemorySize = info.availMem;
        MemorySurPlus = (float) MemorySize / 1024 / 1024;
        return MemorySize;
    }

    public float GetTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader fileReader = new FileReader(str1);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            str2 = bufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (float) (initial_memory / 1024);
    }

    /**
     * 清理进程
     */
    private void KillTask() {
        for (TaskInfo info : UserTaskInfo) {
            if (!info.getIsSystemProcess()) {
                Log.d("danxx" ,"info-->"+info.getPackageName());
                activityManager.killBackgroundProcesses(info.getPackageName());
                // 高级清理
                // try {
                // Method method =
                // Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",
                // String.class);
                // method.invoke(activityManager, info.getPackageName());
                // } catch (Exception e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
            }
        }
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        float MemorySize = (float) info.availMem / 1024 / 1024;
        float size = MemorySize - MemorySurPlus;
        if (size > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            clearmemory = decimalFormat.format(size);
            percentnum = decimalFormat.format((size / TotalMemory) * 100);
            float temp = Float.parseFloat(clearmemory);
            currentMemoryPercent = floatToInt((MemorySurPlus - temp)/TotalMemory*100);
            Message message = mHandler.obtainMessage(CLEAR_FINISH);
            mHandler.sendMessage(message);
        } else {
            Message message = mHandler.obtainMessage(CLEAR_NEEDENT);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            mHandler.removeMessages(MSG_CLEAN);
            mHandler.sendEmptyMessageDelayed(MSG_CLEAN, 500);
        }
        return super.dispatchKeyEvent(event);
    }

    private int floatToInt(float value){
        return (int)value;
    }

    private int floatStrToInt(String value){
        float temp = Float.parseFloat(value);
        return floatToInt(temp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread=true;
        mHandler.removeCallbacksAndMessages(null);
        handlerThreadHandler.removeCallbacksAndMessages(null);

    }
}
