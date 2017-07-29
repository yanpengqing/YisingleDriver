package com.yisingle.driver.app.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yisingle.driver.app.R;

/**
 * Created by jikun on 2016/12/6.
 */

public class ScrollSwithViewButton extends RelativeLayout {
    public final String TAG = ScrollSwithViewButton.class.getSimpleName();


    private String center_text;

    private int center_text_color;
    private int center_text_size;

    private Drawable background;//底部默认资源引用


    private int scrollButtonWith;

    private Drawable scrollButtonBackground;//左边滚动按钮的背景图片

    private Drawable rightImageViewSrc;


    private TextView textView;//中间文字效果

    private ImageView rightImageView;//右边箭头图片

    private Button srcollButton;//滑动Button


    private GestureDetector mGestureDetector;

    private boolean isscoll = false;//是否滚动了

    private boolean issuccess = false;//viewGroup滑动是否成功

    public OnSrollSwichListener onSrollSwichListener;

    public ScrollSwithViewButton(Context context) {
        super(context);
    }

    public ScrollSwithViewButton(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollSwithViewButton);
        center_text_size = typedArray.getDimensionPixelSize(R.styleable.ScrollSwithViewButton_center_textSize, dip2px(getContext(), 16));//获取XML设置的text_size的大小，默认为16dp
        center_text_color = typedArray.getColor(R.styleable.ScrollSwithViewButton_center_textColor, Color.RED);
        center_text = typedArray.getString(R.styleable.ScrollSwithViewButton_center_text);
        background = typedArray.getDrawable(R.styleable.ScrollSwithViewButton_center_background);

        scrollButtonWith = typedArray.getLayoutDimension(R.styleable.ScrollSwithViewButton_scroll_button_with, dip2px(getContext(), 72));
        scrollButtonBackground = typedArray.getDrawable(R.styleable.ScrollSwithViewButton_scroll_button_background);

        rightImageViewSrc = typedArray.getDrawable(R.styleable.ScrollSwithViewButton_right_image_view_src);
        typedArray.recycle();
        initView();

    }


    private void initView() {
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(background);
            } else {
                setBackgroundDrawable(background);
            }

        }
        initLeftScrollButton();//添加并初始化左侧滑动块
        initCenterTextView();//添加并初始化中间文本框
        initRightArrowImageview();//添加并初始化右侧箭头图片


        Drawable drawable = getResources().getDrawable(R.mipmap.scroll_swich_arrow_right);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        srcollButton.setCompoundDrawables(null, null, drawable, null);

        mGestureDetector = new GestureDetector(getContext(), new SrollGestureListener());

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                if (isscoll && event.getAction() == MotionEvent.ACTION_UP) {
                    if (issuccess) {
                        startWithtoOrgitAnimation(srcollButton.getWidth(), getWidth());
                    } else {
                        startWithtoOrgitAnimation(srcollButton.getWidth(), scrollButtonWith);
                    }

                }
                return true;
            }
        });


    }

    /**
     * 左侧滑动View
     */
    private void initLeftScrollButton() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(scrollButtonWith, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_VERTICAL);
        layoutParams.addRule(ALIGN_PARENT_LEFT);

        srcollButton = new Button(getContext());
        srcollButton.setPadding(24, 0, 0, 0);//int left, int top, int right, int bottom
        srcollButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        srcollButton.setClickable(false);
        srcollButton.setEnabled(false);

        if (scrollButtonBackground != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                srcollButton.setBackground(scrollButtonBackground);
            } else {
                srcollButton.setBackgroundDrawable(scrollButtonBackground);
            }

        }

        addView(srcollButton, layoutParams);
    }

    /**
     * 添加并初始化中间文本框
     */
    private void initCenterTextView() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        textView = new TextView(getContext());
        textView.setTextColor(center_text_color);
        textView.setTextSize(center_text_size);
        textView.setText(center_text);
        addView(textView, layoutParams);
    }

    /**
     * 添加并初始化右边箭头图像
     */
    private void initRightArrowImageview() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_VERTICAL);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, 0, 24, 0);//int left, int top, int right, int bottom
        rightImageView = new ImageView(getContext());
        if (rightImageViewSrc != null) {
            rightImageView.setImageDrawable(rightImageViewSrc);
        }
        addView(rightImageView, layoutParams);
    }


    public class SrollGestureListener implements GestureDetector.OnGestureListener {
        private float previousX;//down事件按下的X的相对坐标
        private int moriginalScrollSwithViewWidth;//当前控件的原始大小
        private int moriginalWidth;//srcollButton的原始宽度

        private int criticalX;//临界点的X位置

        @Override
        public boolean onDown(MotionEvent e) {
            isscoll = false;
            previousX = e.getX();//在按下的时候设置X的坐标
            moriginalScrollSwithViewWidth = getWidth();
            moriginalWidth = scrollButtonWith;
            criticalX = getWidth()*3/4;

            Log.e("测试代码","测试代码getWidth()="+getWidth());



            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            int currentX = (int) e2.getX();

            if (currentX >= moriginalWidth && currentX <= moriginalScrollSwithViewWidth) {
                isscoll = true;

                ViewGroup.LayoutParams layoutParams = srcollButton.getLayoutParams();


                layoutParams.width = currentX;

                srcollButton.requestLayout();

                if (currentX >= criticalX) {
                    Log.e("测试代码","测试代码criticalX="+criticalX+"-----currentX="+currentX);
                    issuccess = true;
                    if (onSrollSwichListener != null) {
                        onSrollSwichListener.onSrollingMoreThanCritical();
                    }
                } else {
                    issuccess = false;
                    if (onSrollSwichListener != null) {
                        onSrollSwichListener.onSrollingLessThanCriticalX();
                    }

                }

            }


            return true;
        }


        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void startWithtoOrgitAnimation(int currentheight, int toheight) {
        ValueAnimator animator = null;
        //为了看到动画值的变化，这里添加了动画更新监听事件来打印动画的当前值

        animator = ValueAnimator.ofInt(currentheight, toheight);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = srcollButton.getLayoutParams();
                layoutParams.width = value;
                srcollButton.requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setEnabled(false);
                setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEnabled(true);
                setClickable(true);
                if (issuccess) {
                    startShakeAnimator();
                    recoverySrcollButton();
                    if (onSrollSwichListener != null) {

                        onSrollSwichListener.onSlideFinishSuccess();
                    }
                } else {
                    if (onSrollSwichListener != null) {
                        onSrollSwichListener.onSlideFinishCancel();
                    }
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(250);//动画时间
        animator.start();//启动动画
    }


    /**
     * 开始抖动动画
     */
    private void startShakeAnimator() {


        TranslateAnimation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(7));
        translateAnimation.setDuration(200);
        startAnimation(translateAnimation);


    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public interface OnSrollSwichListener {
        void onSrollingMoreThanCritical();//当前滑动超过临界值

        void onSrollingLessThanCriticalX();//当前滑动小于临界值X

        void onSlideFinishSuccess();//已经滑动确认成功

        void onSlideFinishCancel();//已经滑动确认取消

    }

    public void setOnSrollSwichListener(OnSrollSwichListener onSrollSwichListener) {
        this.onSrollSwichListener = onSrollSwichListener;
    }

    /**
     * 恢复recoverySrcollButton
     */
    private void recoverySrcollButton() {
        ViewGroup.LayoutParams layoutParams = srcollButton.getLayoutParams();
        layoutParams.width = scrollButtonWith;
        srcollButton.requestLayout();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTextSize(float size) {
        textView.setTextSize(size);
    }
}