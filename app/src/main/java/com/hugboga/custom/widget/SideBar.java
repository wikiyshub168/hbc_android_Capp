package com.hugboga.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.utils.Common;
import com.hugboga.custom.utils.UIUtils;


public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
    private String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private int choose = -1;// 选中

    private Paint paint = null;
    private TextView mTextDialog;
    private int singleHeight;
    private boolean isWrapContent = false;
    private int realityHeight = 0;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(Common.dpToPx(getResources(), 12));
        paint.setColor(getResources().getColor(R.color.common_font_color_gray));
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        if (singleHeight == 0) {
            if (isWrapContent) {
                singleHeight = (int) (paint.getFontMetrics().descent - paint.getFontMetrics().ascent) + UIUtils.dip2px(5);
                realityHeight = singleHeight * b.length;
            } else {
                singleHeight = height / b.length;// 获取每一个字母的高度
            }
        }
        for (int i = 0; i < b.length; i++) {
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            if (isWrapContent) {
                yPos += (getHeight() - realityHeight) / 2;
            }
            canvas.drawText(b[i], xPos, yPos, paint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        int c = 0;
        if (isWrapContent) {
            c = (int) ((y - (getHeight() - realityHeight)/2)/ realityHeight * b.length);
        } else {
            c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
//                setBackgroundColor(0x00000000);
                choose = -1;//
                paint.setColor(getResources().getColor(R.color.common_font_color_gray));
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
//                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        paint.setColor(getResources().getColor(R.color.common_font_color_gray));
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public void setHeightWrapContent(boolean _isWrapContent) {
        this.isWrapContent = _isWrapContent;
        singleHeight = 0;
        invalidate();
    }

    public void setLetter(String[] letter) {
        b = letter;
        invalidate();
    }

    public String[] getLetter() {
        return b;
    }

    public String[] getDefaultLetter() {
        String[] defaultLetter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        return defaultLetter;
    }
}