package cn.bili.linsixu.gestureview.activity;

import android.view.MotionEvent;
import android.view.View;

import cn.bili.linsixu.gestureview.R;
import cn.bili.linsixu.gestureview.databinding.ActivityMainBinding;
import cn.bili.linsixu.gestureview.utils.MyLog;
import cn.bili.linsixu.gestureview.view.GestureView;

public class MainActivity extends BaseActivity<ActivityMainBinding>{
    private final String TAG = "MainActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initDate() {
        t.gestureView.setGestureEnabled(true);
        t.gestureView.setHorizontalGestureEnabled(true);
    }

    @Override
    public void initClick() {
        t.gestureView.setTouchGestureListener(new GestureView.OnTouchGestureListener() {
            @Override
            public void onScrollStart(int action) {
                MyLog.i(TAG,"onScrollStart");
            }

            @Override
            public void onScroll(int action, float progress, int pointerCount, boolean inUnSeekRegion) {
                MyLog.i(TAG,"onScroll");
            }

            @Override
            public void onScrollStop(int action, float progress, boolean inUnSeekRegion) {
                MyLog.i(TAG,"onScrollStop");
            }

            @Override
            public boolean onSingleTapConfirmed() {
                MyLog.i(TAG,"onSingleTapConfirmed");
                return false;
            }

            @Override
            public boolean onDoubleTap() {
                MyLog.i(TAG,"onDoubleTap");
                return false;
            }

            @Override
            public void onTwoFingerDoubleTap() {
                MyLog.i(TAG,"onTwoFingerDoubleTap");
            }

            @Override
            public void onTouch(MotionEvent ev) {
                MyLog.i(TAG,"onTouch");
            }

            @Override
            public void onLongPress(MotionEvent ev) {
                MyLog.i(TAG,"onLongPress");
            }
        });

    }

}
