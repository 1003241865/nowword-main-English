package com.example.utils;

import android.content.Context;

import com.example.now_word.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * 从v3.0.0版本开始，图表中的标记(弹出视图)由IMarker接口表示
 */

public class MPChartMarkerView extends MarkerView {

    private ArrowTextView tvContent;

    /**
     * 构造函数。用自定义布局资源设置MarkerView。
     *
     * @param context
     * @param layoutResource 用于MarkerView的布局资源
     */
    public MPChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (ArrowTextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(StringUtils.double2String(ce.getHigh(), 2));
        } else {

            tvContent.setText(StringUtils.double2String(e.getY(), 2));
        }

        super.refreshContent(e, highlight);//必须加上该句话
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // 水平和垂直居中标记
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
