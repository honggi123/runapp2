package com.example.myapplication.Challenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChModify extends AppCompatActivity {



    ImageView btn_distancesel;
    ImageView btn_datesel;
    Button btn_comp;
    EditText edit_chname;
    SharedPreferences Challengeshared;
    TextView view_distance;
    TextView view_date;
    float distance;
    String startdate;
    String enddate;
    SharedPreferences Loginshared;
    ChallengeInfo challengeInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challengemodify);

        edit_chname =findViewById(R.id.edit_chname_chmd);
        btn_comp = findViewById(R.id.btn_comp_chmd);
        view_date = findViewById(R.id.view_date_chmd);
        view_distance = findViewById(R.id.view_distance_chmd);

        Loginshared = getSharedPreferences("Login", MODE_PRIVATE);

        distance = 0.0f;
        challengeInfo = (ChallengeInfo) getIntent().getSerializableExtra("chinfo");

        edit_chname.setText(challengeInfo.name);

//        btn_datesel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChModify.this,Challengedatemk_Activity.class);
//                startActivity(intent);
//            }
//        });
//        btn_distancesel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChModify.this,Challengedistancemk_Activity.class);
//                startActivity(intent);
//            }
//        });
        btn_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = null;
                // ????????? ?????? ????????? ??????
                if(edit_chname.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChModify.this);
                    builder.setTitle("????????? ????????? ???????????????.")        // ?????? ??????
                            .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                            .setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                // ?????? ?????? ????????? ??????, ????????? ???????????????.
                                public void onClick(DialogInterface dialog, int whichButton){
                                    //????????? ?????? ???????????? ???????????? ?????????.
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();    // ????????? ?????? ??????
                    dialog.show();    // ????????? ?????????
                }else {
                    // ?????? ?????? ?????? ???
                    name = edit_chname.getText().toString();

                    if (distance == 0.0f || startdate == null || enddate == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChModify.this);
                        builder.setTitle("?????? ????????? ???????????????.")        // ?????? ??????
                                .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    // ?????? ?????? ????????? ??????, ????????? ???????????????.
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //????????? ?????? ???????????? ???????????? ?????????.
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog dialog = builder.create();    // ????????? ?????? ??????
                        dialog.show();    // ????????? ?????????
                    } else {
                        // ?????? ????????? ??????????????? ????????? ???????????? ?????????
                        request(name);
                    }
                }
            }
        });
        btn_comp.setEnabled(false);

        edit_chname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_chname.getText().toString().equals(challengeInfo.name)){
                    btn_comp.setEnabled(false);
                }else{
                    btn_comp.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();
        // ??????, ?????? ?????? ????????????
        Challengeshared = getSharedPreferences("ch", MODE_PRIVATE);
        distance = Challengeshared.getFloat("distance", 0.0F);

        if(distance != 0.0f){
            view_distance.setText(distance+" km");
        }else {
            Log.e("g_distance",challengeInfo.g_distance+"");
            distance = challengeInfo.g_distance / 1000 + challengeInfo.g_distance % 1000;
            double mdistance = (challengeInfo.g_distance / 1000.00);
            view_distance.setText(String.format("%.2f",mdistance)+" km");
        }

        startdate = Challengeshared.getString("startdate",null);
        enddate = Challengeshared.getString("enddate",null);



        if(startdate != null && enddate != null){
            String newdate =timetodate(enddate);
            view_date.setText(startdate + " ~ " + newdate);
        }else{
            startdate = challengeInfo.s_date;
            enddate = challengeInfo.g_date;
            String newdate =timetodate(enddate);
            view_date.setText(startdate +" ~ "+newdate);
        }


    }

    public String timetodate(String olddate){
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        String newdate = null;
        try {
            date = simpleDate.parse(olddate);
            newdate =newformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newdate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor edit = Challengeshared.edit();
        edit.clear();
        edit.commit();
    }

    public void request(String name){

        ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(ChModify.this,
                "??????????????????..", null, true, true);

        // ????????????????????? ?????? ???????????? ?????? php ?????? ??????onDateSet
        String serverUrl="http://3.12.49.32/chmodify.php";
        // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success) {
                        // ????????? ??????
                        Toast.makeText(ChModify.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor edit = Challengeshared.edit();

                        challengeInfo.setId(Loginshared.getString("id", null));
                        challengeInfo.setName(name);

                        Intent intentR = new Intent();
                        intentR.putExtra("chinfo" , challengeInfo); //??????????????? ??????????????? ??????
                        setResult(RESULT_OK,intentR); //????????? ??????

                        edit.clear();
                        edit.commit();
                        finish();
                    } else {
                        // ????????? ??????
                        Toast.makeText(ChModify.this, "?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ChModify.this, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        // ?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("cno", String.valueOf(challengeInfo.cno));
        smpr.addStringParam("id",Loginshared.getString("id", null));
        smpr.addStringParam("name", name);

        // ????????? ????????? ????????? ?????? ??????
        RequestQueue requestQueue = Volley.newRequestQueue(ChModify.this);
        requestQueue.add(smpr);


    }



}
