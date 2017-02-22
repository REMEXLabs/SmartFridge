package com.example.poiz.fridgetablet.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.activitys.MainActivity;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Categorie;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.dataBase.DatabaseHandler;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by poiz on 01.09.2016.
 */

/**
 * Fragment hold by the MainActivity
 * fragment wich shows all stored Products
 */
public class Fragment_ViewProducts extends Fragment implements AsyncResponse,AdapterView.OnItemSelectedListener {

    Util util;
    ScrollView products_ScrollView;
    LinearLayout scrollViewChild;
    LayoutInflater inflater;
    List<View> views;
    Spinner sortDropDown;
    Spinner categoriesDropDown;
    List<String> sortOptionsList;
    List<String> CategorieList;
    List<Product> productList = null;
    private ArrayAdapter<String> dataAdapterSort;
    private ArrayAdapter<String> dataAdapterCat;
    private SimpleDateFormat dateFormatter;
    private List<Categorie> categorieLisObjt;
    AsyncTask searchTask = null;
    AsyncTask categoriesTask = null;
    LinearLayout        rootLayout;
    DatabaseHandler dbHelper;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentActivity faActivity  = (FragmentActivity)    super.getActivity();
        rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_viewproducts, container, false);



        products_ScrollView = (ScrollView) rootLayout.findViewById(R.id.productsScrollView);



        scrollViewChild = new LinearLayout(super.getActivity());
        scrollViewChild.setOrientation(LinearLayout.VERTICAL);

        products_ScrollView.addView(scrollViewChild);

        sortDropDown = (Spinner)  rootLayout.findViewById(R.id.sort);
        sortDropDown.setOnItemSelectedListener(this);

        categoriesDropDown = (Spinner)  rootLayout.findViewById(R.id.categorieSpinner);
        categoriesDropDown.setOnItemSelectedListener(this);


        CategorieList = new ArrayList<>();
        CategorieList.add("Alle");

        categorieLisObjt = new ArrayList<>();
        categorieLisObjt.add(new Categorie("Alle",0));

        sortOptionsList = new ArrayList<>();
        sortOptionsList.add("Name");
        sortOptionsList.add("Lagerdatum");
        sortOptionsList.add("Haltbarkeitsdatum");


        dataAdapterSort = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, sortOptionsList);
        dataAdapterCat = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, CategorieList);

        // Drop down layout style - list view with radio button
        dataAdapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sortDropDown.setAdapter(dataAdapterSort);
        categoriesDropDown.setAdapter(dataAdapterCat);

        categoriesTask = new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"013");

        this.inflater = inflater;
        views = new ArrayList<>();
        util = new Util(new DatabaseHandler(this.getContext()));
        dbHelper = new DatabaseHandler(this.getContext());
       searchTask = new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"005"+dbHelper.getCountAndStoreID().toJSON());
      /* try {
            productList = dbHelper.getAllProductsByCategorie(0);
            fillScrollView(productList);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return rootLayout;
    }


    /**
     * Fills the Scrollview wich all Childviews. A childview holds the Informations of a Product
     * @param products list of all products wich want to be shown in the view
     */
    private void fillScrollView(List<Product> products){
        scrollViewChild.removeAllViews();
        for (Product p : products) {
            Log.d("fillScr",""+p.getName());
            dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
            View view = inflater.inflate(R.layout.listviewelement_viewproducts, null);
            view.setTag(p);
            TextView prodName = (TextView) view.findViewById(R.id.ProdName);
            TextView storeDate = (TextView) view.findViewById(R.id.storeDate);
            TextView expDate = (TextView) view.findViewById(R.id.expDate);
            ImageView expIndicatorImg = (ImageView) view.findViewById(R.id.colorImg);
            ImageView catImg = (ImageView) view.findViewById(R.id.prodImg);
            setCategorieImg(p.getCatID(),catImg);
           if(p.getDaysLeft()<=3){
               expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg","drawable", super.getActivity().getPackageName() ));
            }else{
               if(p.getDaysLeft()<=7){
                   expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg_yellow","drawable", super.getActivity().getPackageName() ));
               }else{
                   expIndicatorImg.setImageResource(getResources().getIdentifier("@drawable/viewproducts_listviewelement_colorimg_green","drawable", super.getActivity().getPackageName() ));
               }
           }


            prodName.setText(p.getName());
            storeDate.setText(dateFormatter.format(p.getStoreDate()));
            expDate.setText(dateFormatter.format(p.getExpDate()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).openFragment_AddProducts((Product)v.getTag());
                }
            });


            scrollViewChild.addView(view);




        }
    }

    /**
     * overwrtitten Method of the interface Asynctesponse wich gets called after SocketclientAsynctask is finished
     * @param output the from the server received message
     */
    @Override
    public void processFinish(String output) {

        Log.d("ViewProdfragment",output);

        String taskCode = "";
        String json = output.substring(3,output.length());
        if(output.length()>=3){
            taskCode = output.substring(0,3);
        }
        if((taskCode.equals("005")||taskCode.equals("006")||taskCode.equals("007")||taskCode.equals("008")||taskCode.equals("009"))&&!json.equals("fail") ){
            Log.d("ViewProdfragment","got it" + taskCode);
            try {
                try {

                    productList = util.getProductsFromJson(json);
                    Collections.sort(productList,new Product.ProductNameComparator());
                    fillScrollView(productList);
                    Log.d("ViewProdfragment","filled");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(taskCode.equals("013")){

            try {
                initCategories(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initiates the Categories wich were received by the server
     * @param categoriesJson object of a categorie in json
     * @throws JSONException
     */
    public void initCategories(String categoriesJson) throws JSONException {
        JSONArray mArray = new JSONArray(categoriesJson);
        for(int i = 0;i< mArray.length();i++){
            JSONObject jsonObj = new JSONObject(mArray.getString(i));
            CategorieList.add(jsonObj.getString("categorieName"));
            categorieLisObjt.add(new Categorie(jsonObj.getString("categorieName"),jsonObj.getInt("categorieID")));
        }
        dataAdapterCat.notifyDataSetChanged();
    }


    /**
     * gets called If the Users clicks an an option of the Spinner to Sort the Products
     * @param adapterView
     * @param view
     * @param i i = 0 -> sort collection by name, 1 -> by storedate, 2->expiredate
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.sort)
        {
            if(productList != null) {
                switch (i) {
                    case 0:
                        Collections.sort(productList,new Product.ProductNameComparator());
                        break;

                    case 1:
                        Collections.sort(productList,new Product.ProductStoreDateComparator());
                        break;
                    case 2:
                        Collections.sort(productList,new Product.ProductExpDateComparator());
                        break;
                    default:
                        break;
                }
                fillScrollView(productList);
            }


        }
        else if(spinner.getId() == R.id.categorieSpinner)
        {

           // searchTask = new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"00"+(i+5));
            try {
                productList = dbHelper.getAllProductsByCategorie(i);
                fillScrollView(productList);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Sets the Categorie Image of a Product
     * @param catID id of the categorie
     * @param catImg ImageView where an image needs to be declared
     */
    private void setCategorieImg(int catID,ImageView catImg){

        switch(catID)
        {
            case 0:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_mothing_small","drawable", super.getActivity().getPackageName() ));
                break;
            case 1:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_meatandfish_small","drawable", super.getActivity().getPackageName() ));
                break;
            case 2:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_milkandegg_small","drawable", super.getActivity().getPackageName() ));
                break;
            case 3:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_fruitandveg_small","drawable", super.getActivity().getPackageName() ));
                break;
            case 4:
                catImg.setImageResource(getResources().getIdentifier("@drawable/cat_img_others_small","drawable", super.getActivity().getPackageName() ));
                break;
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
     * onPause
     */
    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");

        super.onPause();
    }

}
