package com.example.poiz.fridgetablet.fragments;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Categorie;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.firebase.MyFirebaseMessagingService;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Not In use
 *//*
public class Fragment_OpenProducts extends Fragment implements AsyncResponse,AdapterView.OnItemSelectedListener,View.OnClickListener {

   / private MyReceiver myReceiver;

    private List<Categorie> categorieLisObjt;
    private List<String> categorieListStr;
    private ArrayAdapter<String> dataAdapter;
    Util util;
    Spinner spinner;
    public EditText ProductName;
    AsyncTask getAllCategoriesTask;

    private DatePickerDialog expDateDialog;
    private EditText expDateTxt;
    private SimpleDateFormat dateFormatter;

    private ImageButton manAddButton;
    private ImageButton manDelButton;

    private ImageView titel;

    private static final String TAG  = "DelProductsFrag";

    Product product = null;

    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        util = new Util();

        bundle = this.getArguments();



        FragmentActivity faActivity  = (FragmentActivity)    super.getActivity();
        LinearLayout rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_addproducts, container, false);

        titel = (ImageView)rootLayout.findViewById(R.id.titelimg);

        ProductName = (EditText)rootLayout.findViewById(R.id.productnametxt);

        manAddButton = (ImageButton)rootLayout.findViewById(R.id.manualButton);
        manDelButton = (ImageButton)rootLayout.findViewById(R.id.mandelbtn);

        expDateTxt = (EditText) rootLayout.findViewById(R.id.expDate);
        expDateTxt.setInputType(InputType.TYPE_NULL);


        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
        Calendar newCalendar = Calendar.getInstance();
        expDateDialog = new DatePickerDialog(super.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                expDateTxt.setText(dateFormatter.format(newDate.getTime()));
                if(product!=null)
                    product.setShelflife(util.getDateDiff(util.getTodaysDate(),newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        // Spinner element
        spinner = (Spinner) rootLayout.findViewById(R.id.categoriesspinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        categorieLisObjt = new ArrayList<>();
        categorieListStr = new ArrayList();


        getAllCategoriesTask = new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"013");

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, categorieListStr);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        initAsOpenProduct();

        return rootLayout;
    }





    @Override
    public void processFinish(String output){

        String taskCode = "";
        String json = output.substring(3,output.length());
        if(output.length()>=3){
            taskCode = output.substring(0,3);
        }
        if(taskCode.equals("013")){

            try {
                initCategories(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(taskCode.equals("016")){

           product = util.getProductFromJson(json);
            Log.d(TAG,"jason ist: "+json);
            setUiValues(product.getName(), product.getCatID(),util.getTodaysDate(), product.getShelflife());


        }

    }

    public void initCategories(String categoriesJson) throws JSONException {
        JSONArray mArray = new JSONArray(categoriesJson);
        categorieListStr.add("Noch keine Kategorie");
        for(int i = 0;i< mArray.length();i++){
            JSONObject jsonObj = new JSONObject(mArray.getString(i));
            categorieListStr.add(jsonObj.getString("categorieName"));
            categorieLisObjt.add(new Categorie(jsonObj.getString("categorieName"),jsonObj.getInt("categorieID")));
        }
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyFirebaseMessagingService.SEND_STORED_ITEM);
        super.getActivity().registerReceiver(myReceiver, intentFilter);

        super.onStart();
    }
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "011");
        super.getActivity().unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(view == expDateTxt) {
            expDateDialog.show();
        }
    }

    public void setUiValues(String productName, int CategorieID,Date storeDate, int shelflife){


        //setDate
        util.addDaysToDate(storeDate,shelflife);
        expDateTxt.setText(dateFormatter.format(util.addDaysToDate(storeDate,shelflife)));

        //setName
        ProductName.setText(productName);

        //setCategorie
        if(CategorieID != 0){
            spinner.setSelection(CategorieID);
        }
    }



    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }

    private void initAsOpenProduct(){
        titel.setImageResource(getResources().getIdentifier("@drawable/titel_lastopened","drawable", super.getActivity().getPackageName() ));
        ProductName.setEnabled(false);
        ProductName.setFocusable(false);
        expDateTxt.setEnabled(false);
        expDateTxt.setFocusable(false);
        spinner.setEnabled(false);
        spinner.setFocusable(false);
        manAddButton.setVisibility(View.GONE);
        manDelButton.setVisibility(View.GONE);
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "011");


    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            ProductName.setEnabled(false);
            ProductName.setFocusable(false);
            // TODO Auto-generated method stub
            String itemJson = arg1.getStringExtra("ITEM");
            Log.d(TAG, "in addproducts angekommen  " + itemJson);
            product = util.getProductFromJson(itemJson);
            setUiValues(product.getName(),product.getCatID(),util.getTodaysDate(),product.getShelflife());
        }
    }

}
*/