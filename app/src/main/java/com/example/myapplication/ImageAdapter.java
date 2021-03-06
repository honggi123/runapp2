package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

    ArrayList<String> arr_photopath;
    Context context;
    ArrayList<String> arr_storagepath;

    public ImageAdapter(ArrayList<String> list,ArrayList<String> arr_storagepath, Context context){
        arr_photopath = list;
        this.arr_storagepath = arr_storagepath;
        this.context = context;
    }

    public ImageAdapter(ArrayList<String> arr_imgpath,Context context) {
        this.arr_photopath = arr_imgpath;
        this.context = context;
    }

    public void additem(Uri strimg,Context context){
        Log.e("strimg.getPath()",strimg.getPath());
        arr_photopath.add(String.valueOf(strimg));
        this.context = context;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int imgpos =position;

        Glide.with(context)
                .load(arr_photopath.get(position))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removcedialog(imgpos);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arr_photopath.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView image;

        public Holder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_item);
        }
    }

    public Bitmap showphoto(String photopath) throws IOException {
        File file = new File(photopath);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
        return bitmap;
    }


    public void removcedialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????? ??????")        // ?????? ??????
                .setMessage("?????? ????????? ?????????????????????????")        // ????????? ??????
                .setCancelable(false)        // ?????? ?????? ????????? ?????? ?????? ??????
                .setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    // ?????? ?????? ????????? ??????, ????????? ???????????????.
                    public void onClick(DialogInterface dialog, int whichButton){
                        arr_photopath.remove(position);
                        notifyDataSetChanged();
                        //????????? ?????? ???????????? ???????????? ?????????.
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener(){
                    // ?????? ?????? ????????? ??????, ?????? ???????????????.
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        //????????? ?????? ???????????? ???????????? ?????????.
                    }
                });

        AlertDialog dialog = builder.create();    // ????????? ?????? ??????
        dialog.show();    // ????????? ?????????
    }


}
