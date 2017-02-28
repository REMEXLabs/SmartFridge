package com.example.poiz.fridgetablet.fragments;

import android.app.DatePickerDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.activitys.MainActivity;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Categorie;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.firebase.MyFirebaseMessagingService;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by poiz on 24.08.2016.
 */

/**
 * Fragment hold by fragment_ManageProductsRoot
 * Fragment to Manage Products (Store or Edit)
 */
public class Fragment_ManageProducts extends Fragment implements AsyncResponse,AdapterView.OnItemSelectedListener,View.OnClickListener {

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

    private ImageView catImg;

    private ImageView titel;
    public FragmentActivity faActivity;
    private static final String TAG  = "AddProductsFrag";

    Product product = null;
    LinearLayout topElement;
    Bundle bundle;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        util = new Util();

        bundle = this.getArguments();

        faActivity  = (FragmentActivity)    super.getActivity();
        LinearLayout rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_addproducts, container, false);
        topElement = (LinearLayout)rootLayout.findViewById(R.id.topElement);
        titel = (ImageView)rootLayout.findViewById(R.id.titelimg);

        catImg = (ImageView)rootLayout.findViewById(R.id.catImgView);
        catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_mothing","drawable", super.getActivity().getPackageName() ));

        ProductName = (EditText)rootLayout.findViewById(R.id.productnametxt);

        manAddButton = (Button)rootLayout.findViewById(R.id.manualButton);
        manDelButton = (Button)rootLayout.findViewById(R.id.mandelbtn);

        expDateTxt = (EditText) rootLayout.findViewById(R.id.expDate);
        expDateTxt.setInputType(InputType.TYPE_NULL);
        expDateTxt.requestFocus();


        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
        expDateTxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        expDateDialog = new DatePickerDialog(super.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                expDateTxt.setText(dateFormatter.format(newDate.getTime()));
                updateDateOrShelflife(newDate.getTime());

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


        ProductName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    checkForProduct();
                    return true;
                }
                return false;
            }
        });

        manAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = null;
                setUiValues("", 0,util.getTodaysDate() ,0);
                ProductName.setHint("Produktname eingeben");


                ProductName.setEnabled(true);
                ProductName.setFocusable(true);
                manDelButton.setVisibility(View.GONE);

            }
        });

        manDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delProduct();
                if(bundle != null) {
                    ((MainActivity) getActivity()).openFragment_ViewProducts();
                }else{
                    manDelButton.setVisibility(View.GONE);
                }

               //
                Log.d("","zu löschender eintrag: "+product.getStoreid());

            }
        });


         if(bundle != null){
            initAsViewProduct();


        }else{
            initAsAddProduct();

        }

        return rootLayout;
    }


    /**
     * Deletes the actual Product
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

    /**
     * Updates the Expiredate of the actual Product
     * @param date new expiredate of a product
     */
    private void updateExpDateOfExistingProduct(Date date){
        JSONObject jsonObject= new JSONObject();

        if(product!=null) {
            try {
                jsonObject.put("storedID", product.getStoreid());
                jsonObject.put("newExpDate", date);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "019" + jsonObject.toString());
        }
    }

    /**
     * updates the Shelflife of the actual product
     * @param d new amount of days of the shelflife
     */
    private void updateDateOrShelflife(Date d){
        if(product!=null && bundle == null) // bundle != 0 -> edit Existing Product
        {
            product.setShelflife(util.getDateDiff(util.getTodaysDate(),d));
            try {
                updateShellife();
                updateExpDateOfExistingProduct(d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            updateExpDateOfExistingProduct(d);
        }
    }

    /**
     * After the User entered a Productname, this Method asks the server if the Product exists. If not the Server ctreates a new Entry and
     * Sends the Informations of the New Product to the Client
     */
    private void checkForProduct(){
            JSONObject jsonObject= new JSONObject();

            try {
                jsonObject.put("productName", ProductName.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "016" + jsonObject.toString());

        manDelButton.setVisibility(View.VISIBLE);
        ProductName.setEnabled(false);
        ProductName.setFocusable(false);
    }

    /**
     * gets Calld if the user trys to add a Product by Voice.
     * @param name name of a product
     */
    public void addProductByVoice(String name){

        if(bundle==null) {
            ProductName.setText(name);
            ProductName.setEnabled(false);
            ProductName.setFocusable(false);
            checkForProduct();
        }
    }

    /**
     * gets Calld if the user trys to set the Categorie of a Product by Voice.
     * @param categorie name of a categorie
     */
    public void setCategorieByVoice(String categorie) throws JSONException {
        Log.d("edit","erfolgreich in manage");
        if(product!= null) {
            Log.d("edit","erfolgreich produkt nicht null");
            int i = 0;
            for (Categorie p : categorieLisObjt) {
                i++;
                if (p.getCategorieName().toUpperCase().contains(categorie.toUpperCase())) {
                    spinner.setSelection(i);
                    updateCategorie();
                    break;
                }
            }
        }

    }

    /**
     * sets the specific Categorie Image of a Product
     * @param catID id of a categorie
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
     * ets Calld if the user trys to set the ExpireDate of a Product by Voice.
     * @param dateString date in String format
     */
    public void setDateByVoice(String dateString){
        String dayStr = "";
        String monthStr = "";
        String yearStr = "";
        try{
            dayStr = dateString.substring(0, dateString.indexOf(" "));
            monthStr = dateString.substring(dateString.indexOf(" ") + 1, dateString.lastIndexOf(" "));
            yearStr = dateString.substring(dateString.lastIndexOf(" ") + 1, dateString.length());
            int day = 0;
            int year = 0;
            Date newDate = null;
            try
            {
                day = Integer.parseInt(dayStr);
                year = Integer.parseInt(yearStr);

            } catch (NumberFormatException e){}
            SimpleDateFormat df = new SimpleDateFormat("d MMMM yyyy",Locale.GERMAN);
            try {
                newDate = df.parse(day+" "+monthStr+" "+year);
                expDateTxt.setText(dateFormatter.format(newDate.getTime()));
                updateDateOrShelflife(newDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }catch(StringIndexOutOfBoundsException e){

        }

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
            Log.d(TAG,"Hell yeah tasnk code: " + taskCode);
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
            if(product!=null)
            setUiValues(product.getName(), product.getCatID(),util.getTodaysDate(), product.getShelflife());


        }

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
            categorieListStr.add(util.strToUtf8(jsonObj.getString("categorieName")));
            // categorieListStr.add(jsonObj.getString("categorieName"));
            categorieLisObjt.add(new Categorie(util.strToUtf8(jsonObj.getString("categorieName")),jsonObj.getInt("categorieID")));
            Log.d("umlaute",util.strToUtf8(jsonObj.getString("categorieName")));
        }
        dataAdapter.notifyDataSetChanged();
        if(product!=null){ spinner.setSelection(product.getCatID());setCategorieImg(product.getCatID());}
    }

    /**
     * onStart. Registers the Broadcastreceiver
     */
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

    /**
     * onStop. unregisters the Broadcastreceiver
     */
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "011");
        super.getActivity().unregisterReceiver(myReceiver);
        super.onStop();
    }

    /**
     * gets called if the User clicks a categorie om the spiinner
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG,"spinner: " + item);
        if(product != null && i != 0){
            Log.d(TAG,"spinner1: " + item);
            product.setCatID(i);
            try {
                updateCategorie();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * onNothingSelected
     * @param adapterView
     */
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
        expDateTxt.setText(dateFormatter.format(util.addDaysToDate(storeDate,shelflife)));

        //setName
        ProductName.setText(productName);


        //setCategorie
        if(CategorieID != 0){
           spinner.setSelection(CategorieID);
            setCategorieImg(product.getCatID());
        }
    }

    /**
     * updates the Shelflife of a Product
     * @throws JSONException
     */
    private void updateShellife() throws JSONException{
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("productID", product.getId());
        jsonObject.put("shelflife", product.getShelflife());

        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"015"+jsonObject.toString());


    }

    /**
     * updates the Categorie of a Product
     * @throws JSONException
     */
    private void updateCategorie() throws JSONException {
        setCategorieImg(product.getCatID());
        Log.d(TAG,"updateCat wurde aufgerufen ;D");
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("productID", product.getId());
        jsonObject.put("categorieID", product.getCatID());
        Log.d(TAG,"alles wirde ausgeführt");

        new SocketClientAsyncTask(this,this.getActivity()).execute("014"+jsonObject.toString());
        Log.d(TAG,"async wurde geöffnet");
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
     * is Called if the Fragment is used to Add Products
     */
    private void initAsAddProduct(){
       titel.setImageResource(getResources().getIdentifier("@drawable/titel_lastadded","drawable", super.getActivity().getPackageName() ));
        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "010");
        manDelButton.setVisibility(View.GONE);

    }


    /**
     * Gets Called if the Fragment is used to View/Edit products only
     */
    private void initAsViewProduct(){
        titel.setImageResource(getResources().getIdentifier("@drawable/titel_editprodukt","drawable", super.getActivity().getPackageName() ));
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d1 = null;
        Date d2 =null;
        try{
            d1 = sdf3.parse(bundle.getString("storeDate"));
            d2 = sdf3.parse(bundle.getString("expDate"));

        }catch (Exception e){ e.printStackTrace(); }
        product = new Product(bundle.getInt("id"),bundle.getString("name"),bundle.getInt("catID"),bundle.getInt("shelflife"),d1,d2,bundle.getInt("storeid"));

        setUiValues(product.getName(), product.getCatID(),product.getStoreDate() , 0);
        expDateTxt.setText(dateFormatter.format(d2));

        ProductName.setEnabled(false);
        ProductName.setFocusable(false);

        manAddButton.setVisibility(View.GONE);
        topElement.setVisibility(View.GONE);



    }
    public void saveProduct(String productJson){
         productJson = productJson.replace("productname","productName");
        new SocketClientAsyncTask(this,this.faActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "016" + productJson);
    }

    /**
     * broadcast receiver. if the User used the BarcodeScanner
     */
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
            saveProduct(itemJson);
            manDelButton.setVisibility(View.VISIBLE);
        }
    }

}
