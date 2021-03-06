package com.example.myapplication.viewact.Coach;
import com.android.volley.Request;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
// implements DatePickerDialog.OnDateSetListener


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MySingleton;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GetInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    NumberPicker numberPicker;
    Button btn_runnumnext;

    ArrayList<String> arrayList;
    LinearLayout layoutweekquestion;
    LinearLayout layoutstartdate;
    int questionnum = 3;
    int nowquestionnum = 1;
    TextView choicename;
    Button btn_setstartdate;
    TextView coachstartdate;
    SimpleDateFormat format ;
    Date s_date = null;
    TextView totalqnum;
    TextView nowqnum;
    FrameLayout weeknumserframe;
    FrameLayout coachuserframe;
    FrameLayout startdateframe;

    RecyclerView rccoachuser;
    ArrayList<CoachUser> arr_coachuser;
    CoachUserAdapter coachUserAdapter;
    String coachname;
    String mid;
    SharedPreferences loginshared;
    String logintype;
    ArrayList<String> arrq;
    LinearLayout prognum;

    Intent sendintent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coachinputinfo);

        coachname = getIntent().getStringExtra("coachname");
        arrayList = getIntent().getStringArrayListExtra("arrselect");
        arrq = getIntent().getStringArrayListExtra("arrquestion");

        coachstartdate = findViewById(R.id.coachstartdate);
        numberPicker = findViewById(R.id.numberpicker5);
        btn_runnumnext = findViewById(R.id.btn_runnumnext);
        layoutweekquestion = findViewById(R.id.layoutweekquestion);
        layoutstartdate = findViewById(R.id.layoutstartdate);
        totalqnum = findViewById(R.id.totalqnum);
        nowqnum = findViewById(R.id.nowqnum);
        choicename = findViewById(R.id.choicename);
        btn_setstartdate = findViewById(R.id.btn_setstartdate);
        prognum = findViewById(R.id.prognum);
        // ?????? ?????????
        weeknumserframe = findViewById(R.id.weeknumserframe);
        coachuserframe = findViewById(R.id.coachuserframe);
        startdateframe = findViewById(R.id.startdateframe);

        weeknumserframe.setVisibility(View.VISIBLE);
        coachuserframe.setVisibility(View.GONE);
        startdateframe.setVisibility(View.GONE);

        rccoachuser = findViewById(R.id.rc_coachuser);

        sendintent = new Intent("send");
        sendintent.setPackage("com.example.myapplication");


        // ????????? ??????
        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        Log.e("ProfileMenuAct","14");
        mid = loginshared.getString("id", null);

        // Number Picker Setting
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(7);
        String[] arr = new String[7];
        format = new SimpleDateFormat("yyyy-MM-dd");

        // ?????? ?????? ?????? ?????????
        totalqnum.setText(arrayList.size()+3+"");

        // ?????? ?????? ?????? ?????????
        nowqnum.setText(arrayList.size()+1+"");

        for(int i = 0;i < arr.length; i++ ){
            arr[i] = String.valueOf(i+1) ;
        }

        numberPicker.setDisplayedValues(arr);

        btn_runnumnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(nowquestionnum ==1){
                    arrayList.add(String.valueOf(numberPicker.getValue())+"???");
                    arrq.add("???????????? ????????? ?????? ??????");
                }
                nowquestionnum++;
                nowqnum.setText(arrayList.size()+nowquestionnum+"");
                if(nowquestionnum>questionnum){
                    /*
                    Intent intent = new Intent(GetInfoActivity.this,CoachPlanActivity.class);
                    intent.putExtra("arrselect",arrayList);
                    intent.putExtra("startdate",format.format(s_date));
                    startActivity(intent);
                     */
                    arrayList.add(coachUserAdapter.getselid());
                    request(mid);

                }else if(nowquestionnum == 2){
                    String question = "????????? ?????? ???????????? ??????????????????.";
                    choicename.setText(question);
                    arrq.add("????????? ?????? ?????????");
                    weeknumserframe.setVisibility(View.GONE);
                    coachuserframe.setVisibility(View.GONE);
                    startdateframe.setVisibility(View.VISIBLE);
                    btn_setstartdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showdatepicker();
                        }
                    });

                }else if(nowquestionnum ==3){
                    prognum.setVisibility(View.INVISIBLE);
                    String question = "????????? ???????????? ????????? ??????????????????";
                    choicename.setText(question);

                    weeknumserframe.setVisibility(View.GONE);
                    coachuserframe.setVisibility(View.VISIBLE);
                    startdateframe.setVisibility(View.GONE);

                    //  ?????????????????? xml id

                            // ????????????????????? ??????
                            // ????????? ?????? ??????
                            arr_coachuser = new ArrayList<CoachUser>();
                            coachUserAdapter = new CoachUserAdapter(arr_coachuser);

                            GridLayoutManager gridLayoutManager =  new GridLayoutManager(GetInfoActivity.this,2);
                            gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            rccoachuser.setLayoutManager(gridLayoutManager);
                            // ????????? ??????
                          rccoachuser.setAdapter(coachUserAdapter);

                    coachrequest();
                }
            }
        });
    }


    public void coachrequest(){
            // ????????????????????? ?????? ???????????? ?????? php ?????? ??????
            String serverUrl="http://3.12.49.32/getcoachuser.php";

            // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
            SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
            try {
            JSONObject jsonObject = new JSONObject(response);
            Log.e("response",response);
            boolean success = jsonObject.getBoolean("success");
                if(success) {
                    int num = jsonObject.getInt("num");
                    for(int i= 0; i< num; i++){
                        JSONArray jsonObject1 = jsonObject.getJSONArray("coachuser");
                        JSONObject data = jsonObject1.getJSONObject(i);
                        CoachUser coachUser =  new CoachUser();
                        coachUser.setId(data.getString("id"));
                        coachUser.setName(data.getString("name"));
                        coachUser.setCareer(data.getString("career"));
                        coachUser.setDesc(data.getString("description"));
                        arr_coachuser.add(coachUser);
                        coachUserAdapter.notifyDataSetChanged();
                    }
                } else {
                // ????????? ??????

                }
            } catch (Exception e) {
            e.printStackTrace();
            }
            }
            }, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {

            }
            });
            // ????????? ????????? ????????? ?????? ??????
            RequestQueue requestQueue = Volley.newRequestQueue(GetInfoActivity.this);
            requestQueue.add(smpr);
            }


    public void showdatepicker(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int start_or_end = 2;
        new DatePickerDialog(GetInfoActivity.this,GetInfoActivity.this,year,month,day).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy-MM-dd", Locale.KOREA );

        Date currentTime = new Date();
        Date currentDate = null;
        String oTime = mSimpleDateFormat.format ( currentTime ); //???????????? (String)
        try {
             currentDate =  mSimpleDateFormat.parse( oTime );
            s_date = format.parse(year +"-"+(month+1)+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(s_date.compareTo(currentDate) < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GetInfoActivity.this);
            builder.setTitle("?????? ?????? ????????? ?????? ????????????. ")        // ?????? ??????
                    .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        // ?????? ?????? ????????? ??????, ????????? ???????????????.
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //????????? ?????? ???????????? ???????????? ?????????.
                            s_date = null;
                        }
                    });
            AlertDialog dialog = builder.create();    // ????????? ?????? ??????
            dialog.show();    // ????????? ?????????
        }else{
            coachstartdate.setText(format.format(s_date));
            arrayList.add(format.format(s_date));
        }
    }

    public void request(String mid){
        // ????????????????????? ?????? ???????????? ?????? php ?????? ??????
        String serverUrl="http://3.12.49.32/getuserinfo.php";

        // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("json",jsonObject+"");
                    boolean success = jsonObject.getBoolean("success");
                    if(success) {
                        JSONObject json =jsonObject.getJSONObject("data");
                        Log.e("json",json+"");

                        String coachid = null;

                        for (int i = 0; i < arr_coachuser.size(); i++){
                            if(arr_coachuser.get(i).getSel()){
                                coachid= arr_coachuser.get(i).id;
                            }
                        }
                        String msg = "coachask@"+coachname+"@"+mid+"@"+coachid;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c1 = Calendar.getInstance();
                        String strToday = sdf.format(c1.getTime());
                        msg+= "@"+strToday;

                        msg += "@question";

                        JSONObject qjsonObject = new JSONObject();

                        Log.e("arrq",arrq.size()+"");
                        Log.e("arrayList",arrayList.size()+"");
                        for (int i = 0; i < arrq.size(); i++){
                            qjsonObject.put(arrq.get(i), arrayList.get(i));
                        }

                        msg += "@"+qjsonObject.toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(GetInfoActivity.this);
                        String finalMsg = msg;


                        String finalCoachid = coachid;
                        builder.setTitle("???????????? ???????????? ??? ???????????? ????????? ???????????? ??????????????????????")        // ?????? ??????
                                .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                                .setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                    // ?????? ?????? ????????? ??????, ????????? ???????????????.
                                    public void onClick(DialogInterface dialog, int whichButton){
                                        //????????? ?????? ???????????? ???????????? ?????????.

                                        /*
                                        sendintent.putExtra("send","coachask@"+mid+
                                                "@"+mid+"@"+finalCoachid+"@"+strToday+"@"+json.toString()+"@"+finalMsg);

                                        */
                                        sendintent.putExtra("send",finalMsg);

                                        getApplicationContext().sendBroadcast(sendintent);

                                        finish();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener(){
                                    // ?????? ?????? ????????? ??????, ?????? ???????????????.
                                    public void onClick(DialogInterface dialog, int whichButton){
                                        //????????? ?????? ???????????? ???????????? ?????????.
                                        finish();
                                    }
                                });

                        AlertDialog dialog = builder.create();    // ????????? ?????? ??????
                        dialog.show();    // ????????? ?????????


                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // ?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("userID", mid);

        // ????????? ????????? ????????? ?????? ??????
        RequestQueue requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(smpr);

    }

}
