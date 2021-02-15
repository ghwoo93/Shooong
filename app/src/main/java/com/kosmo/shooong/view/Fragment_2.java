package com.kosmo.shooong.view;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kosmo.shooong.MainActivity;
import com.kosmo.shooong.R;
import com.kosmo.shooong.utils.FileUploadUtils;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
//요구사항 : 위젯 연동(위젯은 혼자


//과제 : 켤때부터 스탭수 받아오기
//1]Fragement상속
//※androidx.fragment.app.Fragment 상속
public class Fragment_2 extends Fragment implements SensorEventListener, OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private LocationEngine locationEngine;
    protected LocationManager locationManager;
    private static int mSteps = 0;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private int mCounterSteps = 0;
    JsonArray coordinates;
    JsonArray coordinates_;
    List<LatLng> lineLatLngList;
    List<Point> pointsList;
    Button recordRoute, uploadRoute;
    boolean locflag = false;
    double speed, deltime; //X_, Y_;
    private MapView mapView;
    public MapboxMap mapboxMap;
    TextView nowspeed;
    //TextView tvStepCount, nowspeed;
    LocationChange loc = new LocationChange(this);
    File jsonFile;
    long startTime,endTime;
    private Fragment_2 f2;
    private Bundle bundle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("com.kosmo.shooong", "onAttach:2");
    }

    //map의 asynctask
    private class NaviOnMapReadyCallback implements OnMapReadyCallback{
        @Override
        public void onMapReady(@NonNull final MapboxMap mapboxMap) {
            Fragment_2.this.mapboxMap = mapboxMap;

            mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                    localizationPlugin.setMapLanguage(Locale.KOREA);
                    Layer settlementLabelLayer = style.getLayer("settlement-label");
                    settlementLabelLayer.setProperties(textField("{name_korea}"));
                    initCoordinates();
                    enableLocationComponent(style);
                    /*
                    //인텐트로 파일넘기기기
                    if(bundle.getString("courseId")!=null){
                        String courseId = bundle.getString("courseId");
                        Log.i("com.kosmo.shooong","getBundle:"+courseId);
                    }
                     */

                    String filepath = "/data/data/com.kosmo.shooong/files/upload/recordsample7.json";
                    File json = new File(filepath);

                    BufferedReader br = null;
                    StringBuffer sb = null;
                    try {
                        br = new BufferedReader(
                                new InputStreamReader(new FileInputStream(json)));

                        sb = new StringBuffer();

                        int data = -1;
                        char[] chars = new char[1024];

                        while ((data = br.read(chars)) != -1) {
                            sb.append(chars, 0, data);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    GeoJsonSource geoJsonSource = new GeoJsonSource("geojson-source", sb.toString());

                    //Log.i("com.kosmo.shooong", sb.toString());
                    style.addSource(geoJsonSource);
                    style.addLayer(new LineLayer("geojson-source", "geojson-source").withProperties(
                            lineColor(ColorUtils.colorToRgbaString(Color.parseColor("#3bb2d0"))),
                            lineWidth(4f)
                    ));
                }
            });
        }
        private void initCoordinates() {
            lineLatLngList = new ArrayList<>();
            pointsList = new ArrayList<>();
            coordinates = new JsonArray();
            coordinates_ = new JsonArray();
            coordinates.add(coordinates_);
        }

        @SuppressWarnings({"MissingPermission"})
        private void enableLocationComponent(@NonNull Style loadedMapStyle) {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
                // Get an instance of the component
                LocationComponent locationComponent = mapboxMap.getLocationComponent();
                // Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);
            } else {
                permissionsManager = new PermissionsManager(Fragment_2.this);
                permissionsManager.requestLocationPermissions(getActivity());
            }
        }
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("com.kosmo.shooong", "onCreateView:2");
        //위젯 활성화
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        /*
        bundle = savedInstanceState;
        bundle.putString("courseId","0");
         */
        f2 = Fragment_2.this;

        //레이아웃 전개]
        final View view = inflater.inflate(R.layout.tablayout_2, null, false);
        nowspeed = view.findViewById(R.id.nowSpeed);
        //tvStepCount = view.findViewById(R.id.textviewstep);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); // 0부터 세고싶으면  TYPE_STEP_COUNTER
        if (stepCountSensor == null) {
            Toast.makeText(getContext(), "만보기를 지원하지 않는 기기입니다", Toast.LENGTH_SHORT).show();
        }
        mCounterSteps = 0;
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new NaviOnMapReadyCallback());

        recordRoute = view.findViewById(R.id.route_record);
        uploadRoute = view.findViewById(R.id.route_upload);

        //버튼에 리스너 부착
        recordRoute.setOnClickListener(recordListener);
        uploadRoute.setOnClickListener(uploadListener);
        uploadRoute.setEnabled(false);

        //Feature feature = Feature.fromJson(sb.toString());

        //mapboxMap.getStyle().addSource(new GeoJsonSource("geojson-source",sb.toString()));
        //addLine("rawLine",feature.get,"#3bb2d0");
        return view;
    }///////////////onCreateView

    private View.OnClickListener uploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ShoongAsyncTask().execute(
                    "http://192.168.0.15:8080/shoong/record/upload/json");
        }
    };

    private class ShoongAsyncTask extends AsyncTask<String,Void,String>{

        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            //프로그래스바용 다이얼로그 생성]
            //빌더 생성 및 다이얼로그창 설정
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setView(R.layout.progress);
            builder.setIcon(android.R.drawable.ic_menu_compass);
            builder.setTitle("업로드");

            //빌더로 다이얼로그창 생성
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String[] params) {
            Log.i("com.kosmo.shoong","파일 업로드 전");
            FileUploadUtils.send2Server(jsonFile,params[0]);
            Log.i("com.kosmo.shoong","파일 업로드 후");
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //서버로부터 받은 데이타(JSON형식) 파싱
            //회원이 아닌 경우 빈 문자열
            //Log.i("com.kosmo.shoong","result:"+result);
            if(result !=null && result.length()!=0) {//회원인 경우
                try {
                    JSONObject json = new JSONObject(result);
                    String name = json.getString("userName");
                    //finish()불필요-NO_HISTORY로 설정했기때문에(매니페스트에서)
                }
                catch(Exception e){e.printStackTrace();}

            }

            //다이얼로그 닫기
            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    //버튼 이벤트 처리]
    private View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ShooongAsyncTask(f2).execute();
        }
    };//////////////////OnClickListener

    //PolyLine Draw & GeoJson IO 스레드 정의
    private class ShooongAsyncTask extends AsyncTask<String,Void,String>{

        private WeakReference<Fragment_2> weakReference;

        ShooongAsyncTask(Fragment_2 activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            locflag = !locflag;

            if(locflag) {
                recordRoute.setText("측정 종료하기");
                startTime = System.currentTimeMillis();
                uploadRoute.setEnabled(false);
            } else {
                recordRoute.setText("측정 시작하기");
                endTime = System.currentTimeMillis();
                uploadRoute.setEnabled(true);

                //지오제이슨 생성
                try {
                    setGeoJson();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            double latitude;
            double longitude;

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "권한이 수락되지 않았습니다. 다시 시도해주세요", Toast.LENGTH_LONG).show();
                return null;
            }
            Location loctemp =  mapboxMap.getLocationComponent().getLastKnownLocation();
            latitude = loctemp.getLatitude();
            longitude = loctemp.getLongitude();
            Log.i("com.kosmo.gps", "latlng " + latitude +" - "+longitude);

            double distance = 0.0;
            while (locflag) { // 스레드
                try {
                    Thread.sleep(1000); // 2초간 Thread 휴식
                    Location location = mapboxMap.getLocationComponent().getLastKnownLocation();
                    float locToMeter = location.distanceTo(loctemp); //2초전에 저장한 위치랑 현재위치 비교해서 m로 반환함
                    distance = distance + locToMeter;
                    //Log.i("com.kosmo.shooong", "loctoMe " + locToMeter);
                    if(speed>1&&speed<180)//움직이지 않거나 GPS 신호가 잡히지 않을때는 speed 고정
                        nowspeed.setText(String.format(" %.1f km/s\n %d m 이동" ,(float)speed, (int)distance));
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.i("com.kosmo.gps", "latlng " + latitude +" - "+longitude);

                    speed = locToMeter*1.8; //2초에 한번씩 불러옴

                    lineLatLngList.add(new LatLng(latitude, longitude)); //선을 연결할 좌표를 추가해줌
                    JsonArray nowLatLng = new JsonArray();
                    nowLatLng.add(longitude);
                    nowLatLng.add(latitude);

                    //해야할일
                    /*
                    포인트 리스트를 합칠지
                     */

                    boolean flag = pointsList.add(Point.fromLngLat(longitude,latitude));
                    Log.i("com.kosmo.shoong", flag?"들어감":"안들어감");
                    for(int i=0;i<pointsList.size();i++)
                        Log.i("com.kosmo.shoong", pointsList.get(i).toString());
                    coordinates_.add(nowLatLng);
                    mHandler.sendEmptyMessage(200); //지도에 선을 그어주는 함수 추가
                    deltime =  System.currentTimeMillis();
                    loctemp = location; // 위치 저장해서 2초뒤에 비교할거
                } catch (Exception e) {
                    Log.i("com.kosmo.shooong", "에러 발생 " + e);
                }
            }
            SystemClock.sleep(1000);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            //doInBackground()메소드 완료 후 처리할 코드
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //UI 변경 - DrawLine
            super.onProgressUpdate(values);
        }
    }/////////////////ShooongAsyncTask
    /*
    private void drawLines(@NonNull FeatureCollection featureCollection) {
        List<Feature> features = featureCollection.features();
        if (features != null && features.size() > 0) {
            Feature feature = features.get(0);
            drawSimplify(feature);
        }
    }

    private void drawSimplify(@NonNull Feature feature) {
        //List<Point> points = ((LineString) Objects.requireNonNull(feature.geometry())).coordinates();
        List<Point> points =
        addLine("simplifiedLine", Feature.fromGeometry(LineString.fromLngLats(points)), "#3bb2d0");
    }
    */
    private void addLine(String layerId, Feature feature, String lineColorHex) {
        mapboxMap.getStyle(style -> {
            style.addSource(new GeoJsonSource(layerId, feature));
            style.addLayer(new LineLayer(layerId, layerId).withProperties(
                    lineColor(ColorUtils.colorToRgbaString(Color.parseColor(lineColorHex))),
                    lineWidth(4f)
            ));
        });
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (locflag){
                switch (msg.what) {
                    case 200:
                        Log.d("com.kosmo.shooong", "선그리기요청");

                        mapboxMap.addPolyline(new PolylineOptions()
                                .addAll(lineLatLngList)
                                .color(Color.parseColor("#24d900"))
                                .width(3));
                        break;
                }
            }else{
                for(Polyline p : mapboxMap.getPolylines()){
                    mapboxMap.removePolyline(p);}
                lineLatLngList = new ArrayList<>();
            }
        }
    };

    private void setGeoJson() throws IOException {
        /*
        해야할 일
        인텐트 확인해서 있으면 프로퍼티만 작성하기
         */
        FileOutputStream outputStream;
        JsonObject geoJson;
        JsonObject jsonProperties;
        JsonObject geometry;
        //JsonArray coordinates;
        SharedPreferences preferences = getContext().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        String id=preferences.getString("id",null);//아이디 얻기
        String name=preferences.getString("name",null);
        Log.i("com.kosmo.shooong",id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        String nowday = format.format(startTime); //현재 날짜 얻어오기
        String filename = name+"_"+nowday+".json";//파일이름 지정
        String filepath = "/data/data/com.kosmo.shooong/files/"+filename; //안드로이드 내부 저장소(앱 삭제되면 같이 삭제 / 다른 앱에서 접근못함)
        jsonFile = new File(filepath);
        //Log.i("com.kosmo.shoong",Double.toString(TurfMeasurement.length(pointsList,TurfConstants.UNIT_KILOMETERS)));
        if (!jsonFile.exists()) { //파일 없으면 빈 파일 생성
            jsonFile.createNewFile();
            geoJson = new JsonObject();
            jsonProperties = new JsonObject();
            geometry = new JsonObject();
            //coordinates = new JsonArray();
            geoJson.addProperty("type","Feature");
            jsonProperties.addProperty("filename",filename);
            jsonProperties.addProperty("userId",id);
            jsonProperties.addProperty("userName",name);
            jsonProperties.addProperty("startTime",nowday);
            jsonProperties.addProperty("duration",(endTime-startTime)/1000);
            jsonProperties.addProperty("recordLength",Double.toString(TurfMeasurement.length(pointsList,TurfConstants.UNIT_KILOMETRES)));
            geometry.addProperty("type","MultiLineString");
            geometry.add("coordinates",coordinates);
            geoJson.add("properties", jsonProperties);
            geoJson.add("geometry",geometry);
            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(geoJson.toString().getBytes());
            outputStream.close();
        }
    }

    public static CharSequence getsteps() {
        if (mSteps == 0) {
            return (CharSequence) "앱을 실행시켜주세요";
        } else
            return "현재 걸음 수 : " + (CharSequence) Integer.toString(mSteps);
    }

    //걸음수 초기화 되지 않음
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (mCounterSteps < 1) {
                // initial value
                mCounterSteps = (int) event.values[0];
            }
            mSteps = (int) event.values[0] - mCounterSteps; // 앱 켤때마다 카운터 0으로 초기화 시킴
            //tvStepCount.setText("현재 걸음 수 : " + (int) event.values[0]);

            mSteps = (int) event.values[0];

            NotificationManager notificationManager=(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelID = "channel_01"; //알림채널 식별자
                String channelName = "MyChannel01"; //알림채널의 이름(별명)
                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(getContext(), channelID);
            }else{
                //알림 빌더 객체 생성
                builder= new NotificationCompat.Builder(getContext(), null);
            }


            builder.setSmallIcon(android.R.drawable.star_off);
            builder.setContentTitle("Shooong");//알림창 제목
            builder.setContentText(mSteps + "걸음");//알림창 내용
            Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.icon_s);
            builder.setLargeIcon(bm);//매개변수가 Bitmap을 줘야한다.
            Notification notification=builder.build();
            notification.vibrate = new long[] {-1};
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notificationManager.notify(1, notification);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onResume() { //처음 켤때
        super.onResume();
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }

    private static class LocationChange
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Fragment_2> activityWeakReference;
        LocationChange(Fragment_2 activity) {
            Log.i("com.kosmo.shooong", "생성자");
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            Fragment_2 activity = activityWeakReference.get();
            Log.i("com.kosmo.shooong", result.getLastLocation()+","+result.getLocations());

            if (activity != null) {
                Location location = result.getLastLocation();

                Toast.makeText(activity.getContext(), ""+location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();
                if (location == null) {
                    Toast.makeText(activity.getContext(), ""+location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a Toast which displays the new location's coordinates

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    Toast.makeText(activity.getContext(), ""+location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Fragment_2 activity = activityWeakReference.get();
        }
    }
}