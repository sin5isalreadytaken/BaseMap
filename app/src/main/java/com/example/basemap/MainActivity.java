package com.example.basemap;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.baidu.mapapi.map.MapView;


public class MainActivity extends Activity {
    public Bitmap ways;
    private WindowManager wm;
    private ImageView image;
    private Context ma = null;
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI= 6.28318530712; // 2*PI
    static double DEF_PI180= 0.01745329252; // PI/180.0
    static double DEF_R =6370693.5; // radius of earth
    public TextView show_position;
    public TextView show_distance;
    public Spinner spinner;
    private int flag = 1;
    private GeoPoint A;
    private GeoPoint B;
    private long distance = 0;
    public Button getPositionOnce;
    public Button pause;
    public Button stop;
    public double Latitude, Longitude;
    private static final String LTAG = MainActivity.class.getSimpleName();
    private MapView mMapView = null;
    private BaiduMap bdMap;
    private Bitmap position;
    private ImageView Blue;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_main);

        image = (ImageView) findViewById(R.id.imageView);
        Blue = (ImageView) findViewById(R.id.blue);
        show_position = (TextView) findViewById(R.id.show_position);
        show_distance = (TextView) findViewById(R.id.show_distance);
        spinner = (Spinner) findViewById(R.id.spinner);
        getPositionOnce = (Button) findViewById(R.id.control_button);
        pause = (Button) findViewById(R.id.pause_button);
        pause.setVisibility(View.GONE);
        stop = (Button) findViewById(R.id.stop_button);
        ma = this;
        mMapView = (MapView) findViewById(R.id.bmapview);
//        mMapView.setBuiltInZoomControls(true);
//        //设置在缩放动画过程中也显示overlay,默认为不绘制
//        mMapView.setDrawOverlayWhenZooming(true);
//        GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
        bdMap = mMapView.getMap();
        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bdMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(36.0, 103.73)));
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(17.5f);
        bdMap.animateMapStatus(u);
        drawWays();
        position = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();


        if (!this.isOPen(this)){
            this.openGPS(this);
        }
        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        getPositionOnce.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Blue.setImageBitmap(position);
                flag = 1;
                spinner.setEnabled(false);
                getPositionOnce.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                try {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                        A = new GeoPoint(Latitude, Longitude);
                        //graphicsOverlay.setData(drawPoint());
                        showLocation(location);
                    }
                } catch (SecurityException e) {
                    Toast.makeText(ma,"请打开GPS定位服务", Toast.LENGTH_SHORT).show();
                }

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {

                        public void onLocationChanged(Location location) {
                            // TODO Auto-generated method stub
                            showLocation(location);
                        }

                        public void onProviderDisabled(String provider) {
                            // TODO Auto-generated method stub
                            showLocation(null);
                        }

                        public void onProviderEnabled(String provider) {
                            // TODO Auto-generated method stub
                            try {
                                showLocation(locationManager.getLastKnownLocation(provider));
                            } catch (SecurityException e) {
                                ;
                            }
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            // TODO Auto-generated method stub
                        }

                    });
                } catch (SecurityException e) {
                    Toast.makeText(ma, "请打开GPS定位服务", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawWays();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                pause.setVisibility(View.GONE);
                getPositionOnce.setVisibility(View.VISIBLE);
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                flag = 1;
                pause.setVisibility(View.GONE);
                getPositionOnce.setVisibility(View.VISIBLE);
                spinner.setEnabled(true);
                String form = spinner.getSelectedItem().toString();
                double formnum = 0.001;
                if (form.equals("骑行")){
                    formnum = 0.00098;
                }
                else if (form.equals("公交")){
                    formnum = 0.0009;
                }
                else if (form.equals("地铁")){
                    formnum = 0.0008;
                }
                Intent i = new Intent(MainActivity.this, countView.class);
                i.putExtra("dis", (int)(distance * formnum));
                distance = 0;
                startActivity(i);
            }
        });
    }

//    public Graphic drawPoint() {
//        GeoPoint pt1 = A;
//
//        // 构建点
//        Geometry pointGeometry = new Geometry();
//        // 设置坐标
//        pointGeometry.setPoint(pt1, 10);
//        // 设定样式
//        Symbol pointSymbol = new Symbol();
//        Symbol.Color pointColor = pointSymbol.new Color();
//        pointColor.red = 0;
//        pointColor.green = 126;
//        pointColor.blue = 255;
//        pointColor.alpha = 255;
//        pointSymbol.setPointSymbol(pointColor);
//        // 生成Graphic对象
//        Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
//        return pointGraphic;
//    }

//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            if (location == null)
//                return;
//
//            locData.latitude = location.getLatitude();
//            locData.longitude = location.getLongitude();
//            // 如果不显示定位精度圈，将accuracy赋值为0即可
//            locData.accuracy = location.getRadius();
//            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
//            locData.direction = location.getDerect();
//            // 更新定位数据
//            myLocationOverlay.setData(locData);
//            // 更新图层数据执行刷新后生效
//            mMapView.refresh();
//            // 是手动触发请求或首次定位时，移动到定位点
//            // 移动地图到定位点
//            Log.d("LocationOverlay", "receive location, animate to it");
//            mMapController.animateTo(new GeoPoint(
//                    (int) (locData.latitude * 1e6),
//                    (int) (locData.longitude * 1e6)));
//        }
//
//        public void onReceivePoi(BDLocation poiLocation) {
//            if (poiLocation == null) {
//                return;
//            }
//        }
//    }

    public void drawWays(){
        String form = spinner.getSelectedItem().toString();
        wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ways = BitmapFactory.decodeResource(getResources(),R.drawable.way1);
        if (form.equals("骑行")){
            ways = BitmapFactory.decodeResource(getResources(),R.drawable.way2);
        }
        else if (form.equals("公交")){
            ways = BitmapFactory.decodeResource(getResources(),R.drawable.way3);
        }
        else if (form.equals("地铁")){
            ways = BitmapFactory.decodeResource(getResources(),R.drawable.way4);
        }
        image .setImageBitmap(ways);
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void showLocation(Location currentLocation){
        if(flag == 0){
            return;
        }
        if(currentLocation != null){
            StringBuffer sb = new StringBuffer(256);
            Latitude = currentLocation.getLatitude();
                Longitude = currentLocation.getLongitude();

                B = new GeoPoint(Latitude, Longitude);
                bdMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(Latitude, Longitude)));
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(17.5f);
                bdMap.animateMapStatus(u);
                distance += GetShortDistance(A.getLatitudeE6(), A.getLongitudeE6(), B.getLatitudeE6(), B.getLongitudeE6());
                A = B;
                //graphicsOverlay.setData(drawPoint());

                sb.append("Latitude: " + Latitude + "\n");
                sb.append("Longitude: " + Longitude);
                show_position.setText(sb.toString());
                show_distance.setText(distance + "");
        }
        else{
            show_position.setText("");
        }
    }

    public int GetShortDistance(double lon1, double lat1, double lon2, double lat2)
     {
          double ew1, ns1, ew2, ns2;
          double dx, dy, dew;
          double distance;
          // 角度转换为弧度
          ew1 = lon1 * DEF_PI180;
          ns1 = lat1 * DEF_PI180;
          ew2 = lon2 * DEF_PI180;
          ns2 = lat2 * DEF_PI180;
          // 经度差
          dew = ew1 - ew2;
          // 若跨东经和西经180 度，进行调整
          if (dew > DEF_PI)
              dew = DEF_2PI - dew;
          else if (dew < -DEF_PI)
              dew = DEF_2PI + dew;
          dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
          dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
          // 勾股定理求斜边长
          distance = Math.sqrt(dx * dx + dy * dy);
          return (int)distance;
     }

    @Override
    public void onDestroy(){
        if(mMapView != null){
            mMapView.onDestroy();
            mMapView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

