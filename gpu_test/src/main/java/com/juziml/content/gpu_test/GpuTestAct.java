package com.juziml.content.gpu_test;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.juziml.content.R;

/**
 * create by zhusw on 2020-07-29 10:55
 */
public class GpuTestAct extends AppCompatActivity {

    GpuTestCurlAnimView gpuTestCurlAnimView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gputest);
        gpuTestCurlAnimView = findViewById(R.id.curlView);

        gpuTestCurlAnimView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gpuTestCurlAnimView.flipPrepare(event.getRawX(), event.getRawY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        gpuTestCurlAnimView.flipCurl(event.getRawX(), event.getRawY());
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        gpuTestCurlAnimView.flipSetToDefault();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


}
