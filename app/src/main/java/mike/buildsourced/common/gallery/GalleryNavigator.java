
package mike.buildsourced.common.gallery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import mike.buildsourced.R;

public class GalleryNavigator extends View {
    private static final int SPACING = 10;
    private static final int RADIUS = 8;
    private static final int STROKE_WIDTH = 1;
    private int mSize = 0;
    private int mPosition = 0;
    private static final Paint mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GalleryNavigator(Context context) {
        super(context);
        mOnPaint.setColor(ContextCompat.getColor(context, R.color.AppBlue));
        mOffPaint.setStyle(Paint.Style.STROKE);
        mOnPaint.setColor(ContextCompat.getColor(context, R.color.AppBlue));
        mOffPaint.setStrokeWidth(STROKE_WIDTH);
    }

    public GalleryNavigator(Context c, int size) {
        this(c);
        mSize = size;
    }

    public GalleryNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOnPaint.setColor(ContextCompat.getColor(context, R.color.AppBlue));
        mOffPaint.setStyle(Paint.Style.STROKE);
        mOffPaint.setColor(ContextCompat.getColor(context, R.color.AppBlue));
        mOffPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mSize; ++i) {
            if (i == mPosition) {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOnPaint);
            } else {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS-STROKE_WIDTH, mOffPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize * (2 * RADIUS + SPACING) - SPACING, 2 * RADIUS);
    }
    
    

//    @Override
//	public boolean isInEditMode() {
//		return false;
//	}

	public void setPosition(int id) {
        mPosition = id;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public void setPaints(int onColor, int offColor) {
        mOnPaint.setColor(onColor);
        mOffPaint.setColor(offColor);
    }

    public void setBlack() {
        setPaints(0xE6000000, 0x66000000);
    }

}
