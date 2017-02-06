package com.cxf.shakedemo.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by cxf on 2017/2/6.
 */
public class ShakeUtil implements SensorEventListener {

    private static final String TAG = "ShakeUtil";

    private static ShakeUtil sUtil;
    private ShakeListener mShakeListener;
    private SensorManager mSensorManager;
    /**
     * 检测的时间间隔
     */
    static final int UPDATE_INTERVAL = 100;
    /**
     * 上一次检测的时间
     */
    long mLastUpdateTime;
    /**
     * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
     */
    float mLastX, mLastY, mLastZ;

    /**
     * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
     */
    public int shakeThreshold = 600;


    private ShakeUtil(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static ShakeUtil getInstance(Context context) {
        if (sUtil == null) {
            synchronized (ShakeUtil.class) {
                if (sUtil == null) {
                    sUtil = new ShakeUtil(context);
                }
            }
        }
        return sUtil;
    }

    public void registerShakeListener() {
        Log.e(TAG, "registerShakeListener: 注册传感器" );
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterShakeListener() {
        Log.e(TAG, "registerShakeListener: 取消传感器" );
        mSensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(ShakeListener shakeListener) {
        mShakeListener = shakeListener;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL) {
            return;
        }
        mLastUpdateTime = currentTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
        // 当加速度的差值大于指定的阈值，认为这是一个摇晃
        if (delta > shakeThreshold) {
            if (mShakeListener != null) {
                mShakeListener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
