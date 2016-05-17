package com.example.basemap;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ServiceConfigurationError;

/**
 * Created by 森梧 on 2016/3/4.
 */
public class countView extends Activity {
    private Context mContext = null;
    public TextView score;
    public TextView point;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.countview);
        mContext = this;
        Intent c = getIntent();
        score = (TextView) findViewById(R.id.thisScoreNum);
        Bundle bundle = c.getExtras();
        int scoreTemp = bundle.getInt("dis");
        score.setText(scoreTemp + "");
        Context cv = countView.this;
        SharedPreferences preferences = getSharedPreferences("cv",Context.MODE_PRIVATE);
        int points = preferences.getInt("points", 0) + scoreTemp;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("points", points);
        editor.commit();
        point = (TextView) findViewById(R.id.allScoreNum);
        point.setText(points + "");

        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        String todayStr = dateFormat.format(today);
        int todayPoint = preferences.getInt(todayStr, 0) + scoreTemp;
        editor.putInt(todayStr, todayPoint);
        editor.commit();

        Button auction = (Button) findViewById(R.id.auction_button);
        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "暂无此功能", Toast.LENGTH_SHORT).show();
                //showPopupWindow(v);
            }
        });
        Button check = (Button) findViewById(R.id.check_button);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(countView.this, chartActivity.class);
                startActivity(i2);
            }
        });
//        Button clear = (Button) findViewById(R.id.clear_button);
//        clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences preferences = getSharedPreferences("cv",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putInt("points", 0);
//                editor.commit();
//                score.setText("0");
//                point.setText("0");
//            }
//        });
    }

    private void showPopupWindow(View view){
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.thx, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,true);

        Button thx = (Button) contentView.findViewById(R.id.thx_button);
        thx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button call = (Button) contentView.findViewById(R.id.call_button);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("cv",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("points",0);
                editor.commit();
                point.setText("0");

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:18189531369"));
                try {
                    startActivity(intent);
                }catch (SecurityException e){
                    Toast.makeText(mContext, "不支持拨号功能", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("mengdd", "onTouch:");
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank));
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int a = popupWindow.getWidth();
        int b = popupWindow.getHeight();
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWindow.getContentView().getMeasuredWidth() / 2, location[1] - popupWindow.getContentView().getMeasuredHeight());
    }
}
