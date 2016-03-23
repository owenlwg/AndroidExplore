package com.owen.animation;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mStopButton;
    Button mViewButton;
    Button mLayoutButton;
    Button mPropertyButton;

    Button mDragButton;

    ImageView mImageView;
    ImageView mRotateImageView;

    LinearLayout mLinearLayout;

    AnimatorSet mAnimatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                                         ObjectAnimator.ofFloat(mImageView, "rotationX", 0, 360),
                                         ObjectAnimator.ofFloat(mImageView, "rotationY", 0, 360),
                                         ObjectAnimator.ofFloat(mImageView, "rotation", 0, 360),
                                         ObjectAnimator.ofFloat(mImageView, "translationX", 0, 300),
                                         ObjectAnimator.ofFloat(mImageView, "translationY", 0, 300),
                                         ObjectAnimator.ofFloat(mImageView, "scaleX", 1, 1.5F),
                                         ObjectAnimator.ofFloat(mImageView, "scaleY", 1, 0.7f),
                                         ObjectAnimator.ofFloat(mImageView, "alpha", 1, 0.5f)
        );

        addDragButton();
    }

    private void initView() {
        mStopButton = (Button) findViewById(R.id.button_stop);
        mViewButton = (Button) findViewById(R.id.button);
        mLayoutButton = (Button) findViewById(R.id.button2);
        mPropertyButton = (Button) findViewById(R.id.button3);

        mImageView = (ImageView) findViewById(R.id.imageview_yn);
        mRotateImageView = (ImageView) findViewById(R.id.imageview_yn2);

        mLinearLayout = (LinearLayout) findViewById(R.id.container_linerlayout);

        mStopButton.setOnClickListener(this);
        mViewButton.setOnClickListener(this);
        mLayoutButton.setOnClickListener(this);
        mPropertyButton.setOnClickListener(this);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clearViewAnimation();
        switch (v.getId()) {
            //View Animation
            case R.id.button:
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.view_animation);
                mImageView.startAnimation(animation);
                animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
                mRotateImageView.startAnimation(animation);
                break;
            //Layout Animation
            case R.id.button2:
                ObjectAnimator objectAnimator = ObjectAnimator
                               .ofFloat(mLinearLayout, "translationY", 0, mLinearLayout.getHeight())
                               .setDuration(1000);
                if (mLinearLayout.getTranslationY() > 0) {
                    objectAnimator.reverse();
                } else {
                    objectAnimator.start();
                }
                break;
            //Property Animation
            case R.id.button3:
                //XML方式
                //平移，旋转，透明度属性动画
//                mAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.property_animator);
//                mAnimatorSet.setTarget(mImageView);
//                mAnimatorSet.start();
                //代码方式
                mAnimatorSet.setDuration(5 * 1000)
                        .start();
                break;
            case R.id.button_stop:
                clearViewAnimation();
                ;
                break;
            default:
                break;
        }
    }

    private void clearViewAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
        mImageView.clearAnimation();
        mRotateImageView.clearAnimation();
    }


    @Override
    protected void onDestroy() {
        clearViewAnimation();
        getWindowManager().removeView(mDragButton);
        super.onDestroy();
    }


    //使用WindowManager实现View的拖动
    private void addDragButton() {
        final WindowManager windowManager = getWindowManager();
        mDragButton = new Button(this);
        mDragButton.setBackgroundColor(Color.BLUE);
        mDragButton.setText("DragButton");
        final WindowManager.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                          LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT);
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR; //系统Window
        layoutParams.gravity= Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 300;
        layoutParams.y = 400;
        windowManager.addView(mDragButton, layoutParams);
        mDragButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = rawX - mDragButton.getWidth() / 2;
                        layoutParams.y = rawY - mDragButton.getHeight();
                        windowManager.updateViewLayout(mDragButton, layoutParams);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
