package com.example.myapplication.Run;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ImageAdapter;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RuncompleteActivity extends AppCompatActivity {

    ArrayList<String> arr_photopath;
    ArrayList<String> arr_storagepath;
    ImageView btn_plusimg;
    ImageAdapter adapter;
    TextView distanceView;
    TextView memo;
    Button btn_upload;
    TextView timeView;
    TextView dateView;
    int time;
    int distance;
    ImageView btn_noimgplus;
    SharedPreferences loginshared;
    String mid;
    RatingBar howrunrating;
    TextView kcalview;
    double kcal;
    private TextToSpeech tts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arr_photopath = new ArrayList<String>();
        setContentView(R.layout.runcomplete);
         kcalview = findViewById(R.id.kcalview_complete);

        btn_plusimg = findViewById(R.id.btn_plusimg);
        distanceView = findViewById(R.id.distanceview_complete);
        timeView = findViewById(R.id.timeview_complete);
        dateView = findViewById(R.id.dateView_complete);
        btn_upload = findViewById(R.id.btn_upload_runcomp);
        btn_noimgplus = findViewById(R.id.btn_noimgplus);
        memo = findViewById(R.id.memo_runcomp);
        howrunrating = findViewById(R.id.runratingBar);
        // ?????? ??????, ?????? ????????????
        time = getIntent().getIntExtra("time",0);
        distance = getIntent().getIntExtra("distance",0);
        kcal = getIntent().getDoubleExtra("kcal",0.0);

        // ????????? ??? ????????? ????????? ????????????
        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        mid = loginshared.getString("id", null);

        String timeformat =new runActivity().TimeToFormat(time);
        timeView.setText(timeformat);

        double kmdistance = (distance / 1000.0);
        distanceView.setText(String.format("%.2f",kmdistance));

        dateView.setText(getdate());
        kcalview.setText(String.format("%.2f",kcal));

        arr_photopath = getIntent().getStringArrayListExtra("arr_photopath");
        arr_storagepath = getIntent().getStringArrayListExtra("arr_storageimg");
        display_imgview();
        speech("????????? ?????? ?????????. ?????? ????????? "+distance+"?????? ??????"+time+"??? ?????????.");

        adapter = new ImageAdapter(arr_photopath,arr_storagepath, RuncompleteActivity.this);
        RecyclerView recyclerView = findViewById(R.id.rcimg);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        btn_noimgplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 101);
            }
        });

        btn_plusimg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int permission = ContextCompat.checkSelfPermission(RuncompleteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                // ????????? ??????????????? ??????
                if (permission == PackageManager.PERMISSION_DENIED) {
                    // ??????????????? ?????????????????? ????????? ????????????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // ?????? ??????(READ_PHONE_STATE??? requestCode??? 1000?????? ??????
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                    }
                    return;
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, 101);
                }
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadPost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // READ_PHONE_STATE??? ?????? ?????? ????????? ????????????
        if(requestCode == 1000) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, 101);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                Log.e("fileurl",String.valueOf(fileUri));
                try {
                    adapter.additem(fileUri,RuncompleteActivity.this);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                }
                display_imgview();
            }
        }
    }


    // ???????????? ????????? ??????????????? ?????????
    private void uploadPost() throws IOException {
        // urlList??? json ????????? ??????
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<arr_photopath.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url", arr_photopath.get(i));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // ????????????????????? ?????? ???????????? ?????? php ?????? ??????
        String serverUrl="http://3.12.49.32/insert_post.php";

        // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("json",String.valueOf(jsonObject));
                    boolean success = jsonObject.getBoolean("success");
                    if(success) {
                        // ????????? ??????
                        Toast.makeText(RuncompleteActivity.this, "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // ????????? ??????
                        Toast.makeText(RuncompleteActivity.this, "?????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RuncompleteActivity.this, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("json",jsonArray.toString());

        // ?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("id", mid);
        smpr.addStringParam("rating", String.valueOf(howrunrating.getRating()));
        smpr.addStringParam("memo", memo.getText().toString());
        smpr.addStringParam("distance", String.valueOf(distance));
        smpr.addStringParam("time", String.valueOf(time));
        smpr.addStringParam("kcal", String.valueOf(kcal));

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getdate = simpleDate.format(mDate);
        smpr.addStringParam("run_date", getdate);

        smpr.addStringParam("url", jsonArray.toString()); // json ????????? ???????????? ??????
        smpr.addStringParam("cntImage", String.valueOf(arr_photopath.size())); // ????????? ?????? ??????

        //????????? ?????? ?????? (pathList??? ????????? ????????? ?????? uri string ?????????)
        for(int i=0; i<arr_photopath.size(); i++) {
            // uri ?????? ?????? ?????????
            String[] proj= {MediaStore.Images.Media.DATA};
            CursorLoader loader= new CursorLoader(RuncompleteActivity.this, Uri.parse(arr_photopath.get(i)), proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            if(cursor != null){
                int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String abUri= cursor.getString(column_index);
                Log.e("aburi",abUri);
                cursor.close();
                // ????????? ?????? ??????
                smpr.addFile("image"+i, abUri);
            }else{
            }
        }
        // ????????? ????????? ????????? ?????? ??????
        RequestQueue requestQueue = Volley.newRequestQueue(RuncompleteActivity.this);
        requestQueue.add(smpr);

        /*
        RequestQueue requestQueue = MainAct.getRequestQueue();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(RuncompleteActivity.this);
            requestQueue.add(smpr);
        } else {
            requestQueue.add(smpr);
        }
*/

    }
    public void display_imgview(){
        if(arr_photopath.size() == 0){
            btn_noimgplus.setVisibility(View.VISIBLE);
            btn_plusimg.setVisibility(View.INVISIBLE);
        }else{
            btn_noimgplus.setVisibility(View.INVISIBLE);
            btn_plusimg.setVisibility(View.VISIBLE);
        }
    }
    public void speech(String txt){
        tts = new TextToSpeech(RuncompleteActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
                tts.setPitch(1.0f);
                tts.setSpeechRate(1.0f);
                tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });
    }

}
