package com.example.myapplication.Profile;
import com.android.volley.Request;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addshoes extends AppCompatActivity {

    EditText edit_shoesname;
    Button btn_reg;
    ImageView btn_camera;
    File photofile;
    String mCurrentPhotoPath;
    ImageView viewshoe;
    SharedPreferences loginshared;
    String mid;
    Uri imgurl;
    ImageView btn_setdistance;
    TextView edit_shoedistacne;
    String result;
    int gdis;
    Boolean setd;
    Boolean setn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.addshoes);
        btn_camera = findViewById(R.id.btn_camera);
        viewshoe = findViewById(R.id.viewshoe);
        btn_reg = findViewById(R.id.btn_reg);
        edit_shoesname = findViewById(R.id.edit_shoename);
        btn_setdistance = findViewById(R.id.btn_setshoedistance);
        edit_shoedistacne = findViewById(R.id.edit_shoedistacne);


        loginshared = getSharedPreferences("Login", MODE_PRIVATE);
        mid = loginshared.getString("id", null);
        result = null;

        btn_setdistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new customdialog().calldialog();
                if(result != null) {
                    edit_shoedistacne.setText(result + "km");
                }

            }
        });



        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final CharSequence[] oItems = {"?????? ??????", "?????? ?????????"};

                                AlertDialog.Builder oDialog = new AlertDialog.Builder(addshoes.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("????????? ???????????????");
                oDialog.setItems(oItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (oItems[which].equals("?????? ??????")) {
                                         int permission = ContextCompat.checkSelfPermission(addshoes.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                         int permission2 = ContextCompat.checkSelfPermission(addshoes.this, Manifest.permission.CAMERA);
                                            // ????????? ??????????????? ??????
                                            if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
                                                // ??????????????? ?????????????????? ????????? ????????????
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    // ?????? ??????(READ_PHONE_STATE??? requestCode??? 1000?????? ??????
                                                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
                                                } return;
                                            }else{
                                                Intent captureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                // ????????? ????????? ??????
                                                File tempDir = getCacheDir();
                                                String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
                                                String fileName = "running"+timeStamp;

                                                File imageDir = null;
                                                try {
                                                    imageDir = File.createTempFile(
                                                            fileName,       // ?????? ??????
                                                            ".jpg",     // ?????? ??????
                                                            tempDir
                                                    );
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                mCurrentPhotoPath = imageDir.getAbsolutePath();
                                                Log.e("photopath",mCurrentPhotoPath);
                                                photofile = imageDir;

                                                if(photofile != null){
                                                        // Uri ????????????
                                                        Uri photoURI = FileProvider.getUriForFile(addshoes.this,"com.example.myapplication.fileprovider",photofile);
                                                        captureintent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                                                        startActivityForResult(captureintent, 101);
                                                }
                                            }


                        } else if (oItems[which].equals("?????? ?????????")) {
                                    int permission = ContextCompat.checkSelfPermission(addshoes.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                                    // ????????? ??????????????? ??????
                                    if (permission == PackageManager.PERMISSION_DENIED) {
                                        // ??????????????? ?????????????????? ????????? ????????????
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            // ?????? ??????(READ_PHONE_STATE??? requestCode??? 1000?????? ??????
                                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                                        }
                                        return;
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_PICK);
                                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                        startActivityForResult(intent, 102);
                                    }
                        }
                    }
                });
                oDialog.setCancelable(false);
                oDialog.show();
            }
        });
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!edit_shoesname.getText().toString().equals("") && !edit_shoedistacne.getText().toString().equals("")){
                    addshoes(edit_shoesname.getText().toString());
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(addshoes.this);
                    builder.setTitle("?????? ?????? ??????????????? ??????????????????.")        // ?????? ??????
                            .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                            .setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                // ?????? ?????? ????????? ??????, ????????? ???????????????.
                                public void onClick(DialogInterface dialog, int whichButton){
                                    //????????? ?????? ???????????? ???????????? ?????????.
                                }
                            });

                    AlertDialog dialog = builder.create();    // ????????? ?????? ??????
                    dialog.show();    // ????????? ?????????
                }
            }
        });


        super.onCreate(savedInstanceState);
    }


     public class customdialog {
            Dialog dig;
            Button btn_setdistance;
            EditText picker1_time;
            EditText picker2_time;


            public void calldialog() {
                dig = new Dialog(addshoes.this);
                // ??????????????? ??????????????? ?????????.
                dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // ????????? ?????????????????? ??????????????? ????????????.
                dig.setContentView(R.layout.pickdistance_dialog);
                picker1_time = dig.findViewById(R.id.picker1_time);
                picker2_time = dig.findViewById(R.id.picker2_time);
                btn_setdistance = dig.findViewById(R.id.btn_setdistance);

                btn_setdistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dig.dismiss();
                        result = picker1_time.getText().toString()+"."+picker2_time.getText().toString();
                        edit_shoedistacne.setText(result+" km");
                        gdis = (int) (Float.parseFloat(result) * 1000);
                    }
                });

                dig.show();
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
                    if(requestCode == 101 && resultCode == RESULT_OK) {
                    Uri test = Uri.fromFile(new File(mCurrentPhotoPath));
                        viewshoe.setVisibility(View.VISIBLE);
                        btn_camera.setVisibility(View.INVISIBLE);
                        Glide.with(addshoes.this)
                                .load(test)
                                .into(viewshoe);
                             saveFile(test);
                        Log.e("imgurl",imgurl+"");
                    }

               if (requestCode == 102) {
                   if (resultCode == RESULT_OK) {
                       Uri fileUri = data.getData();
                       Log.e("fileurl",String.valueOf(fileUri));
                       try {
                           btn_camera.setVisibility(View.INVISIBLE);
                           viewshoe.setVisibility(View.VISIBLE);
                                   Glide.with(addshoes.this)
                                   .load(fileUri)
                                   .into(viewshoe);

//                       adapter.additem(fileUri,addshoes.this);
                       } catch (Exception e) {
                       Toast.makeText(getApplicationContext(), "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                       }
                       imgurl = fileUri;
                   }

               }
    }


    // ?????? ??????
    private void saveFile(Uri image_uri) {
        String fileName;
            ContentValues values = new ContentValues();
            fileName =  "Run"+System.currentTimeMillis()+".png";
            values.put(MediaStore.Images.Media.DISPLAY_NAME,fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            ContentResolver contentResolver = getContentResolver();
            Uri item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        imgurl = item;
            try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {
            Log.d("Run", "null");
            } else {
            byte[] inputData = getBytes(image_uri);
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(inputData);
                fos.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                contentResolver.update(item, values, null, null);
            }
                // ??????
                galleryAddPic(fileName);
            }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("Run", "FileNotFoundException  : "+e.getLocalizedMessage());
            } catch (Exception e) {
                Log.d("Run", "FileOutputStream = : " + e.getMessage());
            }
            }

    private void galleryAddPic(String Image_Path) {

                String SaveFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Image_Path;
                Log.e("galleryaddpic_Image_Path",Image_Path);

                File file = new File(Image_Path);
                MediaScannerConnection.scanFile(addshoes.this,
                new String[]{file.toString()},
                null, null);

                Log.e("save",file.toString());
            }


    public byte[] getBytes(Uri image_uri) throws IOException {
        InputStream iStream = getContentResolver().openInputStream(image_uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024; // ?????? ??????
        byte[] buffer = new byte[bufferSize]; // ?????? ??????

        int len = 0;
        // InputStream?????? ????????? ??? ?????? ????????? ????????? ????????? ??????.
        while ((len = iStream.read(buffer)) != -1)
            byteBuffer.write(buffer, 0, len);
        return byteBuffer.toByteArray();
    }


    public void addshoes(String shoesname){
            // ????????????????????? ?????? ???????????? ?????? php ?????? ??????
            String serverUrl="http://3.12.49.32/addshoes.php";
            ProgressDialog progressDialog;
                    progressDialog = ProgressDialog.show(addshoes.this,
                    "??????????????????..", null, true, true);
                    progressDialog.show();

            // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
            SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
            try {
            JSONObject jsonObject = new JSONObject(response);
            Log.e("json",jsonObject+"");
                boolean success = jsonObject.getBoolean("success");
                if(success) {
                    Toast.makeText(addshoes.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intentR = new Intent();
                            setResult(RESULT_OK,intentR); //????????? ??????
                    finish();
                } else {
                    Toast.makeText(addshoes.this, "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                } catch (Exception e) {
                e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
            Toast.makeText(addshoes.this, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
            });

            //????????? ?????? ?????? (pathList??? ????????? ????????? ?????? uri string ?????????)
                    // uri ?????? ?????? ?????????
        if(imgurl!= null){
            String[] proj= {MediaStore.Images.Media.DATA};
            CursorLoader loader= new CursorLoader(addshoes.this, imgurl, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            if(cursor != null){
                int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String abUri= cursor.getString(column_index);
                Log.e("aburi",abUri);
                cursor.close();

                // ????????? ?????? ??????
                smpr.addFile("image", abUri);
            }else{
            }
        }

            Log.e("shoename",shoesname+"shoe");
            // ????????? ????????? ?????????
            // ?????? ????????? ?????? ???????????? ??????
            smpr.addStringParam("id",mid);
            smpr.addStringParam("gdistance", String.valueOf(gdis));
            smpr.addStringParam("shoesname", shoesname);

            // ????????? ????????? ????????? ?????? ??????
            RequestQueue requestQueue = Volley.newRequestQueue(addshoes.this);
            requestQueue.add(smpr);


    }
}
