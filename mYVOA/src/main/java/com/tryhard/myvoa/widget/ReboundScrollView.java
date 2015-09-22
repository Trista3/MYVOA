package com.tryhard.myvoa.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * �е��Ե�ScrollView ʵ���������غ���������
 * 
 * @author zhangjg
 * @date Feb 13, 2014 6:11:33 PM
 */
public class ReboundScrollView extends ScrollView {

	// ����
	private static final String TAG = "ReboundScrollView";
	private static final float MOVE_FACTOR = 0.5f; // �ƶ����ӣ� �ӻ���ͼ������ָ�ƶ��Ļ���
	private static final int ANIM_TIME = 300; // ����ʱ�䣺����ص�����λ����Ҫ�Ķ���ʱ��

	// ����
	private View contentView; // ScrollView��Ψһһ����View
	private float startY; // ��ָ����ʱ��Yֵ,���ڼ�¼�������������ľ��룬�ڴ��ڲ�����������������״̬�����Ϊ��ǰ��ֵ
	private Rect originalRect = new Rect(); // �����Ĳ���λ��
	private boolean canPullDown = false; // ��ָ����ʱ��¼�Ƿ���Լ�������
	private boolean canPullUp = false; // ��ָ����ʱ��¼�Ƿ���Լ�������
	private boolean isMoved = false; // ����ָ�����Ĺ����м�¼�Ƿ��ƶ��˲��֣������ƶ�

	public ReboundScrollView(Context context) {
		super(context);
	}

	public ReboundScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ��ȡScrollView��Ψһ�����
	 * */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			contentView = getChildAt(0); // ��ȡScrollView��Ψһ�����
		}
	}

	/**
	 * ��ȡScrollView��Ψһ������ĳ�ʼλ����Ϣ
	 * */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (contentView == null)
			return;
		// ��ȡScrollView��Ψһ�ӿؼ��ĳ�ʼλ����Ϣ, ���λ����Ϣ�������ؼ������������б��ֲ���
		originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
	}

	/**
	 * �ڴ����¼���, �����������������߼����ɷ������¼�
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (contentView == null) {
			return super.dispatchTouchEvent(ev);
		}
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			// �ж��Ƿ��������������
			canPullDown = isCanPullDown();
			canPullUp = isCanPullUp();
			// ��¼��ָ����ʱ��Yֵ
			startY = ev.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {
			// ���û���ƶ����֣� ������ִ��
			if (!isMoved)
				break;
			// �ƶ��˲��֣������������䵯��
			TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
			anim.setDuration(ANIM_TIME);
			contentView.startAnimation(anim);

			// ���ûص������Ĳ���λ��
			contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);

			// ����־λ���false
			canPullDown = false;
			canPullUp = false;
			isMoved = false;

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			// ���ƶ��Ĺ����У� ��û�й��������������ĳ̶ȣ� Ҳû�й��������������ĳ̶�
			if (!canPullDown && !canPullUp) {
				startY = ev.getY();
				canPullDown = isCanPullDown();
				canPullUp = isCanPullUp();
				break;
			}

			// ������ָ�ƶ��ľ���
			float nowY = ev.getY();
			int deltaY = (int) (nowY - startY);

			// �Ƿ�Ӧ���ƶ�����
			boolean shouldMove = (canPullDown && deltaY > 0) // ���������� ������ָ�����ƶ�
					|| (canPullUp && deltaY < 0) // ���������� ������ָ�����ƶ�
					|| (canPullUp && canPullDown); // �ȿ�������Ҳ�����������������������ScrollView�����Ŀؼ���ScrollView��С��

			if (shouldMove) {
				// ����ƫ����
				int offset = (int) (deltaY * MOVE_FACTOR);
				// ������ָ���ƶ����ƶ�����
				contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right,
						originalRect.bottom + offset);
				isMoved = true; // ��¼�ƶ��˲���
			}
			break;
		}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * �ж��Ƿ����������
	 */
	private boolean isCanPullDown() {
		return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
	}

	/**
	 * �ж��Ƿ�������ײ�
	 */
	private boolean isCanPullUp() {
		return contentView.getHeight() <= getHeight() + getScrollY();
	}

}
