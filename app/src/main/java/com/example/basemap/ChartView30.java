package com.example.basemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
/**
 * Created by 森梧 on 2016/3/7.
 */
public class ChartView30 extends View{
    private float screen_width;
    private float screen_height;
    private int XPoint;
    private int YPoint;
    private int XScale;
    private int YScale;
    private int XLength;
    private int YLength;
    private String[] XLabel;
    private String[] YLabel;
    private String[] Data;
    private String Title;
    public ChartView30(Context context){
        super(context);
    }

    public void setInfo(String[] XLabels, String[] YLabels, String[] ALLDATA, String strTitle){
        XLabel = XLabels;
        YLabel = YLabels;
        Data = ALLDATA;
        Title = strTitle;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//重写onDraw方法
        screen_width = this.getWidth();
        screen_height = this.getHeight();
        XPoint = (int)screen_width / 10;
        YPoint = (int)screen_height * 4 / 5;
        XScale = (int)screen_width / 33;
        YScale = (int)screen_height / 10;
        XLength = (int)screen_width / 10 * 9;
        YLength = (int)screen_height / 10 * 7;
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(21);

        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);
        for(int i = 0; i * YScale < YLength; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);
            try {
                canvas.drawText(YLabel[i], XPoint - 50, YPoint - i * YScale + 10, paint);
            } catch (Exception e) {
            }
        }
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 5, YPoint - YLength + 10, paint);
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 5, YPoint - YLength + 10, paint);
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);
        for(int i = 0; i * XScale < XLength; i++){
            canvas.drawLine(XPoint + i * XScale, YPoint, XPoint + i * XScale, YPoint - 5, paint);
            try{
                if(i > 0 && YCoord(Data[i - 1]) != -999 && YCoord(Data[i]) != -999)
                    canvas.drawLine(XPoint + (i - 1) * XScale, YCoord(Data[i - 1]), XPoint + i * XScale, YCoord(Data[i]), paint);
                canvas.drawCircle(XPoint + i * XScale, YCoord(Data[i]), 3, paint);
            }catch (Exception e){}
        }
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength - 10, YPoint - 5,paint);    //箭头
        canvas.drawLine(XPoint + XLength, YPoint, XPoint+XLength - 10, YPoint + 5,paint);
        canvas.drawText(Title, screen_width / 5 * 2, screen_height / 10, paint);
    }

    private int YCoord(String y0){
        int y;
        try{
            y = Integer.parseInt(y0);
        }catch (Exception e){
            return -999;
        }
        try{
            return YPoint - y * YScale / Integer.parseInt(YLabel[1]);
        }catch (Exception e){}
        return y;
    }
}
