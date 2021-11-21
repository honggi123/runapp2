package com.example.myapplication.Run;

import static com.example.myapplication.Run.runActivity.TimeToFormat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Challenge.Fragment3;
import com.example.myapplication.Profile.ProfileMenuActivity;
import com.example.myapplication.R;
import com.example.myapplication.RequestInterface;
import com.example.myapplication.viewact.ViewactMenuActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RunMenuActivity extends AppCompatActivity implements OnMapReadyCallback {

    static RequestQueue requestQueue;
    public final String TAG = "MyTag";
    Button btn_logout;
    String mid;
    String logintype;
    BottomNavigationView bottomNavigationView;
    static final int MY_PERMISSION_STORAGE = 101;
    PermissionListener permissionlistener;
    SharedPreferences loginshared;

    Button btn_runstart;
    TextView view_tdate;
    TextView view_tdistance;
    TextView view_ttime;
    Button btn_goalset;
    String tdate;
    TextView view_goalnum;
    ImageView view_goalchk;

    private MapView mapview = null;
    private GoogleMap mMap;

    RunMenuActivity.NetworkTask networkTask;

    ImageView menurun;
    ImageView menuviewact;
    ImageView menuch;
    ImageView menumy;
    SimpleMultiPartRequest smpr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          /*
        int i = 0 ;
        while(i<=10000){
            new Thread(new Runnable(){
                public void run() {
                    try {
                        Thread.sleep(10000000);
                    } catch(InterruptedException e) { }
                }
            }).start();
            i++;
            Log.e("oncreatevioew",i+"");
        }*/
        setContentView(R.layout.fragment1);

        loginshared = getSharedPreferences("Login", MODE_PRIVATE);

        // 로그인 정보
        logintype = loginshared.getString("logintype", null);
        mid = loginshared.getString("id", null);

        Log.e("Mainact","error2");
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RunMenuActivity.this, Runbeforetimer_Activity.class);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(RunMenuActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        menurun = findViewById(R.id.btn_menurun);
        menuviewact = findViewById(R.id.btn_menuviewact);
        menuch = findViewById(R.id.btn_menuch);
        menumy = findViewById(R.id.btn_menumy);

        btn_runstart = findViewById(R.id.btn_runstart);
        view_tdate = findViewById(R.id.todaydate_frag1);
        view_tdistance = findViewById(R.id.viewdistance_frag1);
        view_ttime = findViewById(R.id.viewtime_frag1);
        btn_goalset = findViewById(R.id.btn_goalset_frag1);
        tdate = getdate();
        view_goalnum = findViewById(R.id.view_goalnum);
        view_goalchk = findViewById(R.id.view_goalchk);
        view_tdate.setText(tdate);
        Log.e("oncreatevioew","error2");

        mapview = (MapView) findViewById(R.id.mapview_runmenu);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        Log.e("oncreatevioew","error3");
        // 회원 투데이 러닝 정보 가져오기
       //getinfo();
        Log.e("oncreatevioew","error4");
        mapview.getMapAsync(this);

        Log.e("oncreatevioew","error5");

        // 메뉴버튼 활성화
        menuset();


        btn_runstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("oncreatevioew","error6");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunMenuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 마쉬멜로우 이상버전부터 권한을 물어본다
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    }
                    return;
                }
                Log.e("oncreatevioew","error7");
                Intent intent = new Intent(RunMenuActivity.this, Runbeforetimer_Activity.class);
                Log.e("oncreatevioew","error8");
                startActivity(intent);
                Log.e("oncreatevioew","error9");
            }
        });

        btn_goalset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choicedt();
            }
        });
        Log.e("oncreatevioew","error10");

    }



    // 목표 설정 시 거리 목표 설정
    public void choicedt(){
        Intent intent = new Intent(RunMenuActivity.this,distancegoalset_Activity.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        Log.e("oncreatevioew","onresume");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.e("oncreatevioew","error11");
        super.onStart();
        Log.e("oncreatevioew","error12");
        // 회원 투데이 러닝 정보 가져오기
        //getinfo();
        Log.e("oncreatevioew","error13");

        // AsyncTask를 통해 HttpURLConnection 수행.
        networkTask = new NetworkTask(RunMenuActivity.this);
       networkTask.execute(mid,tdate);
       Log.e("id",mid);
        Log.e("tdate",tdate);
    }


    @Override
    public void onStop() {
        Log.e("oncreatevioew","onstop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("oncreatevioew","ondestory");
        networkTask.cancel(true);

        mapview.getOverlay().clear();
        mapview.removeAllViews();
        mapview.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("oncreatevioew","error11");
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
        Log.e("oncreatevioew","error12");
        if (ContextCompat.checkSelfPermission(RunMenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunMenuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("oncreatevioew","error13");
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("oncreatevioew","error14");
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                Log.e("oncreatevioew","error15");
            }
            return;
        }
        Log.e("oncreatevioew","error16");
        mMap.setMyLocationEnabled(true);
        Log.e("oncreatevioew","error17");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("grantcode", requestCode + "ss");
        switch (requestCode) {
            case 1000:
                Log.e("grantcode", "1000");
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(RunMenuActivity.this, "해당 권한을 활성화 하셔야 러닝 서비스를 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(RunMenuActivity.this, Runbeforetimer_Activity.class);
                startActivity(intent);
                break;
            case 1001:
                Log.e("grantcode", "1001");
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(RunMenuActivity.this, "해당 권한을 활성화 하셔야 현 위치를 확인 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (ActivityCompat.checkSelfPermission(RunMenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunMenuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public static class RequestGetInfo {

    }


  /*
    public void getinfo(){

        // 안드로이드에서 보낼 데이터를 받을 php 서버 주소
        String serverUrl="http://3.143.9.214/Getdailyinfo.php";

        WeakReference<SimpleMultiPartRequest> obj = new WeakReference(new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int distance = 0;
                    // 활동 받아오기
                    if(jsonObject.getString("info").equals("null")){
                        // 오늘의 활동이 없어서 데이터들 못 받아 온 경우
                        view_tdistance.setText("0");


                        view_ttime.setText("00:00");
                    }else{
                        // 오늘의 활동 데이터 받아 온 경우
                        boolean success = jsonObject.getBoolean("isuccess");
                        if(success) {
                            //  성공
                            int time =jsonObject.getInt("time");
                            distance =  jsonObject.getInt("distance");
                            double kmdistance = (distance / 1000.00);
                            view_ttime.setText(TimeToFormat(time));
                            view_tdistance.setText(String.format("%.2f",kmdistance));
                        } else {
                            // 오류로 데이터를 못 받아온 경우
                        }
                        Log.e("json",String.valueOf(jsonObject));
                    }
                    // 목표 받아오기
                    if(jsonObject.getString("goalset").equals("null")){
                        // 오늘의 목표가 없어서 데이터들 못 받아 온 경우
                        view_goalnum.setText("목표를 설정해주세요.");
                        view_goalnum.setTextSize(20f);
                    }else{
                        view_goalnum.setTextSize(35f);
                        // 오늘의 목표 데이터 받아 온 경우
                        boolean success = jsonObject.getBoolean("gsuccess");
                        if(success) {
                            //  성공
                            int dgoal = jsonObject.getInt("Dgoal");
                            if(distance>=dgoal){
                                view_goalchk.setVisibility(View.VISIBLE);
                                btn_goalset.setEnabled(false);
                            }else{
                                view_goalchk.setVisibility(View.INVISIBLE);
                            }
                            double Dgoal =  (jsonObject.getInt("Dgoal") / 1000.0);

                            view_goalnum.setText(String.valueOf(Dgoal) +" km");

                        } else {
                            // 오류로 데이터를 못 받아온 경우
                        }
                        Log.e("json",String.valueOf(jsonObject));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null));

        // 파일 전송 요청 객체 생성[결과를 String으로 받음]
        smpr = obj.get();

        // 요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("id",mid);
        smpr.addStringParam("date", String.valueOf(tdate));

        RequestInterface.getInstance(getApplicationContext());

        // 서버에 데이터 보내고 응답 요청
        requestQueue = RequestInterface.singleton.getRequestQueue();
        requestQueue.add(smpr);


        obj.clear();


        Log.e("size",""+requestQueue.getThreadPoolSize());


        RequestQueue requestQueue = MainAct.getRequestQueue();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(smpr);
        } else {
            requestQueue.add(smpr);
        }

    }
 */


    public  class NetworkTask extends AsyncTask<String, Void, String> {

        String errorString = null;
        RunMenuActivity context;

        private WeakReference<RunMenuActivity> activityWeakReference;

        NetworkTask(RunMenuActivity context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            TextView view_ttime = context.findViewById(R.id.viewdistance_frag1);
            TextView view_tdistance = context.findViewById(R.id.viewtime_frag1);
            ImageView view_goalchk = context.findViewById(R.id.view_goalchk);
            Button btn_goalset = context.findViewById(R.id.btn_goalset_frag1);
            TextView view_goalnum = context.findViewById(R.id.view_goalnum);

            try {
                JSONObject jsonObject = new JSONObject(result);
                int distance = 0;
                // 활동 받아오기
                if(jsonObject.getString("info").equals("null")){
                    // 오늘의 활동이 없어서 데이터들 못 받아 온 경우
                    view_ttime.setText("00:00");
                    view_tdistance.setText("0");
                }else{
                    // 오늘의 활동 데이터 받아 온 경우
                    boolean success = jsonObject.getBoolean("isuccess");
                    if(success) {
                        //  성공
                        int time =jsonObject.getInt("time");
                        distance =  jsonObject.getInt("distance");
                        double kmdistance = (distance / 1000.00);
                        view_ttime.setText(TimeToFormat(time));
                        view_tdistance.setText(String.format("%.2f",kmdistance));
                    } else {
                        // 오류로 데이터를 못 받아온 경우
                    }
                    Log.e("json",String.valueOf(jsonObject));
                }
                // 목표 받아오기
                if(jsonObject.getString("goalset").equals("null")){
                    // 오늘의 목표가 없어서 데이터들 못 받아 온 경우
                    view_goalnum.setText("목표를 설정해주세요.");
                    view_goalnum.setTextSize(20f);
                }else{
                    view_goalnum.setTextSize(35f);
                    // 오늘의 목표 데이터 받아 온 경우
                    boolean success = jsonObject.getBoolean("gsuccess");
                    if(success) {
                        //  성공
                        int dgoal = jsonObject.getInt("Dgoal");
                        if(distance>=dgoal){
                            view_goalchk.setVisibility(View.VISIBLE);
                            btn_goalset.setEnabled(false);
                        }else{
                            view_goalchk.setVisibility(View.INVISIBLE);
                        }
                        double Dgoal =  (jsonObject.getInt("Dgoal") / 1000.0);

                        view_goalnum.setText(String.valueOf(Dgoal) +" km");

                    } else {
                        // 오류로 데이터를 못 받아온 경우
                    }
                    Log.e("json",String.valueOf(jsonObject));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://3.143.9.214/Getdailyinfo.php";
            String postParameters = "id=" + searchKeyword1+"&date="+searchKeyword2;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {

                errorString = e.toString();
                return null;
            }
        }


        public  String TimeToFormat(int time){
            String totaltime = null;
            int sec = time % 60;
            if (time > 3600) {
                int bun = time / 60 % 60;
                int hour = time / 3600;
                totaltime = String.format("%02d", hour) + ":" + String.format("%02d", bun) + ":" + String.format("%02d", sec);
            } else {
                int bun = time / 60 % 60;
                totaltime = String.format("%02d", bun) + ":" + String.format("%02d", sec);
            }
            return totaltime;
        }
    }



    public void menuset(){
        menurun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunMenuActivity.this, RunMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        menuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunMenuActivity.this, Fragment3.class);
                startActivity(intent);
                finish();
            }
        });

        menuviewact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunMenuActivity.this, ViewactMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        menumy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunMenuActivity.this, ProfileMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public  String getdate(){
        long now = System.currentTimeMillis();

        Date date1 = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDate.format(date1);
        return date;
    }



}