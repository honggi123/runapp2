package com.example.myapplication.Chat.GeneralUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.example.myapplication.Challenge.Fragment3;
import com.example.myapplication.Chat.Chatroom;
import com.example.myapplication.Chat.CoachUser.CChatroomActivity;
import com.example.myapplication.MySingleton;
import com.example.myapplication.Profile.ProfileMenuActivity;
import com.example.myapplication.R;
import com.example.myapplication.Run.RunMenuActivity;
import com.example.myapplication.viewact.ViewactMenuActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GChatRoomActivity extends AppCompatActivity {

    ImageView menurun;
    ImageView menuviewact;
    ImageView menuch;
    ImageView menumy;
    ImageView menuchat;
    SharedPreferences loginshared;
    String mid;

    RecyclerView crrecyclerView;
    ArrayList<Chatroom> arr_chatroom;
    GChatroomAdapter gChatroomAdapter;
    private String[] splited;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuchat_general);

        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        mid = loginshared.getString("id", null);

        menuchat = findViewById(R.id.btn_menuchat);
        menurun = findViewById(R.id.btn_menurun);
        menuviewact = findViewById(R.id.btn_menuviewact);
        menuch = findViewById(R.id.btn_menuchat);
        menumy = findViewById(R.id.btn_menumy);
        menuset();

        //  ?????????????????? xml id
        crrecyclerView = findViewById(R.id.rc_gchatroom);
        // ????????????????????? ??????
        // ????????? ?????? ??????
        arr_chatroom = new ArrayList<>();
        gChatroomAdapter = new GChatroomAdapter(arr_chatroom);

        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(GChatRoomActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        crrecyclerView.setLayoutManager(linearLayoutManager);
        // ????????? ??????
        crrecyclerView.setAdapter(gChatroomAdapter);

        IntentFilter intentFilter = new IntentFilter("action");
        registerReceiver(mBroadTestReceiver, intentFilter);

    }

    public BroadcastReceiver mBroadTestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("action")) {
                // ?????????????????? ??????????????? ??? ????????? ??????
                String read = intent.getStringExtra("read");
                splited = read.split("@");
               if (splited[0].equals("receivemsg")){
                    requestchatroom(mid);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        requestchatroom(mid);
    }

    public void menuset(){
        Log.e("menuset","1");
        menurun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GChatRoomActivity.this, RunMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.e("menuset","2");
        menuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GChatRoomActivity.this, Fragment3.class);
                startActivity(intent);
                finish();
            }
        });
        Log.e("menuset","3");
        menuviewact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GChatRoomActivity.this, ViewactMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.e("menuset","4");
        menumy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GChatRoomActivity.this, ProfileMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        menuchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GChatRoomActivity.this, GChatRoomActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.e("menuset","5");
    }

    //???????????????  ??????
    public void requestchatroom(String mid){
        // ????????????????????? ?????? ???????????? ?????? php ?????? ??????
        String serverUrl="http://3.12.49.32/getchatroom.php";

        // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("json",jsonObject.toString());
                    boolean success = jsonObject.getBoolean("success");
                    int size = jsonObject.getInt("num");
                    Gson gson = new Gson();
                    if(success) {
                        arr_chatroom.clear();
                        JSONArray jsonArray =  jsonObject.getJSONArray("cr");
                        for (int i = 0; i < size; i++){
                            Chatroom chatroom = gson.fromJson(jsonArray.get(i).toString(), Chatroom.class);
                            arr_chatroom.add(chatroom);
                            Log.e("addarrchatroom",chatroom.getCoachname());
                        }
                        gChatroomAdapter.notifyDataSetChanged();
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GChatRoomActivity.this, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        // ?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("uid", mid);
        // ????????? ????????? ????????? ?????? ??????
        RequestQueue requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(smpr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK){ // ?????? ??????????????? ??????????????????
                requestchatroom(mid);
                return;
            }

        }
    }
}
