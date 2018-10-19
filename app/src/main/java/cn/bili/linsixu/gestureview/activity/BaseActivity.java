package cn.bili.linsixu.gestureview.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Magic
 * on 2018/10/16.
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected  T t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = (T)DataBindingUtil.setContentView(this,getLayoutId());
        initDate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDate();
        initClick();
    }

    public abstract int getLayoutId();

    public abstract void initDate();

    public abstract void initClick();

}
