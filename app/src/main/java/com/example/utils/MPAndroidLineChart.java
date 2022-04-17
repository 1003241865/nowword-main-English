package com.example.utils;

import android.graphics.Color;

import com.example.now_word.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class MPAndroidLineChart {
    public static final int[] LINE_COLORS = {
            Color.rgb(140, 210, 118), Color.rgb(159, 143, 186), Color.rgb(233, 197, 23)
    };//绿色，紫色，黄色

    public static final int[] LINE_FILL_COLORS = {
            Color.rgb(222, 239, 228), Color.rgb(246, 234, 208), Color.rgb(235, 228, 248)
    };

    /**
     * 单线单y轴图。
     *
     * @param lineChart
     * @param xAxisValue
     * @param yAxisValue
     * @param title
     * @param showSetValues 是否在折线上显示数据集的值。true为显示，此时y轴上的数值不可见，否则相反。
     */
    public static void setLineChart(LineChart lineChart, List<String> xAxisValue, List<Float> yAxisValue, String title, boolean showSetValues) {
        List<List<Float>> entriesList = new ArrayList<>();
        entriesList.add(yAxisValue);

        List<String> titles = new ArrayList<>();
        titles.add(title);

        setLinesChart(lineChart, xAxisValue, entriesList, titles, showSetValues,null);
    }

    /**
     * 绘制线图，默认最多绘制三种颜色。所有线均依赖左侧y轴显示。
     *
     * @param lineChart
     * @param xAxisValue x轴的值
     * @param yXAxisValues y轴的值
     * @param titles 每一个数据系列的标题
     * @param showSetValues 是否在折线上显示数据集的值。true为显示，此时y轴上的数值不可见，否则相反。
     * @param lineColors 线的颜色数组。为null时取默认颜色，此时最多绘制三种颜色。
     */
    public static void setLinesChart(LineChart lineChart, List<String> xAxisValue, List<List<Float>> yXAxisValues, List<String> titles, boolean showSetValues, int[] lineColors) {
        lineChart.getDescription().setEnabled(false);//设置描述
        lineChart.setPinchZoom(true);//设置按比例放缩柱状图
        //设置是否可以拖拽
        lineChart.setDragEnabled(false);
        //设置是否可以缩放 x和y，默认true
        lineChart.setScaleEnabled(false);
        //设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setDoubleTapToZoomEnabled(false);




        MPChartMarkerView markerView = new MPChartMarkerView(lineChart.getContext(), R.layout.custom_marker_view);
        lineChart.setMarker(markerView);

        //x坐标轴设置
        IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValue);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisValue.size());
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextSize(7f);
        xAxis.setValueFormatter(xAxisFormatter);

        //y轴设置
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);//折线上显示值，则不显示坐标轴上的值
        //leftAxis.setDrawZeroLine(true);
        /*leftAxis.setAxisMinimum(0f);*/
        /*leftAxis.setAxisLineWidth(2f);*/

        lineChart.getAxisRight().setEnabled(false);

        //图例设置
        Legend legend = lineChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);

        //设置折线图数据
        setLinesChartData(lineChart, yXAxisValues, titles, showSetValues,lineColors);

        lineChart.setExtraOffsets(10, 30, 20, 10);
        lineChart.animateX(1500);//数据显示动画，从左往右依次显示
    }

    private static void setLinesChartData(LineChart lineChart, List<List<Float>> yXAxisValues, List<String> titles, boolean showSetValues, int[] lineColors) {

        List<List<Entry>> entriesList = new ArrayList<>();
        for (int i = 0; i < yXAxisValues.size(); ++i) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0, n = yXAxisValues.get(i).size(); j < n; j++) {
                entries.add(new Entry(j, yXAxisValues.get(i).get(j)));
            }
            entriesList.add(entries);
        }

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {

            for (int i = 0; i < lineChart.getData().getDataSetCount(); ++i) {
                LineDataSet set = (LineDataSet) lineChart.getData().getDataSetByIndex(i);
                set.setValues(entriesList.get(i));
                set.setLabel(titles.get(i));
            }

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            for (int i = 0; i < entriesList.size(); ++i) {
                LineDataSet set = new LineDataSet(entriesList.get(i), titles.get(i));
                if(lineColors!=null){
                    set.setColor(lineColors[i % entriesList.size()]);
                    set.setCircleColor(lineColors[i % entriesList.size()]);
                    set.setCircleColorHole(Color.WHITE);
                    // 设置线的宽度
                    set.setLineWidth(3f);
                } else {
                    set.setColor(LINE_COLORS[i % 3]);
                    set.setCircleColor(LINE_COLORS[i % 3]);
                    set.setCircleColorHole(Color.WHITE);
                    set.setLineWidth(3f);
                }

                if (entriesList.size() == 1) {
                    set.setDrawFilled(true);
                    set.setFillColor(LINE_FILL_COLORS[i % 3]);
                    set.setLineWidth(3f);
                }
                dataSets.add(set);
            }

            LineData data = new LineData(dataSets);
            if (showSetValues) {
                data.setValueTextSize(10f);
                data.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        return StringUtils.double2String(value, 1);
                    }
                });
            } else {
                data.setDrawValues(false);
            }
            lineChart.setData(data);
        }
    }

}
