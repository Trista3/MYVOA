package com.tryhard.myvoa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	public float currentX = 15;
	public float currentY = 80;
	// 定义、并创建画笔
	Paint p = new Paint();
	int color = 0x00FCFFEE;
	Canvas canvasM;

	public DrawView(Context context) {
		super(context);
	}

	public DrawView(Context context, AttributeSet set) {
		super(context, set);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 设置画笔颜色
		p.setColor(color);
		// 绘制一个小圆(作为小球)
		canvas.drawCircle(currentX, currentY, 14, p);
	}

	public void setColor(int color){
		this.color = color;

	//	canvasM.drawCircle(currentX, currentY, 12, p);
	}
}
