package com.example.yxb.selfindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * PACKAGE_NAME:com.example.yxb.selfindicator
 * FUNCTIONAL_DESCRIPTION
 * CREATE_BY:xiaobo
 * CREATE_TIME:2016/7/16
 * MODIFY_BY:
 */
public class ViewPagerIndicator extends LinearLayout{

    private Paint mPaint;//画笔

    private Path mPath;//三角形路径

    private int mTriangleWidth;//三角形底边宽度

    private int mTriangleHeight;//三角形高

    private static final float RADIO_TRIANGLE_WIDTH = 1/6F;//三角形底边占单个tab比例

    private int mInitTranslationX;//初始化三角形移动距离

    private int mTranslationX;//滑动时三角形的偏移距离

    private int mVisableTabCount;//可见的tab数

    private static final int COUNT_DEFAULE_TAB = 4;//设置默认的可见tab数量为4

    private static final int COLOR_TEXT_NORMAL = 0X77FFFFFF;

    private static final int COLOR_TEXT_HIGHTlIGHT = 0XFFFFFFFF;

    private List<String> mTittles;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取可见tab数量
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        //获得xml文件中的对应属性数量
        mVisableTabCount = array.getInt(R.styleable.ViewPagerIndicator_visiable_tab_count,COUNT_DEFAULE_TAB);
        if (mVisableTabCount < 0){
            mVisableTabCount = COUNT_DEFAULE_TAB;
        }

        array.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);      //抗锯齿
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);  //填充
        mPaint.setPathEffect(new CornerPathEffect(3)); //圆滑无尖角
    }

    //当控件尺寸发生改变时第一时间回调此方法，通过此方法设置宽高
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);//先执行构造方法
        
        mTriangleWidth = (int) (w / mVisableTabCount * RADIO_TRIANGLE_WIDTH);
        mTriangleHeight = mTriangleWidth / 2;
        mInitTranslationX = w / mVisableTabCount /2 - mTriangleWidth / 2;
        
        initTriangle();
    }



    //初始化三角形
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight + 5);
        mPath.close();
    }

    //绘制三角形

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();//先将画布保存

        canvas.translate(mInitTranslationX + mTranslationX, getHeight()); //移动到初始化位置,mTranslationX设置为动态的，方便重绘，此处为0
        canvas.drawPath(mPath,mPaint); //按路径绘制

        canvas.restore();//再次保存
        super.dispatchDraw(canvas);//构造方法放下
    }

    // 三角形跟随滑动而移动，此方法在viewPager滚动时调用
    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth() / mVisableTabCount;
        mTranslationX = (int) ((position + positionOffset) * tabWidth); //改变mTranslationX的值

        //让标题tab跟着滑动而移动
        //滑动到倒数第二个，且最好最多到倒数第三个，有一定的偏移量，总个数比最大可见数大时才移动tab画布
        if (position >= (mVisableTabCount - 2) && position < (getChildCount() - 2) && positionOffset > 0 && getChildCount() > mVisableTabCount){
            if (mVisableTabCount != 1){
                this.scrollTo((int) ( (position - (mVisableTabCount - 2)) * tabWidth + tabWidth * positionOffset), 0);
            }else{
                this.scrollTo((int) ( position * tabWidth + tabWidth * positionOffset), 0);
            }
        }

        invalidate();//重绘
    }

    //xml文件加载完后会回调此方法，在这里为子控件设置长宽
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();//写在第一行

        int cCount = getChildCount();
        if (cCount == 0) return;

        for (int i = 0; i < cCount; i ++){
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mVisableTabCount;
            view.setLayoutParams(lp);
        }

        setItemClickEvent();
    }
    //获得屏幕宽度
    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    //动态添加Tab
    public void setTabTittles(List<String> tittles){
        if (tittles != null && tittles.size() > 0){
            this.removeAllViews();//清除所有的子控件，一般都要有这一步，防止第一次点击无效
            mTittles = tittles;
            for (String tittle : mTittles){
                addView(generateTextView(tittle));
            }

            setItemClickEvent();
        }
    }

    private static final int COLOR_TAB_NORMAL = 0X77FFFFFF;
    //外部设置可见tab数量，必须在setTabTittles之前执行
    public void setVisableTabCount(int count){
        mVisableTabCount = count;
    }
    //根据tittle创建tab
    private View generateTextView(String tittle) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mVisableTabCount;
        textView.setLayoutParams(lp);
        textView.setText(tittle);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(COLOR_TAB_NORMAL);
        return textView;
    }
    //设置关联的Viewpager
    private ViewPager mViewPager;

    public interface pageOnChangeListener{
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void onPageSelected(int position);
        public void onPageScrollStateChanged(int state);
    }

    public pageOnChangeListener listener;
    public void setPageOnChangeListener(pageOnChangeListener listener){
        this.listener = listener;
    }

    public void setViewPager(ViewPager viewPager, int position){
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (listener != null){
                    listener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (listener != null){
                    listener.onPageSelected(position);
                }
                hightLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null){
                    listener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(position);
        hightLightTextView(position);
    }

    public void hightLightTextView(int position){
        View view = getChildAt(position);
        if (view instanceof TextView){
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHTlIGHT);
        }
        resetTextViewColor();
    }

    public void resetTextViewColor(){
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).setTextColor(COLOR_TEXT_HIGHTlIGHT);
            }
        }
    }

    //设置tab的点击事件
    public void setItemClickEvent(){
        for (int i = 0; i < getChildCount(); i++){
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

}
