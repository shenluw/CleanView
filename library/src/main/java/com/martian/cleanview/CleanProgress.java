package com.martian.cleanview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Shenluw
 *         创建日期：2017/4/28 18:06
 */
public class CleanProgress extends View {
    private static final String TAG = "CleanProgress";
    private static final boolean debug = false;

    private static final int DEFAULT_LINE_COUNT = 5;
    private final Rect debugRect = new Rect();

    private int mCenterX, mCenterY;


    private TextPaint mProgressTextPaint;
    private MText mProgressText = new MText();
    private int startProgress;
    private int targetProgress;
    private String mark = "%";
    private float progressTextSize = 120;
    @ColorInt
    private int progressTextColor = Color.RED;

    private Paint mCirclePaint;
    private MCircle mInnerFillCircle;
    private MCircle mOuterFillCircle;
    //    private Interpolator gradientInterpolator;
    private float innerRadius = 100, outerRadius = 120;
    @ColorInt
    private int innerCircleColor = Color.GREEN;
    @ColorInt
    private int outerCircleColor = Color.BLUE;
//    @ColorInt
//    private int outerCircleTargetColor = Color.GREEN;
//    @ColorInt
//    private int innerCircleTargetColor = Color.RED;

    private Paint mRingPaint;
    private MSymmetryArc mRingArc;
    private int ringAngular = 1000;
    private int ringAcceleration = 100;
    private float ringSweepAngle = 20;
    private float ringTargetAngle = 60;
    @ColorInt
    private int ringColor;

    final private List<MCircle> mCircleLines = new ArrayList<>();
    private float mCircleLinesSpace;
    @ColorInt
    private int circleLineColor = Color.RED;
    private float circleLineWidth = 2;
    private int lineCount = DEFAULT_LINE_COUNT;


    private TextPaint mFractionsPaint;
    private MFractions mFractions;
    private CharSequence[] fractionTexts = {};
    private float fractionTextSize = 30;
    @ColorInt
    private int fractionColor = Color.BLUE;
    private float fractionGenerateInterval = 30;
    private float fractionGenerateFactor = .7f;


    private Animator mAnimator;

    private float piece = .4f;
    private long duration;


    public CleanProgress(Context context) {
        this(context, null, 0);
    }

    public CleanProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public CleanProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }


    private void initialize(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.CleanProgress, defStyleAttr, defStyleRes);

        CharSequence[] fractions = attributes.getTextArray(R.styleable.CleanProgress_fractionTexts);
        if (fractions != null) fractionTexts = fractions;
        Resources res = getResources();
        duration = attributes.getInt(R.styleable.CleanProgress_duration,
                res.getInteger(R.integer.mc_duration));

        lineCount = attributes.getInt(R.styleable.CleanProgress_lineCount,
                res.getInteger(R.integer.mc_line_count));
        String mark = attributes.getString(R.styleable.CleanProgress_mark);
        this.mark = TextUtils.isEmpty(mark) ? "%" : mark;

        this.progressTextSize = attributes.getDimension(R.styleable.CleanProgress_progressTextSize
                , res.getDimension(R.dimen.mc_progress_text_size));

        Theme theme = context.getTheme();
        this.progressTextColor = attributes.getColor(R.styleable.CleanProgress_progressTextColor,
                ResourcesCompat.getColor(res, R.color.mc_progress_text_color, theme));

        this.startProgress = attributes.getInteger(R.styleable.CleanProgress_startProgress,
                res.getInteger(R.integer.mc_start_progress));

        this.targetProgress = attributes.getInteger(R.styleable.CleanProgress_targetProgress,
                res.getInteger(R.integer.mc_target_progress));

        this.innerRadius = attributes.getDimension(R.styleable.CleanProgress_innerRadius,
                res.getDimension(R.dimen.mc_inner_radius));

        this.outerRadius = attributes.getDimension(R.styleable.CleanProgress_outerRadius,
                res.getDimension(R.dimen.mc_outer_radius));

        this.innerCircleColor = attributes.getColor(R.styleable.CleanProgress_innerCircleColor,
                ResourcesCompat.getColor(res, R.color.mc_inner_circle_color, theme));

        this.outerCircleColor = attributes.getColor(R.styleable.CleanProgress_outerCircleColor,
                ResourcesCompat.getColor(res, R.color.mc_outer_circle_color, theme));

//        this.outerCircleTargetColor = attributes.getColor(R.styleable.CleanProgress_outerCircleTargetColor,
//                ResourcesCompat.getColor(res, R.color.mc_outer_circle_target_color, theme));
//        this.innerCircleTargetColor = attributes.getColor(R.styleable.CleanProgress_innerCircleTargetColor,
//                ResourcesCompat.getColor(res, R.color.mc_inner_circle_target_color, theme));

        this.ringAngular = attributes.getInt(R.styleable.CleanProgress_ringAngular,
                res.getInteger(R.integer.mc_ring_angular));
        this.ringAcceleration = attributes.getInt(R.styleable.CleanProgress_ringAcceleration,
                res.getInteger(R.integer.mc_ring_acceleration));

        this.ringSweepAngle = attributes.getFloat(R.styleable.CleanProgress_ringSweepAngle,
                res.getInteger(R.integer.mc_ring_sweep_angle));

        this.ringTargetAngle = attributes.getFloat(R.styleable.CleanProgress_ringTargetAngle,
                res.getInteger(R.integer.mc_ring_target_angle));

        this.ringColor = attributes.getColor(R.styleable.CleanProgress_ringColor,
                ResourcesCompat.getColor(res, R.color.mc_ring_color, theme));

        this.circleLineColor = attributes.getColor(R.styleable.CleanProgress_circleLineColor,
                ResourcesCompat.getColor(res, R.color.mc_circle_line_color, theme));

        this.circleLineWidth = attributes.getDimension(R.styleable.CleanProgress_circleLineWidth,
                res.getDimension(R.dimen.mc_circle_line_width));

        this.fractionTextSize = attributes.getDimension(R.styleable.CleanProgress_fractionTextSize,
                res.getDimension(R.dimen.mc_fraction_Text_Size));

        this.fractionColor = attributes.getColor(R.styleable.CleanProgress_fractionColor
                , ResourcesCompat.getColor(res, R.color.mc_fraction_color, theme));

        this.fractionGenerateInterval = attributes.getFloat(R.styleable.CleanProgress_fractionGenerateInterval,
                res.getInteger(R.integer.mc_fraction_generate_interval));

        TypedValue typedValue = new TypedValue();
        res.getValue(R.dimen.mc_fraction_generate_factor, typedValue, true);

        this.fractionGenerateFactor = attributes.getFloat(R.styleable.CleanProgress_fractionGenerateFactor,
                typedValue.getFloat());

        res.getValue(R.dimen.mc_piece, typedValue, true);
        this.piece = attributes.getFloat(R.styleable.CleanProgress_piece,
                typedValue.getFloat());

        this.duration = attributes.getInt(R.styleable.CleanProgress_duration,
                res.getInteger(R.integer.mc_duration));
        attributes.recycle();


//        gradientInterpolator = new LinearInterpolator();

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Style.STROKE);
        mCirclePaint.setStrokeWidth(circleLineWidth);
        mCirclePaint.setColor(circleLineColor);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(outerRadius - innerRadius);
        mRingPaint.setStyle(Style.STROKE);
        mRingPaint.setStrokeCap(Cap.ROUND);
        mRingPaint.setColor(ringColor);


        mFractionsPaint = new TextPaint();
        mFractionsPaint.setColor(fractionColor);

        mProgressTextPaint = new TextPaint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setTextSize(progressTextSize);
        mProgressTextPaint.setColor(progressTextColor);

        mInnerFillCircle = new MCircle(0, 0, innerRadius);
        mOuterFillCircle = new MCircle(0, 0, outerRadius);

        mRingArc = new MSymmetryArc();

        for (int i = 0; i < lineCount; i++) {
            mCircleLines.add(new MCircle());
        }

        mProgressText.setTextSize(progressTextSize);
        mProgressText.setText(startProgress + this.mark);


        this.mFractions = new MFractions();
        this.mFractions.setStartTextSize(fractionTextSize);
        this.mFractions.setTarget(mCenterY);
        this.mFractions.setFractionTexts(fractionTexts);

        updateDrawable();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveMeasured(widthMeasureSpec, getSuggestedMinimumWidth());
        int height = resolveMeasured(heightMeasureSpec, getSuggestedMinimumHeight());
        if (debug) {
            debugRect.set(0, 0, width, height);
            Log.d(TAG, "onMeasure: " + debugRect);
        }

        updateDrawableBounds(width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDrawableBounds(w, h);
    }

    public void updateDrawable() {
        mCirclePaint.setStrokeWidth(circleLineWidth);
        mCirclePaint.setColor(circleLineColor);

        mRingPaint.setStrokeWidth(outerRadius - innerRadius);
        mRingPaint.setColor(ringColor);

        mFractionsPaint.setColor(fractionColor);

        mProgressTextPaint.setTextSize(progressTextSize);
        mProgressTextPaint.setColor(progressTextColor);


        mInnerFillCircle.setR(innerRadius);
        mOuterFillCircle.setR(outerRadius);

        mCircleLines.clear();
        for (int i = 0; i < lineCount; i++) {
            mCircleLines.add(new MCircle());
        }

        updateDrawableBounds(getWidth(), getHeight());
    }

    private void updateDrawableBounds(int w, int h) {
        int centerX = w / 2, centerY = h / 2;
        mCenterX = centerX;
        mCenterY = centerY;
        mInnerFillCircle.set(centerX, centerY);
        mInnerFillCircle.setColor(innerCircleColor);
        mOuterFillCircle.set(centerX, centerY);
        mOuterFillCircle.setColor(outerCircleColor);


        mProgressText.setX(centerX);
        mProgressText.setY(centerY);

        mProgressText.setTextSize(progressTextSize);

        float segment = (Math.min(w - getPaddingLeft() - getPaddingRight(),
                h - getPaddingTop() - getPaddingBottom()) / 2 - outerRadius) / lineCount;

        for (int i = 0; i < lineCount; i++) {
            MCircle circle = mCircleLines.get(i);
            circle.setColor(circleLineColor);
            circle.set(centerX, centerY);
            circle.setR(segment * (i + 1) + outerRadius);
        }
        mCircleLinesSpace = segment;

        float padding = (outerRadius - innerRadius) / 2;
        float arcRadius = outerRadius;
        RectF oval = new RectF(centerX - arcRadius + padding, centerY - arcRadius + padding, centerX + arcRadius - padding, centerY + arcRadius - padding);
        mRingArc.setOval(oval);

        mRingArc.setSweepAngle(ringSweepAngle);

        mFractions.setRange((int) (Math.min(getHeight() - getPaddingBottom() - getPaddingTop()
                , getWidth() - getPaddingLeft() - getPaddingRight()
        ) / 2 - outerRadius));
        mFractions.setTarget(centerY);
        mFractions.setFractionTexts(fractionTexts);

        mFractions.setPivotX(centerX);
        mFractions.setPivotY(centerY);
        mFractions.setLimitFactor(fractionGenerateFactor);
        mFractions.setInterval(fractionGenerateInterval);

    }

    private int resolveMeasured(int measureSpec, int desired) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                return desired;
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, desired);
            case MeasureSpec.EXACTLY:
            default:
                return specSize;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircleLines(mCirclePaint, canvas);

        mFractions.draw(mFractionsPaint, canvas, mCenterX, mCenterY);

        drawFillCircle(mCirclePaint, canvas);

        mRingArc.draw(mRingPaint, canvas, mCenterX, mCenterY);

        mProgressText.draw(mProgressTextPaint, canvas, mCenterX, mCenterY);

        if (debug) drawDebug(mRingPaint, canvas);
    }

    private void drawFillCircle(Paint paint, Canvas canvas) {
        Style originStyle = paint.getStyle();
        paint.setStyle(Style.FILL);
        mOuterFillCircle.draw(paint, canvas, mCenterX, mCenterY);
        mInnerFillCircle.draw(paint, canvas, mCenterX, mCenterY);
        paint.setStyle(originStyle);
    }

    private void drawCircleLines(Paint paint, Canvas canvas) {
        for (int i = 0; i < lineCount; i++) {
            mCircleLines.get(i).draw(paint, canvas, mCenterX, mCenterY);
        }
    }

    private void drawDebug(Paint paint, Canvas canvas) {
        int color = paint.getColor();
        float strokeWidth = paint.getStrokeWidth();
        Style style = paint.getStyle();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(8);
        paint.setStyle(Style.STROKE);
        canvas.drawRect(debugRect, paint);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);

    }

    public void start(AnimatorListener listener) {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(listener);
        animatorSet.setDuration(duration);
        mAnimator = animatorSet;

        ValueAnimator progressAnimator = ValueAnimator.ofInt(startProgress, targetProgress);
        progressAnimator.setDuration(duration);
        progressAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgressText.setText(animation.getAnimatedValue() + mark);
            }
        });

        Collections.sort(mCircleLines, CIRCLE_COMPARATOR);
        ValueAnimator circleAnimator = ValueAnimator.ofFloat(mCircleLines.get(mCircleLines.size() - 1).getR(), 0);
        circleAnimator.setDuration((long) (duration * (1 - piece)));
        circleAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float r = (float) animation.getAnimatedValue();
                for (int i = mCircleLines.size() - 1; i >= 0; i--) {
                    mCircleLines.get(i).setR(r - i * mCircleLinesSpace);
                }
            }
        });

        final TimeAnimator arcDegrees = new TimeAnimator();
        arcDegrees.setDuration((long) (duration * (1 - piece)));
        arcDegrees.setTimeListener(new TimeListener() {
            float angular = ringAngular;

            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                float t = deltaTime / 1000f;
                angular += ringAcceleration * t;
                mRingArc.addDegrees(t * angular);
            }
        });

        ValueAnimator arcSweepAnimator = ValueAnimator.ofFloat(mRingArc.getSweepAngle(), ringTargetAngle);
        arcSweepAnimator.setDuration((long) (duration * piece));
        arcSweepAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRingArc.setSweepAngle((Float) animation.getAnimatedValue());
            }
        });

        TimeAnimator textsAnimator = new TimeAnimator();
        textsAnimator.setTimeListener(mFractions);
        textsAnimator.setDuration((long) (duration * piece));


//        ObjectAnimator innerColorGradientAnimator = new ObjectAnimator();
//        innerColorGradientAnimator.setEvaluator(ARGB_EVALUATOR);
//
//        if (gradientInterpolator != null)
//            innerColorGradientAnimator.setInterpolator(gradientInterpolator);
//        innerColorGradientAnimator.setDuration((long) (duration * (1 - piece)));
//        ValueAnimator outColorGradientAnimator = innerColorGradientAnimator.clone();

//        innerColorGradientAnimator.setIntValues(innerCircleColor, innerCircleTargetColor);
//        innerColorGradientAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Integer value = (Integer) animation.getAnimatedValue();
//                mInnerFillCircle.setColor(value);
//            }
//        });
//
//        outColorGradientAnimator.setIntValues(outerCircleColor, outerCircleTargetColor);
//        outColorGradientAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mOuterFillCircle.setColor((Integer) animation.getAnimatedValue());
//            }
//        });


        animatorSet.play(textsAnimator).with(arcSweepAnimator);
        animatorSet.play(arcDegrees).after(arcSweepAnimator);
        animatorSet.play(arcDegrees).with(circleAnimator);
        animatorSet.play(progressAnimator).after(arcSweepAnimator);


//        animatorSet.play(innerColorGradientAnimator).after(arcSweepAnimator);

        TimeAnimator update = new TimeAnimator();
        update.setDuration(duration);
        update.setTimeListener(updateDrawListener);
        animatorSet.play(update);

        animatorSet.start();


    }

    private final TimeListener updateDrawListener = new TimeListener() {
        @Override
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            invalidate();
        }
    };

    private final static Comparator<MCircle> CIRCLE_COMPARATOR = new Comparator<MCircle>() {
        @Override
        public int compare(MCircle o1, MCircle o2) {
            return (int) (o1.getR() - o2.getR());
        }
    };

}
