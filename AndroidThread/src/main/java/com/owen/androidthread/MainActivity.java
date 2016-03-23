package com.owen.androidthread;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    Button mButtonFiexd;
    Button mButtonCached;
    Button mButtonScheduled;

    Button mButtonSingle;
    TextView mTextView;
    StringBuilder mStringBuilder = new StringBuilder();

    Runnable command = new Runnable() {
        @Override
        public void run() {
            SystemClock.sleep(1000);
            mHandler.sendEmptyMessage(101);
        }
    };

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 101) {
                mStringBuilder.append(df.format(new Date()) + "\n");
                mTextView.setText(mStringBuilder.toString());
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textview_result);
        mTextView.setText("CPU_COUNT:" + CPU_COUNT);

        CPU_COUNT = CPU_COUNT < 2 ? 4 : CPU_COUNT;

        mButtonFiexd = (Button) findViewById(R.id.button1);
        mButtonCached = (Button) findViewById(R.id.button2);
        mButtonScheduled = (Button) findViewById(R.id.button3);
        mButtonSingle = (Button) findViewById(R.id.button4);
        mButtonFiexd.setOnClickListener(this);
        mButtonCached.setOnClickListener(this);
        mButtonScheduled.setOnClickListener(this);
        mButtonSingle.setOnClickListener(this);

        mFixedThreadPool = Executors.newFixedThreadPool(CPU_COUNT);
        mCachedThreadPool = Executors.newCachedThreadPool();
        mScheduledExecutor = Executors.newScheduledThreadPool(CPU_COUNT);
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    ExecutorService mFixedThreadPool;
    ExecutorService mCachedThreadPool;
    ScheduledExecutorService mScheduledExecutor;
    ExecutorService mSingleThreadExecutor;

    @Override
    public void onClick(View v) {
        mStringBuilder.delete(0, mStringBuilder.length());
        mStringBuilder.append("CPU_COUNT:" + CPU_COUNT + "\n");

        switch (v.getId()) {
            case R.id.button1:
                for (int i = 0; i < CPU_COUNT + 2; i++) {
                    mFixedThreadPool.execute(command);
                }
                break;
            case R.id.button2:
                for (int i = 0; i < CPU_COUNT; i++) {
                    mCachedThreadPool.execute(command);
                }
                break;
            case R.id.button3:
                //延迟2秒后运行
                mScheduledExecutor.schedule(command, 2, TimeUnit.SECONDS);
                //延迟1秒，每隔2秒运行一次
                mScheduledExecutor.scheduleAtFixedRate(command, 1, 2, TimeUnit.SECONDS);
                break;
            case R.id.button4:
                for (int i = 0; i < CPU_COUNT; i++) {
                    mSingleThreadExecutor.execute(command);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFixedThreadPool.shutdownNow();
        mCachedThreadPool.shutdownNow();
        mScheduledExecutor.shutdownNow();
        mSingleThreadExecutor.shutdownNow();
    }
}
