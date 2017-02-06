package com.cxf.shakedemo.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.cxf.shakedemo.R;
import com.cxf.shakedemo.util.ShakeListener;
import com.cxf.shakedemo.util.ShakeUtil;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ShakeListener {

    private static final String TAG = "MainActivity";

    private RelativeLayout mTopView;
    private View mTopLine;
    private RelativeLayout mBottomView;
    private View mBottomLine;

    private Animation mTopOpenAnim;
    private Animation mBottomOPenAnim;
    private Animation mTopCloseAnim;
    private Animation mBottomCloseAnim;
    private Handler mHandler;
    private Runnable mCloseAnim = new Runnable() {
        @Override
        public void run() {
            mTopView.startAnimation(mTopCloseAnim);
            mBottomView.startAnimation(mBottomCloseAnim);
        }
    };
    private boolean isShaking;//正在执行摇一摇
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ShakeUtil.getInstance(this).setOnShakeListener(this);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mTopView = (RelativeLayout) findViewById(R.id.top);
        mTopLine = findViewById(R.id.top_line);
        mBottomView = (RelativeLayout) findViewById(R.id.bottom);
        mBottomLine = findViewById(R.id.bottom_line);
        mHandler = new Handler();

        mTopOpenAnim = AnimationUtils.loadAnimation(this, R.anim.top_open);
        mTopOpenAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTopLine.setVisibility(View.VISIBLE);
                mBottomLine.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(mCloseAnim, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomOPenAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_open);
        mTopCloseAnim = AnimationUtils.loadAnimation(this, R.anim.top_close);
        mTopCloseAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTopLine.setVisibility(View.GONE);
                mBottomLine.setVisibility(View.GONE);
                isShaking = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomCloseAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_close);
        mMediaPlayer=new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.shake));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeUtil.getInstance(this).registerShakeListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ShakeUtil.getInstance(this).unRegisterShakeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTopView = null;
        mTopLine = null;
        mBottomView = null;
        mBottomLine = null;
        mTopOpenAnim = null;
        mBottomOPenAnim = null;
        mTopCloseAnim = null;
        mBottomCloseAnim = null;
        mHandler = null;
        mCloseAnim = null;
        mVibrator = null;
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer=null;
    }

    @Override
    public void onShake() {
        if (isShaking) {
            return;
        }
        isShaking = true;
        mVibrator.vibrate(300);
        mMediaPlayer.start();
        mTopView.startAnimation(mTopOpenAnim);
        mBottomView.startAnimation(mBottomOPenAnim);
    }
}
