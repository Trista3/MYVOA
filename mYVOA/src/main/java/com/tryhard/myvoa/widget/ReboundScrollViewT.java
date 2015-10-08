package com.tryhard.myvoa.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;



/**
 * 有弹性的ScrollView 实现下拉弹回和上拉弹回
 *
 * @author zhangjg
 * @date Feb 13, 2014 6:11:33 PM
 */
public class ReboundScrollViewT extends ScrollView {

	// 常量
	private static final String TAG = "ReboundScrollView";
	private static final float MOVE_FACTOR = 0.5f; // 移动因子： 延缓视图随着手指移动的滑动
	private static final int ANIM_TIME = 300; // 弹回时间：界面回到正常位置需要的动画时间

	// 变量
	private View contentView; // ScrollView的唯一一个子View
	private float startY; // 手指按下时的Y值,用于记录上拉或者下拉的距离，在处于不可上拉或者下拉的状态会更新为当前的值
	private Rect originalRect = new Rect(); // 正常的布局位置
	private boolean canPullDown = false; // 手指按下时记录是否可以继续下拉
	private boolean canPullUp = false; // 手指按下时记录是否可以继续上拉
	private boolean isMoved = false; // 在手指滑动的过程中记录是否移动了布局，若有移动

	public ReboundScrollViewT(Context context) {
		super(context);
	}

	public ReboundScrollViewT(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 获取ScrollView的唯一子组件
	 * */
	@SuppressLint("MissingSuperCall")
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			contentView = getChildAt(0); // 获取ScrollView的唯一子组件
		}
	}

	/**
	 * 获取ScrollView的唯一子组件的初始位置信息
	 * */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (contentView == null)
			return;
		// 获取ScrollView的唯一子控件的初始位置信息, 这个位置信息在整个控件的生命周期中保持不变
		originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
	}

	/**
	 * 在触摸事件中, 处理上拉和下拉的逻辑并派发触摸事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (contentView == null) {
			return super.dispatchTouchEvent(ev);
		}
		int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				// 判断是否可以上拉和下拉
				canPullDown = isCanPullDown();
				canPullUp = isCanPullUp();
				// 记录手指按下时的Y值
				startY = ev.getY();
				break;
			}
			case MotionEvent.ACTION_UP: {
				// 如果没有移动布局， 则跳过执行
				if (!isMoved)
					break;
				// 移动了布局，开启动画让其弹回
				TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
				anim.setDuration(ANIM_TIME);
				contentView.startAnimation(anim);

				// 设置回到正常的布局位置
				contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);

				// 将标志位设回false
				canPullDown = false;
				canPullUp = false;
				isMoved = false;

				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 在移动的过程中， 既没有滚动到可以上拉的程度， 也没有滚动到可以下拉的程度
				if (!canPullDown && !canPullUp) {
					startY = ev.getY();
					canPullDown = isCanPullDown();
					canPullUp = isCanPullUp();
					break;
				}

				// 计算手指移动的距离
				float nowY = ev.getY();
				int deltaY = (int) (nowY - startY);

				// 是否应该移动布局
				boolean shouldMove = (canPullDown && deltaY > 0) // 可以下拉， 并且手指向下移动
						|| (canPullUp && deltaY < 0) // 可以上拉， 并且手指向上移动
						|| (canPullUp && canPullDown); // 既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）

				if (shouldMove) {
					// 计算偏移量
					int offset = (int) (deltaY * MOVE_FACTOR);
					// 随着手指的移动而移动布局
					contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right,
							originalRect.bottom + offset);
					isMoved = true; // 记录移动了布局
				}
				break;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 判断是否滚动到顶部
	 */
	private boolean isCanPullDown() {
		return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
	}

	/**
	 * 判断是否滚动到底部
	 */
	private boolean isCanPullUp() {
		return contentView.getHeight() <= getHeight() + getScrollY();
	}

}


