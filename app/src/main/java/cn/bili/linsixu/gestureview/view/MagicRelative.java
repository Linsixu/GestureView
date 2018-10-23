package cn.bili.linsixu.gestureview.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.bili.linsixu.gestureview.R;

/**
 * 自定义布局之动态添加子View
 * Created by Magic
 * on 2018/10/23.
 */
public class MagicRelative extends RelativeLayout implements SeekBar.OnSeekBarChangeListener{
    private TextView textLeft;
    private TextView textRight;
    private SeekBar seekBar;
    private SeekBar.OnSeekBarChangeListener listener;//用于监听SeekBar回调

    public MagicRelative(Context context) {
        super(context);
        init(context,null);
    }

    public MagicRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MagicRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        //defStyleAttr是接受style文件下的自定义的样式
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
//        LinearLayout linearLayout = new LinearLayout(context);
//        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        params1.gravity = Gravity.CENTER;
//        addView(linearLayout,params1);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MagicRelative);

        boolean isShowLeft;
        boolean isShowRight;
        String leftName = null;
        String rightName = null;

        try{
            isShowLeft = a.getBoolean(R.styleable.MagicRelative__lsbShowLeftLabel,false);//自定义布局属性，用来判断是否加载左边的TextView
            isShowRight = a.getBoolean(R.styleable.MagicRelative__lsbShowRightLabel,false);//自定义布局属性，用来判断是否加载右边的TextView
            leftName = a.getString(R.styleable.MagicRelative__lsbShowLeftText);
            rightName = a.getString(R.styleable.MagicRelative__lsbShowRightText);
        }finally {
            a.recycle();
        }
        if(isShowLeft){
            textLeft = new TextView(new ContextThemeWrapper(context,R.style.MagiclItemLabel),null,R.style.MagiclItemLabel);
            textLeft.setId(R.id.label_left);//引用resource定义的资源文件id
            textLeft.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);//设置垂直居中和居左
            textLeft.setPadding(4,0,0,0);
            if(!TextUtils.isEmpty(leftName)) textLeft.setText(leftName);
            RelativeLayout.LayoutParams ps = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            ps.addRule(ALIGN_PARENT_LEFT);//Rule that aligns the child's left edge with its RelativeLayout parent's left edge.
            ps.addRule(CENTER_VERTICAL);//Rule that centers the child vertically with respect to the bounds of its RelativeLayout parent.
            addView(textLeft,ps);//添加View
        }

        if(isShowRight){
            textRight = new TextView(new ContextThemeWrapper(context,R.style.MagiclItemLabel),null,R.style.MagiclItemLabel);
            textRight.setId(R.id.label_right);
            textRight.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            textRight.setPadding(0,0,4,0);
            if(!TextUtils.isEmpty(rightName))textRight.setText(rightName);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            params.addRule(ALIGN_PARENT_RIGHT);
            params.addRule(CENTER_VERTICAL);
            addView(textRight,params);
        }

        seekBar = new SeekBar(context);
        seekBar.setMax(100);
        seekBar.setProgress(0);
        seekBar.setFocusable(true);
        seekBar.setOnSeekBarChangeListener(this);
        RelativeLayout.LayoutParams psBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        psBar.addRule(CENTER_VERTICAL);
        if(leftName != null){
            psBar.addRule(RIGHT_OF,R.id.label_left);//等同于android:layout_toRightOf="@id/label_left"
        }
        if(rightName != null){
            psBar.addRule(LEFT_OF,R.id.label_right);//等同于android:layout_toLeftOf="@id/label_right"
        }
        addView(seekBar,psBar);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(listener != null)listener.onProgressChanged(seekBar,progress,fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(listener != null)listener.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(listener != null)listener.onStopTrackingTouch(seekBar);
    }

    public void setListener(SeekBar.OnSeekBarChangeListener listener) {
        this.listener = listener;
    }

    public void setTextRight(String rightName){
        textRight.setText("rightName");
    }
}
