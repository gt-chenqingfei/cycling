package com.beastbikes.android.modules.user.filter.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SaveImageView extends ImageView {

    private Paint paint_rect = new Paint();

    private int offset = 0;

    public SaveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//    	paint_rect.setColor(Color.WHITE);
//    	paint_rect.setStrokeWidth(3);
//    	final int width = canvas.getWidth(); // hdpi 480x800
//		final int height = canvas.getHeight();
//		final int edgeWidth = 10;
//		final int space = width / 3; // 长宽间隔
//		int vertz = this.getOffset();
//		int hortz = 0;
//		for (int i = 0; i < 100; i++) {
//			if(vertz!=this.getOffset() || width!=vertz){
//				canvas.drawLine(0, vertz, width, vertz, paint_rect);
//			}
//			if(0!=hortz || width!=hortz){
//				canvas.drawLine(hortz, 0, hortz, height, paint_rect);
//			}
//			vertz += space;
//			hortz += space;
//		}
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}  
