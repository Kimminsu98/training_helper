package org.techtown.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class time_end extends AppCompatActivity {
    static int counter;
    Button backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_end);
        backbtn = findViewById(R.id.backbtn);

        findViewById(R.id.backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Handler handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                // 원래 하려던 동작 (UI변경 작업 등)\\
                backbtn.setEnabled(true);
                //MainActivity.adapter.notifyDataSetChanged();
            }
        };

        counter=0;
        backbtn.setEnabled(false);
        Timer timer=new Timer();
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                Log.e("카운터",String.valueOf(counter));
                counter++;
                if(counter>5){
                    timer.cancel();
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
        };
        timer.schedule(tt,0,1000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       switch (keyCode){
           case KeyEvent.KEYCODE_BACK: return true;
       }
       return super.onKeyDown(keyCode, event);
    }

}