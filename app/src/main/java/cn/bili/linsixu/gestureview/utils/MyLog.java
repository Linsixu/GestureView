package cn.bili.linsixu.gestureview.utils;

import android.util.Log;

import cn.bili.linsixu.gestureview.BuildConfig;

/**
 * Created by Magic
 * on 2018/10/16.
 */
public class MyLog {

    public static void i(String TAG,String content){
        if(BuildConfig.Debug){
            Log.i(TAG,content);
        }
    }
}
