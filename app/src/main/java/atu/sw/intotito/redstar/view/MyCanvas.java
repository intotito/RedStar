package atu.sw.intotito.redstar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import atu.sw.intotito.redstar.R;

/**
 * A custom Class where all the drawing for the appliation will be performed
 *
 */
public class MyCanvas extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private Path star;
    private float majorRadius;
    private float minorRadius;

    private Paint starPaint;
    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public MyCanvas(Context context) {
        super(context);
        init(null, 0);
    }

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Canvas, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.Canvas_exampleString);
        mExampleColor = a.getColor(
                R.styleable.Canvas_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.Canvas_exampleDimension,
                mExampleDimension);
        majorRadius = a.getDimension(R.styleable.Canvas_majorRadius,
                majorRadius);
        minorRadius = a.getFloat(R.styleable.Canvas_cuspRatio, 0.5f) * majorRadius;

        if (a.hasValue(R.styleable.Canvas_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.Canvas_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Set up a default Paint object for drawing
        starPaint = new Paint();
        starPaint.setStyle(Paint.Style.STROKE);
        starPaint.setStrokeWidth(3f);
        starPaint.setColor(Color.RED);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
        star = new Path();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        drawStar(9, majorRadius, minorRadius, star, canvas);
        // Draw the text.
 /*       canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

*/        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
    //        mExampleDrawable.draw(canvas);
        }
    }

    /**
     * This method draws a 5-pointed star by plotting the crest(s) and trough(s) of the
     * Star. The drawing is done in 5 different phases implemented as a loop.
     * The crest(s) are calculated as <code>(R * cosΘ, R * sinΘ)</code> and the troughs are
     * calculated as <code>(r * cosø, r * sinø)</code> where <code>Θ</code> is the current angle
     * of rotation which is a multiple of the angles between the crests or troughs, and
     * <code>ø = Θ + Θ / 2</code>. For a 5-pointed star <code>Θ = 360° / 5 = 72°,
     * ø = 72 + 36 = 108</code>
     * @param R Major Radius of the Star
     * @param r Minor Radius of the Star
     * @param path Path object that will draw the Star
     * @param canvas Canvas object is passed into the method to effect the drawing
     */
    private void drawStar(int points, float R, float r, Path path, Canvas canvas){
        double rotation = 2 * Math.PI / points;
        double angle = rotation;//0.5 * Math.PI;

        canvas.drawArc(R - 10,
                R - 10,
                R + 10,
                R + 10,
                0f,
                360,
                true, starPaint);

        for(int i = 0; i < points; i++, angle += rotation){
            if(i == 0)
                path.moveTo((float)(R + R * Math.cos(angle)),
                        R - (float)(R * Math.sin(angle)));
            else
            path.lineTo((float)(R + R * Math.cos(angle)),
                    R - (float)(R * Math.sin(angle)));

            path.lineTo((float)(R + r * Math.cos(angle + rotation / 2f)),
                    R - (float)(r * Math.sin(angle + rotation / 2f)));
        }
        path.close();

 /*       for(int i = 0; i < 5; i++, angle += rotation){
            Log.e("Check", "i = " + i + " cos(a) = " + Math.cos(angle));
            path.lineTo(R + (float)(R * Math.cos(angle)), R - (float)(R * Math.sin(angle)));
            float a = (float) (angle + rotation / 2f);
            path.lineTo((float)(R - r * Math.cos(a)), (float)(R - r * Math.sin(a)));
        }
 */       canvas.drawPath(path, starPaint);
 
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view"s example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view"s example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view"s example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view"s example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}