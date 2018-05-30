package com.example.tcy.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.tcy.bean.PieBean;
import com.example.tcy.utils.DxUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tcy on 2018/5/18.
 */

public class PieView extends View {

    private Context context;
    // 饼状图初始绘制角度
    private final float DEFAULT_START_ANGLE = 0;
//    private float mStartAngle = 0;
    private float angleValue =0;
    // 扇形画笔
    private Paint mPaint = new Paint();
    //中间圆画笔
    private Paint circlePaint = new Paint();
    //中心阴影画笔
    private Paint arPaint = new Paint();

    //文字画笔
    private Paint textPaint = new Paint();
    //分割线画笔
    private Paint linePaint = new Paint();
    //中间文字画笔
    private Paint centerTextPaint = new Paint();
    // 数据
    private ArrayList<PieBean> mData;
    //数据颜色
    private ArrayList<Integer> mColors;
    //中间圆半径
    private float cir;
    private int centerCir = 0;
    private String centerText;

    private float centerX;
    private float mViewWidth;
    private float mViewHeight;
    private float centerY;

    private int textColor;
    private int centerTextColor;
    private int lineColor;
    private boolean showAnimation=true;
    private boolean isDrawLine = true;
    private boolean isPercentageShow = true;
    private boolean shadow= true;
    private boolean touchSoll = true;
    private boolean touchCarve = true;

    //饼图半径
    private float radius;

    //自定义动画
    private ValueAnimator animator;
    private ValueAnimator animatorTouch;

    //动画时间
    private static final long ANIMATION_DURATION = 1000;

    private Path centerP;
    private Region centerR;
    private Region globalRegion;

    private ClickListener listener;
    private int selectArcPostion=-1;
    private int animationPostion = -1;
    private float curtFraction = 1f;
    private float curtFractionTouch = 1f;
    private float curtFractionTouch2 = 0f;

    private  float allValue; //总数

    private List<Float> angleList;

    public PieView(Context context) {
        super(context);
        this.context = context;
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        angleList = new ArrayList<>();

        mPaint.setStyle(Paint.Style.FILL);//设置画笔模式为填充
        mPaint.setAntiAlias(true);//设置抗锯齿
        circlePaint.setStyle(Paint.Style.FILL);//设置画笔模式为填充
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);

        arPaint.setStyle(Paint.Style.FILL);//设置画笔模式为填充
        arPaint.setAntiAlias(true);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);//设置防抖动

        centerTextPaint.setStyle(Paint.Style.FILL);
        centerTextPaint.setAntiAlias(true);
        centerTextPaint.setDither(true);//设置防抖动
        centerTextPaint.setColor(Color.BLACK);


        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.WHITE);


        animator = ValueAnimator.ofFloat(0, 1f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curtFraction = animation.getAnimatedFraction();
                angleValue = DEFAULT_START_ANGLE;
                invalidate();
            }
        });


        animatorTouch = ValueAnimator.ofFloat(1f, 1.07f);
        animatorTouch.setDuration(200);
        animatorTouch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curtFractionTouch = (Float) animation.getAnimatedValue();
                curtFractionTouch2 = 0.03f * animation.getAnimatedFraction();
                invalidate();
            }

        });


        centerP = new Path();
        centerR = new Region();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = Math.min(heightSpecSize, Math.min(DxUtil.getScreenSize(context)[0], DxUtil.getScreenSize(context)[1]));
        } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = Math.min(widthSpecSize, Math.min(DxUtil.getScreenSize(context)[0], DxUtil.getScreenSize(context)[1]));
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = height = Math.min(DxUtil.getScreenSize(context)[0], DxUtil.getScreenSize(context)[1]);
        } else {
            width = widthSpecSize;
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight=h;
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX, centerY) * 0.725f;
        if(centerCir==1){
            cir = radius/2+radius/8;
        }else {
            cir = radius/centerCir;
        }
        globalRegion = new Region(-w, -h, w, h);
        centerTextPaint.setTextSize(radius/8);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mData||mData.size()==0)
            return;
        //画布背景
        canvas.drawColor(Color.WHITE);
//        canvas.translate(mViewWidth/2, mViewHeight/2);
        RectF rect = new RectF(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius);//一个整圆的大小矩形
//       float  currentStartAngle = mStartAngle;                    // 当前起始角度

        for (int i = 0; i < mData.size(); i++) {
            PieBean pie = mData.get(i);
            mPaint.setColor(pie.getColor());
            float mAngle = 360  *curtFraction* pie.getValue() / allValue;
            if(animationPostion>=0&&i==animationPostion){

                float mRadiusTemp = radius * curtFractionTouch;
                canvas.drawArc(centerX - mRadiusTemp, centerY - mRadiusTemp, centerX + mRadiusTemp,
                        centerY + mRadiusTemp, angleValue + mAngle * curtFractionTouch2,
                        (mAngle - mAngle * curtFractionTouch2 * 2)-1, true, mPaint);
            }else {
                canvas.drawArc(rect, angleValue, mAngle, true, mPaint);
            }

            pie.setCutAngle(angleValue);

            Path arcP = new Path();
            Region arcR = new Region();
            arcR.set((int)(centerX - radius), (int)(centerY - radius),(int)(centerX + radius), (int)(centerY + radius));

            //每次都移动操作的起点到圆心点
            arcP.moveTo(centerX, centerY);
            float cx = mAngle-angleValue;
            float nx = (float) (Math.sin(cx)*radius);
            float ny = (float) Math.sqrt(radius*radius+nx*nx);
            arcP.lineTo(nx, ny);//原点到弧度的起点之间得线
            arcP.addArc(rect,angleValue,mAngle);//画圆弧
            arcP.lineTo(centerX, centerY);//画完圆弧后移动到圆心起点的线
            arcR.setPath(arcP,arcR);
            pie.setRegion(arcR);

                //计算扇形中心点的坐标
                float textAngle = angleValue+  mAngle / 2;  //计算文字位置角度
                float textPointX = (float) (centerX + (radius*0.8) * Math.cos(Math.toRadians(textAngle)));
                float textPointY = (float) (centerY + (radius*0.8) * Math.sin(Math.toRadians(textAngle)));
                PointF pointF =   new PointF(textPointX, textPointY);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setColor(textColor);
                textPaint.setTextSize(radius / 9);
                //如果角度大于15度 画文字
                if(pie.getAngle()>15&&isPercentageShow){
                    canvas.drawText(pie.getPercentage(), pointF.x,  pointF.y, textPaint);
                    //获取字体高度
                    Paint.FontMetrics fm = textPaint.getFontMetrics();
                    float textH = fm.descent - fm.ascent;
                    canvas.drawText(pie.getName(), pointF.x,  pointF.y+ textH, textPaint);
                }

               if(isDrawLine){
                   linePaint.setColor(lineColor);
                   if(i==mData.size()-1){
                       linePaint.setStrokeWidth((float) 5.0);
                   }else {
                       linePaint.setStrokeWidth((float) 10.0);//设置线宽
                   }
                   float startX = rect.centerX();
                   float startY = rect.centerY();
                   float endX = (float) (centerX + (radius)* Math.cos(Math.toRadians(angleValue+  mAngle)));
                   float endY = (float) (centerY + (radius)* Math.sin(Math.toRadians(angleValue +  mAngle)));
                   canvas.drawLine(startX, startY, endX, endY, linePaint);
               }
//            currentStartAngle += pie.getAngle();
            angleValue+=mAngle;
        }

        centerP.addCircle(rect.centerX(), rect.centerY(),cir,Path.Direction.CW);
        centerR.setPath(centerP,globalRegion);
        if(cir>0&&shadow){
            //绘制中心阴影
            arPaint.setColor(0x33000000);
            canvas.drawCircle(rect.centerX(), rect.centerY(),cir+20,arPaint);
        }
        //画一个半径为扇形圆的半径的四分之1的圆
        canvas.drawCircle(rect.centerX(), rect.centerY(),cir,circlePaint);

        //绘制中间文字
        if(!TextUtils.isEmpty(centerText)&&centerCir<5&&centerCir>=1){
            centerTextPaint.setColor(centerTextColor);
            Paint.FontMetrics fm = centerTextPaint.getFontMetrics();
            // 计算文字高度
            float fontHeight = fm.descent - fm.ascent;
            //获取字体的长度
            float fontWidth = centerTextPaint.measureText(centerText);
            canvas.drawText(centerText, rect.centerX()-fontWidth/2, (float) (rect.centerY()+fontHeight/2.5), centerTextPaint);
        }
        super.onDraw(canvas);
    }


    // 设置数据
    public void setData(ArrayList<PieBean> mData) {
        this.mData = mData;
        initData(mData);
        if(showAnimation){
            animator.start();
        }
    }

    public void setCenterCir(int centerCir){
        if(centerCir>10){
            centerCir= 10;
        }
        this.centerCir =centerCir;
    }

    public void  setCenterText(String centerText) {
            if(centerText.length()<5){
                this.centerText = centerText;
            }else {
                this.centerText = centerText.substring(0,4)+"...";
            }
    }

    public void setmColors(ArrayList<Integer> mColors){
        this.mColors = mColors;
    }

    public void setTextColor(int textColor){
        this.textColor = textColor;
    }

    public void setCenterTextColor(int color){
        this.centerTextColor = color;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setShowAnimation(boolean showAnimation){
        this.showAnimation =showAnimation;
    }

    public void setDrawLine(boolean drawLine) {
        isDrawLine = drawLine;
    }

    public void setPercentageTextShow(boolean isPercentageShow){
        this.isPercentageShow = isPercentageShow;
    }

    public void isShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setTouchStart(boolean touchStart) {
        this.touchSoll = touchStart;
    }

    public void setTouchCarve(boolean touchCarve) {
        this.touchCarve = touchCarve;
    }

    private void initData(ArrayList<PieBean> mData){
        if(mData==null||mData.size()==0){
            return;
        }
        float sumValue = 0;
        for (int i = 0; i <mData.size() ; i++) {
            PieBean pieBean =  mData.get(i);
            pieBean.setColor(ContextCompat.getColor(context, mColors.get(i)));
            sumValue += pieBean.getValue();       //计算数值和
        }
        allValue = sumValue;

        for (int i = 0; i <mData.size(); i++) {
            PieBean pieBean =  mData.get(i);
            NumberFormat nt = NumberFormat.getPercentInstance();
            //设置百分数精确度2即保留两位小数
            nt.setMinimumFractionDigits(0);
            float percentage = pieBean.getValue() / allValue;
            float angle = percentage * 360;                 // 对应的角度
            pieBean.setPercentage(nt.format(percentage));   // 百分比
            pieBean.setAngle(angle);
        }


        float angleTemp;
        float startAngleTemp = DEFAULT_START_ANGLE;
        for (int i = 0; i <mData.size(); i++){
            angleTemp = 360 * mData.get(i).getValue() / allValue;
            angleList.add(startAngleTemp + angleTemp / 2);
            startAngleTemp += angleTemp;
        }

    }


    // 获取当前触摸点在哪个区域
    int getTouchedPath(int x, int y) {
        if (centerR.contains(x, y)) {
            return 0;
        } else if (selectArcPostion>=0&&mData.get(selectArcPostion).getRegion().contains(x, y)) {
            return 1;
        }
        return -1;
    }

    int touchFlag = -1;
    int currentFlag = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float mx = x - centerX;
                float my = y - centerY;
                float result = mx * mx + my * my;
                if(result <= radius * radius) {
                    for (int i = 0; i <mData.size() ; i++) {
                        PieBean pieBean = mData.get(i);
                        if (pieBean.getRegion().contains(x,y)){
                            if(listener!=null&&!animator.isRunning()&&!animatorTouch.isRunning()){
                                selectArcPostion = i;
                                animationPostion = i;
                            }
                        }
                    }
                }
                touchFlag = getTouchedPath(x, y);
                currentFlag = touchFlag;
                break;
            case MotionEvent.ACTION_MOVE:
                currentFlag = getTouchedPath(x, y);
                break;

            case MotionEvent.ACTION_UP:

                currentFlag = getTouchedPath(x, y);
                // 如果手指按下区域和抬起区域相同 为单击事件
                if (currentFlag == touchFlag && currentFlag != -1){
                    // 点击区域判断
                    if (centerR.contains(x,y)){
                        if(listener!=null){
                            listener.onCenterClick();
                            return true;
                        }
                    }

                    if(selectArcPostion>=0&&!animator.isRunning()&&!animatorTouch.isRunning()){
                        if(!mData.get(selectArcPostion).isCarve()){
                            listener.onArcClick(animationPostion);
                            curtFractionTouch = 1f;
                            curtFractionTouch2 = 0f;
                            final float angle = getRotationAngle(animationPostion);
                            ValueAnimator animatorRotation;
                            animatorRotation = ValueAnimator.ofFloat(angleValue, angleValue + angle);
                            animatorRotation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    angleValue = (Float) animation.getAnimatedValue();
                                    invalidate();
                                }
                            });
                            int time = (int) (1000 * Math.abs(angle) / 360);
                            if(touchSoll){
                                animatorRotation.setDuration(time);
                                animatorRotation.start();
                            }
                            if(touchCarve){

                                animatorTouch.setStartDelay(time);
                                animatorTouch.start();
                            }
                            mData.get(selectArcPostion).setCarve(true);
                            for (int i = 0; i <mData.size() ; i++) {
                                 if(i!=selectArcPostion){
                                     mData.get(i).setCarve(false);
                                 }
                            }
                            selectArcPostion = -1;

                        }
                        return true;
                    }

                }

                touchFlag = currentFlag = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                touchFlag = currentFlag = -1;
                break;

        }
        return true;
    }

    private float getRotationAngle(int i) {
        float angleR;
        float angleT = angleList.get(i);
        if (angleT <= 270f && angleT >= 90f) {
            angleR = 88f - angleT;
        } else if (angleT > 270f && angleT <= 360f) {
            angleR = 360f - angleT + 90f;
        } else if (angleT >= 0 && angleT < 90) {
            angleR = 90 - angleT;
        } else {
            angleR = 0;
        }

        for (int id = 0; id < angleList.size(); id++) {
            float temp = angleList.get(id) + angleR;
            if (temp > 360f) {
                temp -= 360f;
            } else if (temp < 0) {
                temp += 360f;
            }
            angleList.set(id, temp);
        }
        return angleR;
    }



    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    // 点击事件监听器
    public interface ClickListener {
        void onCenterClick();
        void onArcClick(int i);
    }


}
