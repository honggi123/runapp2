package com.example.myapplication.viewact;
// implements DatePickerDialog.OnDateSetListener




import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class addruninfo extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final int GET_GALLERY_IMAGE =200;

    ImageView addmultipleimg;
    ImageView addimg;
    RecyclerView my_recyclerView;
    ImageAdapter imageAdapter;
    ArrayList<String> arr_imgpath;
    TextView btn_adddate;
    TextView view_date;
    TextView btn_adddistance;
    TextView view_time;
    TextView view_distance;
    TextView btn_addtime;
    Boolean complete_datepick;
    Boolean complet_dispick;
    EditText editmemo;
    Button addruninfo;
    SharedPreferences loginshared;
    String date;
    String mid;
    int kmdis;
    int mdis;
    int time;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addruninfo);

        addmultipleimg = findViewById(R.id.addmultipleimg);
        addmultipleimg.setVisibility(View.VISIBLE);

        addimg = findViewById(R.id.addimg);
        btn_adddate = findViewById(R.id.btn_adddate);
        view_date = findViewById(R.id.viewdate_addrun);
        btn_adddistance = findViewById(R.id.btn_adddistance);
        view_distance = findViewById(R.id.viewdistance_addrun);
        view_time = findViewById(R.id.viewtime_addrun);
        btn_addtime = findViewById(R.id.btn_addtime);
        editmemo = findViewById(R.id.editmemo_addrun);
        addruninfo = findViewById(R.id.btn_addruninfo);

        // ????????? ??? ????????? ????????? ????????????
        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        mid = loginshared.getString("id", null);

        // ????????? ??????????????????
        my_recyclerView   = findViewById(R.id.rc_addrun);
        // ????????????????????? ??????
        // ????????? ?????? ??????
        arr_imgpath = new ArrayList<>();

        complete_datepick = false;
        complet_dispick = false;

        addmultipleimg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //????????? ????????? ?????? Intent ??????
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // true??? ?????? ???????????? ?????? ?????? ?????? ??????
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //???????????? uri??? ?????????.
                startActivityForResult(intent, GET_GALLERY_IMAGE); //GET_GALLERY_IMAGE???
                }
        });

        addimg.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {

                int permission = ContextCompat.checkSelfPermission(addruninfo.this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
        btn_adddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdatepicker();
            }
        });

        btn_adddistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pickdistance_Dialog pickdistance_dialog = new Pickdistance_Dialog();
                pickdistance_dialog.calldialog();
            }
        });
        btn_addtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picktime_Dialog picktime_dialog = new Picktime_Dialog();
                picktime_dialog.calldialog();
            }
        });
        addruninfo.setOnClickListener(new View.OnClickListener() {
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

    public void showdatepicker(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(addruninfo.this,addruninfo.this,year,month,day).show();
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        try {
            month++;
            if(compare_sdate(year,month,day)){
                // ?????? ??? ????????? ??????
                date = year+"/"+month+"/"+day;
                view_date.setText(date);
                complete_datepick = true;
            }else{
                complete_datepick = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode ==GET_GALLERY_IMAGE) //????????? ?????? ???????????? ???
            {
                    if(resultCode ==RESULT_OK) {
                    if (data.getClipData() == null) {
                    } else {
                        addimg.setVisibility(View.VISIBLE);
                        addmultipleimg.setVisibility(View.INVISIBLE);

                    ClipData clipData = data.getClipData();
                    if (clipData.getItemCount() == 1) { //????????? 1??? ???????????? ???
                    Uri img_path = clipData.getItemAt(0).getUri(); //????????? URI
                    arr_imgpath.add(String.valueOf(img_path));  //?????? ???????????? ??????
                    }
                    else if ( clipData.getItemCount() > 1) { //????????? 1??? ?????? ???????????? ???
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri img_path = clipData.getItemAt(i).getUri();
                        arr_imgpath.add(String.valueOf(img_path)); }
                    }
                        }
                        imageAdapter = new ImageAdapter(arr_imgpath,addruninfo.this);

                        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(addruninfo.this);
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        my_recyclerView.setLayoutManager(linearLayoutManager);
                        // ????????? ??????
                        my_recyclerView.setAdapter(imageAdapter);
                    }
            }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                Log.e("fileurl",String.valueOf(fileUri));
                try {
                    imageAdapter.additem(fileUri, addruninfo.this);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }


    public Boolean compare_sdate(int year,int month,int day) throws ParseException {
            SimpleDateFormat format;
            format = new SimpleDateFormat("yyyy-MM-dd");
           Date c_date = format.parse(year +"-"+month+"-"+day);

            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy-MM-dd", Locale.KOREA );

            Date currentTime = new Date();

            String oTime = mSimpleDateFormat.format ( currentTime ); //???????????? (String)
            Date currentDate =  mSimpleDateFormat.parse( oTime );

            Log.e("date",c_date.compareTo(currentDate)+"");
            if(c_date.compareTo(currentDate) > 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(addruninfo.this);
                    builder.setTitle("?????? ?????? ?????? ?????? ?????? ????????? ?????????????????????. ")        // ?????? ??????
                    .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                    .setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                // ?????? ?????? ????????? ??????, ????????? ???????????????.
                                public void onClick(DialogInterface dialog, int whichButton){
                                        //????????? ?????? ???????????? ???????????? ?????????.
                                        }
                    });

                        AlertDialog dialog = builder.create();    // ????????? ?????? ??????
                        dialog.show();    // ????????? ?????????
                           return false;
                }else{
                    return true;
                 }
            }


    // ???????????? ????????? ??????????????? ?????????
    private void uploadPost() throws IOException {
        // urlList??? json ????????? ??????
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<arr_imgpath.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url", arr_imgpath.get(i));
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
                        Toast.makeText(addruninfo.this, "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();

                        Intent intentR = new Intent();
                        setResult(RESULT_OK,intentR); //????????? ??????

                        finish();
                    } else {
                        // ????????? ??????
                        Toast.makeText(addruninfo.this, "?????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(addruninfo.this, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("json",jsonArray.toString());

        // ?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("id", mid);
        smpr.addStringParam("rating", String.valueOf(0));
        smpr.addStringParam("memo", editmemo.getText().toString());
        smpr.addStringParam("distance", String.valueOf(mdis));
        smpr.addStringParam("time", String.valueOf(time));
        smpr.addStringParam("run_date", date);
        smpr.addStringParam("url", jsonArray.toString()); // json ????????? ???????????? ??????
        smpr.addStringParam("cntImage", String.valueOf(arr_imgpath.size())); // ????????? ?????? ??????

        //????????? ?????? ?????? (pathList??? ????????? ????????? ?????? uri string ?????????)
        for(int i=0; i<arr_imgpath.size(); i++) {
            // uri ?????? ?????? ?????????
            String[] proj= {MediaStore.Images.Media.DATA};
            CursorLoader loader= new CursorLoader(addruninfo.this, Uri.parse(arr_imgpath.get(i)), proj, null, null, null);
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
        RequestQueue requestQueue = Volley.newRequestQueue(addruninfo.this);
        requestQueue.add(smpr);


    }


    public class Pickdistance_Dialog {
        Dialog dig;
        Button btn_setdistance;
        EditText pickerkm_distance;
        EditText pickerm_distance;

        public void calldialog() {
            dig = new Dialog(addruninfo.this);
            // ??????????????? ??????????????? ?????????.
            dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // ????????? ?????????????????? ??????????????? ????????????.
            dig.setContentView(R.layout.pickdistance_dialog);
            btn_setdistance = dig.findViewById(R.id.btn_setdistance);
            pickerkm_distance = dig.findViewById(R.id.picker1_time);
            pickerm_distance = dig.findViewById(R.id.picker2_time);

            btn_setdistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    kmdis = Integer.parseInt(pickerkm_distance.getText().toString());
                    mdis = Integer.parseInt(pickerm_distance.getText().toString());
                    view_distance.setText(kmdis + "."+mdis +" km");
                    String dis = kmdis +"."+mdis;
                    mdis = (int) (Float.parseFloat(dis) * 1000);
                    complet_dispick = true;
                    dig.dismiss();

                }
            });
            dig.show();
        }
    }

     public class Picktime_Dialog{
            Dialog dig;
            Button btn_settime;
            EditText pickerhour_time;
            EditText pickermin_time;
            EditText pickersec_time;

            public void calldialog() {
                dig = new Dialog(addruninfo.this);
                // ??????????????? ??????????????? ?????????.
                dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // ????????? ?????????????????? ??????????????? ????????????.
                dig.setContentView(R.layout.picktime_dialog);
                btn_settime = dig.findViewById(R.id.btn_setdistance);
                pickerhour_time = dig.findViewById(R.id.picker1_time);
                pickermin_time = dig.findViewById(R.id.picker2_time);
                pickersec_time = dig.findViewById(R.id.picker3_time);

                btn_settime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = Integer.parseInt(pickerhour_time.getText().toString());
                        int min = Integer.parseInt(pickermin_time.getText().toString());
                        int sec = Integer.parseInt(pickersec_time.getText().toString());

                        view_time.setText(hour+":"+min+":"+sec);

                        time = (hour * 60 * 60) +(min*60) +sec;

                        dig.dismiss();
                    }
                });
                dig.show();
            }
        }



}
