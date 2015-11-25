package com.code.bmj.myview;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class notificationListViewForScrollView extends ListView {

    public int index;

    public notificationListViewForScrollView(Context context) {
        super(context);
    }
    public notificationListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public notificationListViewForScrollView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}