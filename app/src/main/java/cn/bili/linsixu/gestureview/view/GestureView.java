package cn.bili.linsixu.gestureview.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Magic
 * on 2018/10/16.
 */
public class GestureView extends View {
    private static final String TAG = "GestureView";

    private CustomGestureDetector mGestureDetector;
    private MultiPointerGestureDetector mMultiPointerGestureDetector;

    private boolean mEnableGesture = true;
    private boolean mEnableHorizontalGesture = true;
    private OnTouchGestureListener mOnTouchGestureListener;

    public GestureView(Context context) {
        super(context);
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Gesture是否开启
    public void setGestureEnabled(boolean enabled) {
        mEnableGesture = enabled;
        if (mGestureDetector != null) {
            mGestureDetector.setGestureEnabled(enabled);
        }
    }

    //水平手势是否启用
    public void setHorizontalGestureEnabled(boolean enabled) {
        mEnableHorizontalGesture = enabled;
        if (mGestureDetector != null) {
            mGestureDetector.setHorizontalGestureEnabled(enabled);
        }
    }

    public void setTouchGestureListener(OnTouchGestureListener listener) {
        mOnTouchGestureListener = listener;
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0) {
            mGestureDetector = new CustomGestureDetector(getContext().getApplicationContext(), new CustomGestureDetector.PlayerGestureListener(width, height), listener);
            mGestureDetector.setGestureEnabled(mEnableGesture);
            mGestureDetector.setHorizontalGestureEnabled(mEnableHorizontalGesture);
            mMultiPointerGestureDetector = new MultiPointerGestureDetector(getContext(), mMutilListener);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mGestureDetector = new CustomGestureDetector(getContext().getApplicationContext(), new CustomGestureDetector.PlayerGestureListener(getWidth(), getHeight()), mOnTouchGestureListener);
            mGestureDetector.setGestureEnabled(mEnableGesture);
            mGestureDetector.setHorizontalGestureEnabled(mEnableHorizontalGesture);
            mMultiPointerGestureDetector = new MultiPointerGestureDetector(getContext(), mMutilListener);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (mGestureDetector != null) {
            //因为View的onTouchEvent无法处理各种手势操作，必须把Event传递到GestureDetector进行处理，再通过GestureDetector内部接口回调来进行复杂手势对应的操作
            handled = mGestureDetector.onTouchEvent(event);
        }
        handled = mMultiPointerGestureDetector.onTouchEvent(event) || handled;
        return handled || super.onTouchEvent(event);
    }

    private MultiPointerGestureDetector.OnGestureListener mMutilListener = new MultiPointerGestureDetector.OnGestureListener() {
        @Override
        public void onDoubleTip(MotionEvent ev) {
            Log.d(TAG, "Double click with two fingers");
            if (mOnTouchGestureListener != null) {
                mOnTouchGestureListener.onTwoFingerDoubleTap();
            }
        }
    };


    public static class CustomGestureDetector extends GestureDetector{
        private final CustomGestureDetector.PlayerGestureListener mGestureListener;
        private boolean mEnableGesture = true;


        public CustomGestureDetector(Context context, CustomGestureDetector.PlayerGestureListener listener, OnTouchGestureListener touchListener) {
            super(context, listener);//将PlayerGestureListener设置给GestureDetector来进行OnGestureListener接口的回调
            mGestureListener = listener;
            listener.setTouchGestureListener(touchListener);
        }

        public void setGestureEnabled(boolean enabled) {
            mEnableGesture = enabled;
        }

        public void setHorizontalGestureEnabled(boolean enabled) {
            if (mGestureListener != null) {
                mGestureListener.setHorizontalGestureEnabled(enabled);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (mGestureListener != null) {
                mGestureListener.onTouch(ev);
            }
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (mGestureListener != null && mGestureListener.onUp(ev)) {
                    return true;
                }
            }
            if (ev.getAction() == MotionEvent.ACTION_MOVE && !mEnableGesture) {
                return false;
            }
            return super.onTouchEvent(ev);
        }

        public static class PlayerGestureListener extends SimpleOnGestureListener{
            private final int mGestureWidth;
            private final int mGestureHeight;
            private boolean mInVerticalMoving;
            private boolean mInHorizontalMoving;
            private boolean mInGestureSeekingMode;
            private static final float MIN_SEEKABLE_PROGRESS_FACTOR = 0.02f;
            private OnTouchGestureListener mOnTouchGestureListener;
            private float mProgressFactor = 0f;
            private int mCurrentAction = -1;
            private boolean mEnableHorizontalGesture = true;

            public PlayerGestureListener(int width, int height) {
                mGestureWidth = width;
                mGestureHeight = height;
            }


            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onUp(MotionEvent e) {
                // un seek region
                boolean inUnSeekRegion = false;
                float startX = e.getX();
                float startY = e.getY();
                if (startX < mGestureWidth * 0.1f && startY < mGestureHeight * 0.3f) {
                    inUnSeekRegion = true;
                }
                if (startX > mGestureWidth * 0.9f && startY < mGestureHeight * 0.3f) {
                    inUnSeekRegion = true;
                }
                endDragingSeekBar(inUnSeekRegion);
                if (mInHorizontalMoving) {
                    mInHorizontalMoving = false;
                }
                if (mInVerticalMoving) {
                    mInVerticalMoving = false;
                }
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mOnTouchGestureListener != null) {
                    return mOnTouchGestureListener.onSingleTapConfirmed();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mOnTouchGestureListener != null) {
                    return mOnTouchGestureListener.onDoubleTap();
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (handleScroll(e1, e2, distanceX, distanceY)) {
                    return true;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mOnTouchGestureListener != null) {
                    mOnTouchGestureListener.onLongPress(e);
                }
            }

            /**
             * @param e1        scroll起始位置信息,起始位置不一定是上一次位置
             * @param e2        当前的scroll位置信息
             * @param distanceX 相对上一次的水平移动距离
             * @param distanceY 相对上一次的的垂直移动距离
             * @return
             */
            private final boolean handleScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null || e2 == null) {
                    return false;
                }
                // safe area
                float startX = e1.getX();
                if (startX < mGestureWidth * 0.01f || startX > mGestureWidth * 0.95f) {
                    return true;
                }
                float startY = e1.getY();
                if (startY < mGestureHeight * 0.1f || startY > mGestureHeight * 0.95f) {
                    return true;
                }
                boolean consumed = false;
                float moveDelta = Math.abs(distanceY) - Math.abs(distanceX);
                if (moveDelta > 0) {
                    consumed = onVertialMove(e1, e2, distanceX, distanceY);
                } else if (moveDelta < 0) {
                    consumed = onHorizontalMove(e1, e2, distanceX, distanceY);
                }
                if (consumed) {
                    return true;
                }
                return false;
            }

            private final boolean onHorizontalMove(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mInVerticalMoving || !mEnableHorizontalGesture) {
                    return false;
                }
                float delatFactorX = getDeltaFactorX(e1, e2);
                if ((Math.abs(delatFactorX) < MIN_SEEKABLE_PROGRESS_FACTOR && !mInGestureSeekingMode)) {
                    return false;
                }

                int e1PointerCount = e1.getPointerCount();
                int e2PointerCount = e2.getPointerCount();
                int pointerCount = Math.max(e1PointerCount, e2PointerCount);

                if (!mInGestureSeekingMode) {
                    beginDragingSeekBar(delatFactorX);
                }
                // un seek region
                boolean inUnSeekRegion = false;
                float endX = e2.getX();
                float endY = e2.getY();
                if (endX < mGestureWidth * 0.1f && endY < mGestureHeight * 0.3f) {
                    inUnSeekRegion = true;
                }
                if (endX > mGestureWidth * 0.9f && endY < mGestureHeight * 0.3f) {
                    inUnSeekRegion = true;
                }
                dragSeekBar(delatFactorX, pointerCount, inUnSeekRegion);

                if (!mInHorizontalMoving) {
                    mInHorizontalMoving = true;
                }
                return false;
            }

            private void beginDragingSeekBar(float progress) {
                mProgressFactor = progress;
                mInGestureSeekingMode = true;
                onStartTrackingTouch(OnTouchGestureListener.ACTION_HORIZONTAL_SCROLLING);
            }

            private void dragSeekBar(float progress, int pointerCount, boolean inUnSeekRegion) {
                mProgressFactor = progress;
                onProgressChanged(OnTouchGestureListener.ACTION_HORIZONTAL_SCROLLING, progress, true, pointerCount, inUnSeekRegion);
                enterGestureSeekingMode();
            }

            private void endDragingSeekBar(boolean inUnSeekRegion) {
                if (mInHorizontalMoving || mInVerticalMoving) {
                    mInGestureSeekingMode = false;
                    onStopTrackingTouch(mCurrentAction, mProgressFactor, inUnSeekRegion);
                    exitGestureSeekingMode();
                }
            }

            private void enterGestureSeekingMode() {
                mInGestureSeekingMode = true;
            }

            public void exitGestureSeekingMode() {
                mInGestureSeekingMode = false;
            }

            public boolean isInGestureSeekingMode() {
                return mInGestureSeekingMode;
            }

            public void setTouchGestureListener(OnTouchGestureListener onTouchGestureListener) {
                mOnTouchGestureListener = onTouchGestureListener;
            }

            private void onStartTrackingTouch(int action) {
                if (mOnTouchGestureListener != null) {
                    mOnTouchGestureListener.onScrollStart(action);
                }
            }

            private void onProgressChanged(int action, float progress, boolean fromUser, int pointerCount, boolean inUnSeekRegion) {
                mCurrentAction = action;
                if (mOnTouchGestureListener != null) {
                    mOnTouchGestureListener.onScroll(action, progress, pointerCount, inUnSeekRegion);
                }
            }

            private void onStopTrackingTouch(int action, float progress, boolean inUnSeekRegion) {
                if (mOnTouchGestureListener != null) {
                    mOnTouchGestureListener.onScrollStop(action, progress, inUnSeekRegion);
                }
            }

            private final boolean onVertialMove(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mInHorizontalMoving) {
                    return false;
                }

                int e1PointerCount = e1.getPointerCount();
                int e2PointerCount = e2.getPointerCount();
                int pointerCount = Math.max(e1PointerCount, e2PointerCount);

                float startX1 = e1.getX();
                float startX2 = e2.getX();
                int gestureWidth = mGestureWidth;
                float left = gestureWidth / 3;
                float right = left * 2;
                // left area h gesture
                if (startX1 < left && startX2 < left) {
                    // brigtness
                    float diffFactor = getDeltaFactorY(e1, e2);
                    if (!mInVerticalMoving) {
                        mInVerticalMoving = true;
                        onStartTrackingTouch(OnTouchGestureListener.ACTION_VERTICAL_SCROLLING_LEFT);
                    }
                    onProgressChanged(OnTouchGestureListener.ACTION_VERTICAL_SCROLLING_LEFT, diffFactor, true, pointerCount, false);
                } else if (startX1 > right && startX2 > right) { // right area v gesture
                    // volume
                    float diffFactor = getDeltaFactorY(e1, e2);
                    if (!mInVerticalMoving) {
                        mInVerticalMoving = true;
                        onStartTrackingTouch(OnTouchGestureListener.ACTION_VERTICAL_SCROLLING_RIGHT);
                    }
                    onProgressChanged(OnTouchGestureListener.ACTION_VERTICAL_SCROLLING_RIGHT, diffFactor, true, pointerCount, false);
                }

                return false;
            }

            /**
             * 计算滑动距离占横屏幕的百分比
             * @param e1 前一个event事件
             * @param e2 当前event事件
             * @return 0...1.0（0%...100%）
             */
            private final float getDeltaFactorX(MotionEvent e1, MotionEvent e2) {
                int rangeX = mGestureWidth;
                if (rangeX <= 0) {
                    return 0.0f;
                }

                float deltaX = e2.getX() - e1.getX();
                return deltaX / rangeX;
            }

            /**
             * 计算滑动距离占竖直屏幕的百分比
             * @param e1 前一个event事件
             * @param e2 当前event事件
             * @return 0...1.0（0%...100%）
             */
            private final float getDeltaFactorY(MotionEvent e1, MotionEvent e2) {
                int rangeY = mGestureHeight;
                if (rangeY <= 0) {
                    return 0.0f;
                }

                float deltaY = e2.getY() - e1.getY();
                return deltaY / rangeY;
            }


            public void onTouch(MotionEvent ev) {
                if (mOnTouchGestureListener != null) {
                    mOnTouchGestureListener.onTouch(ev);
                }
            }

            public void setHorizontalGestureEnabled(boolean enabled) {
                mEnableHorizontalGesture = enabled;
            }
        }


    }

    public interface OnTouchGestureListener {
        int ACTION_HORIZONTAL_SCROLLING = 0x1;
        int ACTION_VERTICAL_SCROLLING_LEFT = 0x5;
        int ACTION_VERTICAL_SCROLLING_RIGHT = 0x6;

        /**
         * 开始拖动
         *
         * @param action
         */
        void onScrollStart(int action);

        /**
         * 拖动中
         *
         * @param action
         * @param progress
         */
        void onScroll(int action, float progress, int pointerCount, boolean inUnSeekRegion);

        /**
         * 拖动结束，手指离开屏幕
         *
         * @param action
         * @param progress
         */
        void onScrollStop(int action, float progress, boolean inUnSeekRegion);

        /**
         * 单击
         *
         * @return
         */
        boolean onSingleTapConfirmed();

        /**
         * 双击
         *
         * @return
         */
        boolean onDoubleTap();

        void onTwoFingerDoubleTap();

        /**
         * 手指触摸到
         *
         * @param ev
         */
        void onTouch(MotionEvent ev);

        /**
         * 长按
         *
         * @param ev
         */
        void onLongPress(MotionEvent ev);
    }
}
