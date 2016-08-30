package com.app.dragviewlayout.mydragviewlayout;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dh193 on 2016/8/29.
 */
public class MyDragViewLayout extends ViewGroup{
    public ViewDragHelper viewDragHelper;
    private boolean isOpen;
    private View mMenuView;
    private View mContentView;
    private int mCurrentTop=0;

    public MyDragViewLayout(Context context) {
        super(context);
        init();
    }



    public MyDragViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyDragViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper=ViewDragHelper.create(this,1.0f,new ViewDragHelperCallBack());
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==mContentView;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.max(Math.min(top,mMenuView.getHeight()),0);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int finalTop=mMenuView.getHeight();
            if(yvel<=0){
                if(releasedChild.getTop()<=mMenuView.getHeight()/2){
                    finalTop=0;
                }else if(releasedChild.getTop()>=mMenuView.getHeight()/2){
                    finalTop=mMenuView.getHeight();
                }else {
                    finalTop=0;
                }
            }
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(),finalTop);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mMenuView.setVisibility((changedView.getHeight()-top==getHeight())?View.GONE:View.VISIBLE);
            mCurrentTop+=dy;
            requestLayout();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (mMenuView==null)
                return 0;
            return (mMenuView==child)?mMenuView.getHeight():0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state==ViewDragHelper.STATE_IDLE){
                isOpen=(mContentView.getTop()==mMenuView.getHeight());
            }
        }

    }

    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    public boolean isDrawerOpened(){
        return isOpen;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth=MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight=MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth,measureHeight);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView=getChildAt(0);
        mContentView=getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
             mMenuView.layout(0,0,mMenuView.getMeasuredWidth(),mMenuView.getMeasuredHeight());
             mContentView.layout(0,mCurrentTop+mMenuView.getHeight(),
                     mContentView.getMeasuredWidth(),mCurrentTop+mContentView.getMeasuredHeight()+mMenuView.getHeight());
    }
}
