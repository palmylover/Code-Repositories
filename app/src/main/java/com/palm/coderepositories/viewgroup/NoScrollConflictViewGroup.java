package com.palm.coderepositories.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by palm on 16/9/8.
 * In Code Repositories -> com.palm.coderepositories.viewgroup
 */
public class NoScrollConflictViewGroup extends ViewGroup{
  public NoScrollConflictViewGroup(Context context) {
    super(context);
  }

  public NoScrollConflictViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {

  }

  /**
   * 防止冲突的flag。<br/>
   * true : 识别为左右滑动事件，并且允许子view处理，parent 放行；<br/>
   * false : 当前非左右滑动事件，或禁止子view处理，事件由 parent 捕获处理。
   */
  private boolean mPreventForHorizontal = false;

  /**
   * 上一个 motion event，用于判断滑动手势：左右、上下等
   */
  private MotionEvent mLastMoveEvent;

  /**
   * 是否允许子 view 处理左右滑动事件
   */
  private boolean mDisableWhenHorizontalMove = true;

  /**
   * 当大于这个值时表明这是一次滑动事件。<br/>
   * 来自于 {@link ViewConfiguration#getScaledTouchSlop()} <br/>
   * 最先的处理是：mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2 <br/>
   * 跟进发现，系统的 getScaledTouchSlop() 返回的值为 8dp，导致识别手势出错，因此需要一个更好的方法作手势判断
   */
  //private int mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2;
  private int mPagingTouchSlop = 10;

  /**
   * 防止 ViewGroup 本身和其子 View 在滑动时的冲突：子 View 处理左右滑动时 ViewGroup 不处理上下滑动动作
   */
  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    // ...
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        // ...
      case MotionEvent.ACTION_DOWN:
        // ...
        mLastMoveEvent = ev;
        // 按下时置为false，在move中判断
        mPreventForHorizontal = false;
        // ...
        return true;

      case MotionEvent.ACTION_MOVE:
        float offsetX = ev.getX() - mLastMoveEvent.getX();
        float offsetY = ev.getY() - mLastMoveEvent.getY();
        mLastMoveEvent = ev;

        // 根据 x 轴运动距离以及 x 轴和 y 轴的比例判断是否是左右滑动
        // 还要加上 parent 是否在初始位置的判断，以保证只在没开始下拉时传递给 child
        if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX)
            > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
          // ...
          //if (mPtrIndicator.isInStartPosition()) {
            mPreventForHorizontal = true;
          //}
          // ...
        }
        // ...
        if (mPreventForHorizontal) {
          return super.dispatchTouchEvent(ev);
        }
        // ...
    }
    return super.dispatchTouchEvent(ev);
  }
}
