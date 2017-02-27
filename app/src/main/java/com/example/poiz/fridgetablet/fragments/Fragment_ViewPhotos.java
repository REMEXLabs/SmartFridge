package com.example.poiz.fridgetablet.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Picture;
import com.example.poiz.fridgetablet.dataBase.DatabaseHandler;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment hold by the MainActivity
 * Fragment to show the Photos of the Fridge
 */
public class Fragment_ViewPhotos extends Fragment implements AsyncResponse {
    private Util util;
    List<Picture> pics;
    ImageView fridgeImgView;
    ImageView freezerImgView;
    TextView fridgeTxt;
    TextView freezerTxt;
    AsyncTask getPhotosTask;
    DatabaseHandler dbHelper;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        util = new Util();

        Bundle bundle = this.getArguments();
        FragmentActivity faActivity = (FragmentActivity) super.getActivity();
        LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_viewphotos, container, false);

        fridgeImgView = (ImageView)rootLayout.findViewById(R.id.fridgeImg);
        freezerImgView = (ImageView)rootLayout.findViewById(R.id.freezerImg);

        fridgeTxt = (TextView)rootLayout.findViewById(R.id.fridgetxt);
        freezerTxt = (TextView)rootLayout.findViewById(R.id.freezertxt);
        dbHelper = new DatabaseHandler(this.getContext());

        try {
            getPhotosTask= new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"017"+dbHelper.getPicTimestampsJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        pics = dbHelper.getPictures();
        if(pics!=null){
            showPics();
        }
        return rootLayout;
    }

    private void showPics(){

            byte[] fridgeBytes = pics.get(0).getImageBytes();
            byte[] freezerBytes = pics.get(1).getImageBytes();
            Bitmap fridgeImg = BitmapFactory.decodeByteArray(fridgeBytes, 0, fridgeBytes.length);
            Bitmap freezerImg = BitmapFactory.decodeByteArray(freezerBytes, 0, freezerBytes.length);
            Log.d("", "hier das bild: " + pics.get(0).getImageBytes() + " " + fridgeBytes.length);
            Log.d("", "hier das bild: " + pics.get(1).getImageBytes());

            DisplayMetrics dm = new DisplayMetrics();
            super.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            System.out.println(Arrays.toString(fridgeBytes));

            fridgeImgView.setMinimumHeight(dm.heightPixels);
            fridgeImgView.setMinimumWidth(dm.widthPixels);
            fridgeImgView.setImageBitmap(fridgeImg);

            freezerImgView.setMinimumHeight(dm.heightPixels);
            freezerImgView.setMinimumWidth(dm.widthPixels);
            freezerImgView.setImageBitmap(freezerImg);


    }

    @Override
    public void processFinish(String output) {
        String taskCode = "";
        //longInfo(output);
        String json = output.substring(3,output.length());
        if(output.length()>=3){
            taskCode = output.substring(0,3);
            Log.d("loginf", "taskconde is: "+taskCode);
            Log.d("","Hell yeah tasnk code: " + taskCode);
        }
        if(taskCode.equals("017")&& json.length() > 10 ){
            try {
                Log.d("loginf", "in 017");
                pics = util.getPicturesFromJson(json);
                for(int i=0;i<pics.size();i++){
                    dbHelper.deletePhoto(pics.get(i).getName());
                    dbHelper.inserPicture(pics.get(i));
                }
                if(pics != null){
                    pics = dbHelper.getPictures();
                    showPics();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }
}
