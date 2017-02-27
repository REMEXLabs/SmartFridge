package com.example.poiz.fridgetablet.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment hold by the MainActivity.
 * The Fragment shows the view wich is needed to delete Products
 */
public class Fragment_DeleteProducts extends Fragment implements AsyncResponse,AdapterView.OnItemSelectedListener,View.OnClickListener {

    private MyReceiver myReceiver;

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

    private Button manAddButton;
    private Button manDelButton;
    private List<Product> productList;
    private ImageView titel;

    private ImageView catImg;

    private static final String TAG  = "DelProductsFrag";

    FragmentActivity faActivity;

    Product product = null;
    Context c;
    LayoutInflater inflater;
    Bundle bundle;


    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        util = new Util();
        c = super.getActivity();
        bundle = this.getArguments();
        this.inflater=inflater;



        faActivity  = (FragmentActivity)    c;
        LinearLayout rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_addproducts, container, false);

        titel = (ImageView)rootLayout.findViewById(R.id.titelimg);

        ProductName = (EditText)rootLayout.findViewById(R.id.productnametxt);

        manAddButton = (Button)rootLayout.findViewById(R.id.manualButton);
        manDelButton = (Button)rootLayout.findViewById(R.id.mandelbtn);

        catImg = (ImageView) rootLayout.findViewById(R.id.catImgView);
        setCategorieImg(0);

        expDateTxt = (EditText) rootLayout.findViewById(R.id.expDate);
        expDateTxt.setInputType(InputType.TYPE_NULL);


        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
        Calendar newCalendar = Calendar.getInstance();
        expDateDialog = new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {

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
        dataAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_dropdown_item, categorieListStr);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        initAsDelProduct();

        return rootLayout;
    }


    /**
     * overwrtitten Method of the interface Asynctesponse wich gets called after SocketclientAsynctask is finished
     * @param output the from the server received message
     */
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
        if(taskCode.equals("020")){

            try {
                Log.d("antwort"," "+json);
                productList = util.getProductsFromJson(json);
                if(productList.size()>1){
                    deleteProductDialog();
                }
                if(productList.size()==1){
                    setUiValues(productList.get(0).getName(),productList.get(0).getCatID(),productList.get(0).getStoreDate() , productList.get(0).getShelflife());
                    //todo löschen
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"jason ist: "+json);



        }

    }

    /**
     * If the entered Product matches more then one Product in the Datebase, this dialog is shown.
     * The dialog gives the user the oppotunity to choose wich Product he wants to delete
     */
    private void deleteProductDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(c);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Bitte Produkte wählen:");
        final Dialog myDialog = new Dialog(c);

        LinearLayout linLayout = (LinearLayout)    inflater.inflate(R.layout.dialog_viewproducts, null);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
;
        for (Product p: productList) {
            Log.d("produkte"," yes");
            View view = inflater.inflate(R.layout.listviewelement_viewproducts, null);
            view.setTag(p);
            TextView prodName = (TextView) view.findViewById(R.id.ProdName);
            TextView storeDate = (TextView) view.findViewById(R.id.storeDate);
            TextView expDate = (TextView) view.findViewById(R.id.expDate);
            ImageView expIndicatorImg = (ImageView) view.findViewById(R.id.colorImg);

            if(p.getDaysLeft()<=3){
                expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg","drawable", c.getPackageName() ));
            }else{
                if(p.getDaysLeft()<=7){
                    expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg_yellow","drawable", c.getPackageName() ));
                }else{
                    expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg_green","drawable", c.getPackageName() ));
                }
            }


            prodName.setText(p.getName());
            storeDate.setText(dateFormatter.format(p.getStoreDate()));
            expDate.setText(dateFormatter.format(p.getExpDate()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // ((MainActivity) getActivity()).openFragment_AddProducts((Product)v.getTag());
                    product = (Product)v.getTag();
                    setUiValues(product.getName(),product.getCatID(),product.getStoreDate(), product.getShelflife());
                    delProduct();
                   myDialog.dismiss();
                }
            });


            linLayout.addView(view);
        }

        myDialog.setContentView(linLayout);
        myDialog.show();


    }

    /**
     * Initiates the Categories wich were received by the server
     * @param categoriesJson
     * @throws JSONException
     */
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

    /**
     * onStart.
     * Registers the broadcast receiver
     */
    @Override
    public void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyFirebaseMessagingService.SEND_STORED_ITEM);
        c.registerReceiver(myReceiver, intentFilter);

        super.onStart();
    }

    /**
     * unreggisters the broadcastreceiver and sets the delet or store State
     */
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "011");
        c.unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * shows the dialog for the expiredate if expiredate was clicked
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view == expDateTxt) {
            expDateDialog.show();
        }
    }

    /**
     * sets the Productname, Categoriename, Store Date and Expiredate in the View
     * @param productName
     * @param CategorieID
     * @param storeDate
     * @param shelflife
     */
    public void setUiValues(String productName, int CategorieID,Date storeDate, int shelflife){


        //setDate
        util.addDaysToDate(storeDate,shelflife);
        expDateTxt.setText(dateFormatter.format(util.addDaysToDate(storeDate,shelflife)));

        //setName
        ProductName.setText(productName);

        //setCategorie
        if(CategorieID != 0){
            spinner.setSelection(CategorieID);
            setCategorieImg(CategorieID);

        }
    }

    /**
     * Sets the images for the Categorie of a Product
     * @param catID
     */
    private void setCategorieImg(int catID){
        switch(catID)
        {
            case 0:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_mothing","drawable", super.getActivity().getPackageName() ));
                break;
            case 1:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_meatandfish","drawable", super.getActivity().getPackageName() ));
                break;
            case 2:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_milkandegg","drawable", super.getActivity().getPackageName() ));
                break;
            case 3:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_fruitandveg","drawable", super.getActivity().getPackageName() ));
                break;
            case 4:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_others","drawable", super.getActivity().getPackageName() ));
                break;
        }

    }


    /**
     * onPause
     */
    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }

    /**
     * If the Fragment is used to Delete Products
     */
    private void initAsDelProduct(){
        titel.setImageResource(getResources().getIdentifier("@drawable/titel_lastdeleted","drawable", c.getPackageName() ));
        ProductName.setEnabled(false);
        ProductName.setFocusable(false);
        expDateTxt.setEnabled(false);
        expDateTxt.setFocusable(false);
        spinner.setEnabled(false);
        spinner.setFocusable(false);
        manDelButton.setVisibility(View.GONE);
        manAddButton.setVisibility(View.GONE);
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "011");


    }

    /**
     * gets called if a voicecommand to delete a product was received
     * @param name
     */
    public void deleteProductByVoice(String name){

        JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("productName", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"020"+jsonObject.toString());
    }

    /**
     * called to delete a Product from the Database of Stored Products on the serevr
     */
    private void delProduct(){
        JSONObject jsonObject= new JSONObject();

        if(product!=null) {
            try {
                jsonObject.put("storedID", product.getStoreid());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "018" + jsonObject.toString());
        }
    }

    public void deleteProduct(String productJson){
        new SocketClientAsyncTask(this,this.faActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "030" + productJson);
    }


    /**
     * Broadcastreceiver is called if a Product was deletet witch the Barcodescanner from the server
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            ProductName.setEnabled(false);
            ProductName.setFocusable(false);
            // TODO Auto-generated method stub
            String itemJson = arg1.getStringExtra("ITEM");
            deleteProduct(itemJson);
            Log.d(TAG, "in addproducts angekommen  " + itemJson);
            product = util.getProductFromJson(itemJson);
            setUiValues(product.getName(),product.getCatID(),util.getTodaysDate(),product.getShelflife());
        }
    }

}
