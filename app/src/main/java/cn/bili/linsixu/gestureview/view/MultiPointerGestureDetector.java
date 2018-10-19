package cn.bili.linsixu.gestureview.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 处理双指点击
 * Created by Magic
 * on 2018/10/16.
 */
public class MultiPointerGestureDetector {
        private static final String TAG = "MultiDetector";

        private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;
        private static final int DOUBLE_TAP_MIN_TIME = 50;
        private static final int DOUBLE_TAP_SLOP = 100;
        private static final boolean ENABLE_SLOP_CHECK = false;

        private static final int MSG_TIMEOUT = 1;

        private MotionEvent mCurrentDownEvent;
        private MotionEvent mPreviousDownEvent;
        private MotionEvent mPreviousUpEvent;

        private Context mContext;
        private GestureHandler mHandler;
        private OnGestureListener mGestureListener;

        private int mDoubleTapSlopSquare;
        private int mDoubleClickCount;//双指点击事件（不满足双指是不会+1的）

        private class GestureHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TIMEOUT:
                        Log.d(TAG, "handle time out msg");
                        reset();
                        break;
                }
            }
        }

        public MultiPointerGestureDetector(Context context, OnGestureListener gestureListener) {
            mContext = context;
            mGestureListener = gestureListener;
            init(context);
        }

        private void init(Context context) {
            int doubleTapSlop;
            if (context == null) {
                doubleTapSlop = DOUBLE_TAP_SLOP;
            } else {
                final ViewConfiguration configuration = ViewConfiguration.get(context);
                doubleTapSlop = configuration.getScaledDoubleTapSlop();
            }
            mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
            mHandler = new GestureHandler();
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeMessages(MSG_TIMEOUT);
                    if (mPreviousDownEvent == null) {
                        mPreviousDownEvent = MotionEvent.obtain(event);
                        Log.d(TAG, "pre down event instance");
                    } else {
                        mCurrentDownEvent = MotionEvent.obtain(event);
                        Log.d(TAG, "curr down event instance");
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    boolean isTwoPointer = event.getPointerCount() == 2;
                    if (!isTwoPointer) {
                        reset();
                        Log.d(TAG, "reset when not two finger");
                    } else {
                        mDoubleClickCount++;
                        Log.d(TAG, String.format("click count (%d)", mDoubleClickCount));
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (mPreviousUpEvent == null && mPreviousDownEvent != null) {
                        mPreviousUpEvent = MotionEvent.obtain(event);

                        Log.d(TAG, "action up when double click");
                        mHandler.removeMessages(MSG_TIMEOUT);
                        mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, DOUBLE_TAP_TIMEOUT + 50);
                    } else if (isConsideredDoubleTap()) {
                        if (mGestureListener != null) {
                            mGestureListener.onDoubleTip(event);
                        }
                        reset();
                        return true;
                    } else {
                        reset();
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    mHandler.removeMessages(MSG_TIMEOUT);
                    reset();
                    break;
            }
            return false;
        }

        private void reset() {
            mPreviousDownEvent = null;
            mPreviousUpEvent = null;
            mCurrentDownEvent = null;
            mDoubleClickCount = 0;
            mHandler.removeMessages(MSG_TIMEOUT);
        }

        private boolean isConsideredDoubleTap() {

            if (mPreviousDownEvent == null || mPreviousUpEvent == null || mCurrentDownEvent == null || mDoubleClickCount != 2) {
                return false;
            }

            final long deltaTime = mCurrentDownEvent.getEventTime() - mPreviousUpEvent.getEventTime();
            if (deltaTime > DOUBLE_TAP_TIMEOUT || deltaTime < DOUBLE_TAP_MIN_TIME) {
                return false;
            }

            if (!ENABLE_SLOP_CHECK) {
                return true;
            }

            int deltaX = (int) mPreviousDownEvent.getX() - (int) mCurrentDownEvent.getX();//获取两点Event的X轴差距
            int deltaY = (int) mPreviousDownEvent.getY() - (int) mCurrentDownEvent.getY();//获取两点Event的Y轴差距
            return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);//勾股定理，已知两边求第三的长度（两点间距）
        }

        public interface OnGestureListener {
            void onDoubleTip(MotionEvent ev);
        }
}
