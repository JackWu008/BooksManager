package net.lzzy.booksmanager.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import net.lzzy.booksmanager.R;

import org.androidannotations.annotations.EActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 007 on 2016/6/7.
 * 启动时显示图片的activity
 */
@EActivity(R.layout.activity_start)
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//让图片占满屏幕
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                CategoryActivity_.intent(StartActivity.this).start();
//                finish();
//            }
//        };
//        timer.schedule(task, 2000);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                CategoryActivity_.intent(StartActivity.this).start();
               finish();
            }
        }, 2000);// 持续时间2s
    }
}
