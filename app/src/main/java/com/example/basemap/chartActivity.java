package com.example.basemap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.basemap.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by 森梧 on 2016/3/7.
 */
public class chartActivity extends Activity{
    private String[] date7 = new String[7], point7 = new String[7], x7 = new String[7], date30 = new String[30], point30 = new String[30], x30 = new String[7];

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chart);

        Context cv = chartActivity.this;
        SharedPreferences preferences = getSharedPreferences("cv", Context.MODE_PRIVATE);
        Date day7 = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat cal = new SimpleDateFormat("yy-MM-dd");
        int point = 0, pointMax = 0;
        String temp = "";
        for(int i = 0 ; i < 7; i++){
            Date theday = new Date(day7.getTime() - i * 24 * 60 * 60 *1000);
            temp = cal.format(theday);
            point = preferences.getInt(temp, 0);
            temp = dateFormat.format(theday);
            pointMax = (pointMax > point) ? pointMax : point;
            date7[6 - i] = temp;
            point7[6 - i] = point + "";
        }
        for(point = pointMax; point % 7 != 0; point++);
        x7[0] = "";
        for(int i = 1; i < 7; i ++){
            if(point == 0){
                x7[i] = "";
                x7[1] = "1";
            }
            else
                x7[i] = (point / 7 * i) + "";
        }
        point = 0;
        pointMax = 0;
        for(int i = 0 ; i < 30; i++){
            Date theday = new Date(day7.getTime() - i * 24 * 60 * 60 *1000);
            temp = cal.format(theday);
            point = preferences.getInt(temp, 0);
            temp = dateFormat.format(theday);
            pointMax = (pointMax > point) ? pointMax : point;
            date30[29 - i] = temp;
            point30[29 - i] = point + "";
        }
        for(point = pointMax; point % 7 != 0; point++);
        x30[0] = "";
        for(int i = 1; i < 7; i ++){
            if(point == 0){
                x30[i] = "";
                x30[1] = "1";
            }
            else
                x30[i] = (point / 7 * i) + "";
        }

        LinearLayout layout1 = (LinearLayout)findViewById(R.id.charViewLO);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.charView30LO);

        com.example.basemap.ChartView lineView1 = new com.example.basemap.ChartView(this);
        com.example.basemap.ChartView30 lineView2 = new com.example.basemap.ChartView30(this);
        lineView1.setInfo(date7, x7, point7, "最近一周折线图");
        lineView2.setInfo(date30, x30, point30, "最近一月折线图");
        layout1.addView(lineView1);
        layout2.addView(lineView2);
    }
}
