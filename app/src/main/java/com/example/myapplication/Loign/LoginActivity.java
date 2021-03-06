package com.example.myapplication.Loign;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Chat.CoachUser.CChatroomActivity;
import com.example.myapplication.Chat.MySocketService;
import com.example.myapplication.Join.JoinActivity;
import com.example.myapplication.Join.snsjoinActivity;
import com.example.myapplication.MySingleton;
import com.example.myapplication.R;
import com.example.myapplication.Request.IdchkRequest;
import com.example.myapplication.Request.LoginRequest;
import com.example.myapplication.Run.RunMenuActivity;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText edit_id;
    EditText edit_pw;
    Button btn_login;
    Button btn_join;
    TextView btn_findid;
    TextView btn_pwfind;
    ImageButton btn_kakaologin;
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;
    // ????????? ?????? ?????? shared
    SharedPreferences Loginshared;
    SharedPreferences.Editor loginedit;
    MySocketService socketService;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("loginact","error1");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        SharedPreferences loginshared;
        String mid;

        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        mid = loginshared.getString("id", null);


        Log.e("loginact","error2");

        //RequestInterface.singleton.getInstance().setContext(getApplicationContext`());
        MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        edit_id = findViewById(R.id.edit_id);
        edit_pw = findViewById(R.id.edit_pw);
        btn_findid = findViewById(R.id.btn_findid);
        btn_pwfind = findViewById(R.id.btn_findpw);
        Log.e("loginact","error3");

        btn_join = findViewById(R.id.btn_join);
        btn_login = findViewById(R.id.btn_login);
        btn_kakaologin = findViewById(R.id.btn_kakaologin);
        Log.e("loginact","error4");
        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        Log.e("loginact","error5");
        // ????????? ?????? ?????? sharedpref
        Loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        loginedit = Loginshared.edit();
        if(Loginshared.getBoolean("dologin",false)){
           // Intent intent = new Intent(LoginActivity.this, RunMenuActivity.class);
            //startActivity(intent);
           // finish();
        }

        // ????????? ????????? ??????
        btn_kakaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });


        // ???????????? ????????? ??????
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        // ????????? ??????
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mID = edit_id.getText().toString();
                String mPW = edit_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("json", response);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // ??????????????? ????????? ??????
                                loginedit.putBoolean("dologin",true);
                                loginedit.putString("logintype","standard");
                                loginedit.putString("id",mID);
                                loginedit.commit();

                                socketconn(mID);
                                if(Boolean.parseBoolean(jsonObject.getString("coachuser"))){

                                    Toast.makeText(LoginActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, CChatroomActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, RunMenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else { // ??????????????? ????????? ??????
                                Toast.makeText(LoginActivity.this, "???????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //????????? volley??? ???????????? ????????? ???
                LoginRequest loginRequest = new LoginRequest(mID, mPW, Request.Method.POST, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });


        // ????????? ??????
        btn_findid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogidfind idfinddialog = new Dialogidfind(LoginActivity.this);
                idfinddialog.calldialog();
            }
        });


        // ???????????? ??????
        btn_pwfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogpwfind dialogpwfind = new Dialogpwfind(LoginActivity.this);
                dialogpwfind.calldialog();
            }
        });

    }


    public void socketconn(String mid){
        ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                // ???????????? ??????????????? ??? ???????????? ?????????
                // ????????? ????????? ??????????????? ??????
                Log.e("CChatroomAct","seviceconnect");
                MySocketService.LocalBinder mb = (MySocketService.LocalBinder) service;
                socketService = mb.getService(); // ???????????? ???????????? ????????? ????????????
                socketService.setMid(mid);
                socketService.serverconn();
            }

            public void onServiceDisconnected(ComponentName name) {
                // ???????????? ????????? ????????? ??? ???????????? ?????????
            }
        };

        Intent intent2 = new Intent(LoginActivity.this, MySocketService.class);
        bindService(intent2,mConnection,BIND_AUTO_CREATE); // startService ?????? ????????? ??????
        startService(intent2);
    }


    public class SessionCallback implements ISessionCallback {
        // ???????????? ????????? ??????
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // ???????????? ????????? ??????
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // ????????? ?????? ??????
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "????????? ?????? ??????: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "????????? ?????? ?????? ??????: " + errorResult);
                        }

                        @Override
                        public void onSuccess(MeV2Response result) {
                            Long snsid = result.getId();
                            // ??? ?????? ??? ????????????????????? ???????????? ?????? ??????
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.e("json", response);
                                        JSONObject jsonObject = new JSONObject(response);
                                        Log.e("json",String.valueOf(jsonObject));
                                        boolean haveid = jsonObject.getBoolean("haveid");
                                        if (haveid) { // ????????? snsid ???????????? ?????? ?????? -> ????????? ????????????
                                            String mID = jsonObject.getString("loginid");
                                            loginedit.putBoolean("dologin",true);
                                            loginedit.putString("logintype","kakao");
                                            loginedit.putString("id",mID);
                                            loginedit.commit();

                                            Toast.makeText(LoginActivity.this, "?????? ???????????? ????????????????????????..????????? ??????", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, RunMenuActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else { // ????????? snsid ???????????? ?????? ?????? -> ???????????? ????????????
                                            Toast.makeText(LoginActivity.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, snsjoinActivity.class);
                                            intent.putExtra("snsid",snsid);
                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            //????????? volley??? ???????????? ????????? ???
                            IdchkRequest idchkRequest = new IdchkRequest(null,String.valueOf(snsid), Request.Method.POST, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                            queue.add(idchkRequest);

                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {
                                // ?????????
                                String email = kakaoAccount.getEmail();
                                Gender gender = kakaoAccount.getGender();

                                if(gender != null){
                                    Log.i("KAKAO_API", "gender: " + gender);
                                }

                                if (email != null) {
                                    Log.i("KAKAO_API", "email: " + email);

                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ??????
                                    // ???, ?????? ????????? ???????????? ????????? ????????? ?????? ???????????? ????????? ????????? ????????? ???????????? ???????????? ?????????.

                                } else {
                                    // ????????? ?????? ??????
                                }

                                // ?????????
                                Profile profile = kakaoAccount.getProfile();

                                if (profile != null) {
                                    Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                    Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ?????? ??????

                                } else {
                                    // ????????? ?????? ??????
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.clearCallbacks();
        sessionCallback = null;
    }

}





