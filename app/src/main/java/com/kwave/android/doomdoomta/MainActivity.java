package com.kwave.android.doomdoomta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final LaunchPad pad = (LaunchPad) findViewById(R.id.launchpad);
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10000);
//                    pad.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            pad.playPlayList();
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }
}