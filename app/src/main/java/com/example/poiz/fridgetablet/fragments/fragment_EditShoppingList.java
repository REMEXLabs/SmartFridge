package com.example.poiz.fridgetablet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.activitys.MainActivity;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.data.ShoppingList;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment hold by the MainActivity
 * The fragment shows the View wich is needed to edit a shoppinglist
 */
public class fragment_EditShoppingList extends Fragment implements AsyncResponse {

    Util util;
    ScrollView products_ScrollView;
    LinearLayout scrollViewChild;
    LayoutInflater inflater;
    List<View> views;
    private SimpleDateFormat dateFormatter;
    LinearLayout        rootLayout;
    ShoppingList sList;
    fragment_EditShoppingList f;
    List<String> responseList;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView pNametextView;
    EditText shoppingListName;
    TextView shoppingListDate;
    Button addBtn;
    Button delBtn;
    String productsJson;
    boolean newList;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        newList = bundle.getBoolean("new");

        FragmentActivity faActivity  = (FragmentActivity)    super.getActivity();
        rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_editshoppinglist, container, false);

        f = this;

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);

        products_ScrollView = (ScrollView) rootLayout.findViewById(R.id.shoppinglistScrollView);

        pNametextView = (AutoCompleteTextView)rootLayout.findViewById(R.id.autocompleteTextView);
        shoppingListName = (EditText) rootLayout.findViewById(R.id.shoppingListNameTxt);
        shoppingListDate = (TextView) rootLayout.findViewById(R.id.shoppingListCreationDate);

        addBtn = (Button) rootLayout.findViewById(R.id.addbtn);
        delBtn = (Button) rootLayout.findViewById(R.id.delBtn);

        this.inflater = inflater;

        util = new Util();

        scrollViewChild = new LinearLayout(super.getActivity());
        scrollViewChild.setOrientation(LinearLayout.VERTICAL);

        products_ScrollView.addView(scrollViewChild);

        if(newList==false) {
            sList = ((MainActivity) getActivity()).getShoppingList();
            updateUI(sList.getName(),sList.getDateOfCreation());
            fillScrollView(sList.getProducts());
        }else{
            new SocketClientAsyncTask(f,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"028"); //create new shoppinglist in db
            new SocketClientAsyncTask(f,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"029"); //get suggestions
        }

        new SocketClientAsyncTask(f,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"023"); //products for autocomplete


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    String pname= pNametextView.getText().toString();
                    int pId = getIdOfProduct(pname);
                    if(pId!=0 && pname.length()>1) {
                        closeKeyboard();
                        addProductToShoppingList(pId,pname);
                    }
                    pNametextView.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jObject = new JSONObject();
                try {
                    jObject.put("shoppinglistid",sList.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"026"+jObject.toString());
            }
        });

        shoppingListName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if(shoppingListName.getText().toString().length()>1){
                        try {
                            closeKeyboard();
                            updateShoppingListName(shoppingListName.getText().toString());
                            shoppingListName.clearFocus();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        return rootLayout;
    }

    /**
     * Adds a given Product to the Shoppinglist by its Name and its ID
     * @param pId the id of the Product which want to be added to the shoppinglist
     * @param pname the name of the product which want to be added to the shoppinglist
     * @throws JSONException
     */
    public void addProductToShoppingList(int pId, String pname) throws JSONException {
        JSONObject jObject = new JSONObject();
        jObject.put("shoppinglistid",sList.getId());
        jObject.put("productid", pId);
        sList.getProducts().add(new Product(pId, pname, 0, 0));
        fillScrollView(sList.getProducts());
        new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"024"+jObject.toString());
    }

    /**
     * Delets a given Product from the shoppinglist by its Object
     * @param p the object of the product wich want to be deleted from the shoppinglist
     */
    public void delProductFromListByProduct(Product p){
        String name = p.getName();
        JSONObject jObject = new JSONObject();
        try {

            jObject.put("productid",p.getId());
            jObject.put("shoppinglistid",sList.getId());
            sList.getProducts().remove(p);
            fillScrollView(sList.getProducts());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"022"+jObject.toString());
    }

    /**
     * Delets a Product from the shoppinglist by its name
     * @param name the name of a product which want to be deleted from the shoppinglist
     */
    public void delProductFromListByName(String name){
        JSONObject jObject = new JSONObject();
        try {


            jObject.put("shoppinglistid",sList.getId());
            List<Product> pList = sList.getProducts() ;
            for(Product prod: pList){
                if(prod.getName().equals(name)){
                    jObject.put("productid",prod.getId());
                    pList.remove(prod);
                }
            }
            fillScrollView(sList.getProducts());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"022"+jObject.toString());
    }

    /**
     * returns the ID of a Product by its Name
     * @param name name of a product where the id is needed
     * @return the id of the product
     * @throws JSONException
     */
    private int getIdOfProduct(String name) throws JSONException {
        JSONArray jsonarray = new JSONArray(productsJson);
        for (int i = 0; i < jsonarray.length(); i++) {
            final JSONObject e = jsonarray.getJSONObject(i);
            if(e.getString("name").equals(name)){
                return e.getInt("id");
            }
        }
            JSONObject jObject = new JSONObject();
            jObject.put("name",name);
            new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"025"+jObject.toString());
        return 0;
    }

    /**
     * Fills the Scrollview with all Products of the actual Shoppingview
     * @param products list of all products wich want to be shown in the view of the shoppinglist
     */
    private void fillScrollView(List<Product> products){
        scrollViewChild.removeAllViews();
        for (Product p : products) {

            View view = inflater.inflate(R.layout.listviewelement_productinshoppinglist, null);
            TextView prodName = (TextView) view.findViewById(R.id.ProdName);
            ImageButton delBtn = (ImageButton) view.findViewById(R.id.delBtn) ;
            delBtn.setTag(p);
            prodName.setText(p.getName());

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product p = (Product)v.getTag();
                    delProductFromListByProduct(p);
                }
            });

            scrollViewChild.addView(view);
        }
    }

    /**
     * Fills the Listadapter with Productnames for Autocompletefunktion
     * @param productsJson product in JSON
     * @throws JSONException
     */
    public void initResponseList(String productsJson) throws JSONException {
        JSONArray jsonarray = new JSONArray(productsJson);
        responseList = new ArrayList<String>();
        for (int i = 0; i < jsonarray.length(); i++) {
            final JSONObject e = jsonarray.getJSONObject(i);
            String name = e.getString("name");
            responseList.add(name);
        }

        adapter = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_dropdown_item_1line, responseList);
        pNametextView.setAdapter(adapter);
    }

    /**
     * Updates the Name of the actual Shoppinglist
     * @param name the new name of the shoppinglist
     * @throws JSONException
     */
    private void updateShoppingListName(String name) throws JSONException {
        JSONObject jObject = new JSONObject();
        jObject.put("name",name);
        jObject.put("shoppinglistid",sList.getId());
        new SocketClientAsyncTask(f,f.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"027"+jObject.toString());
    }

    /**
     * Sets the name and Date of the Shoppinglist in the View
     * @param listName name of the shoppinglist
     * @param listDate date when the shoppinglist was created
     */
    public void updateUI(String listName, Date listDate){
        shoppingListName.setHint(listName);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
        Log.d("dateStringslist",""+listDate.toString());
        shoppingListDate.setText(dateFormatter.format(listDate));

    }

    /**
     * Hides the Keyboard
     */
    public void closeKeyboard(){
        InputMethodManager inputManager =   (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getActivity().getCurrentFocus()!=null)
        inputManager.hideSoftInputFromWindow( this.getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Opens a Dialog where the User can Choose wich Products he want in his Shoppinglist by some recomondations
     * @param suggestedProducts list of productnames which is needed to give suggesztions
     */
    private void sugegestedProductsDialog(List<Product> suggestedProducts){
        Log.d("suggestions","start dialog");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.getContext());
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Bitte Produkte wählen:");
        final Dialog myDialog = new Dialog(this.getContext());

        LinearLayout linLayout = (LinearLayout)    inflater.inflate(R.layout.dialog_viewsuggestedproducts, null);
        ScrollView suggestions_ScrollView = (ScrollView) linLayout.findViewById(R.id.productsScrollView);

        LinearLayout sViewChild = new LinearLayout(super.getActivity());
        sViewChild.setOrientation(LinearLayout.VERTICAL);

        suggestions_ScrollView.addView(sViewChild);


        Button okbtn = (Button)linLayout.findViewById(R.id.suggestionsFinish);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    myDialog.dismiss();
            }
        });


        for (Product p: suggestedProducts) {
            View view = inflater.inflate(R.layout.listviewelement_productsuggestioninshoppinglist, null);

            TextView prodName = (TextView) view.findViewById(R.id.ProdName);
            CheckBox cBox = (CheckBox) view.findViewById(R.id.cBox) ;
            cBox.setTag(p);
            prodName.setText(p.getName());


            cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // ((MainActivity) getActivity()).openFragment_AddProducts((Product)v.getTag());
                    Product product = (Product)buttonView.getTag();
                    if (isChecked){
                        try {
                            addProductToShoppingList(product.getId(),product.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (!isChecked) {
                        delProductFromListByName(product.getName());
                    }
                }
            });


            sViewChild.addView(view);
        }

        myDialog.setContentView(linLayout);
        myDialog.show();


    }

    /**
     * overwrtitten Method of the interface Asynctesponse wich gets called after SocketclientAsynctask is finished
     * @param output the from the server received message
     */
    @Override
    public void processFinish(String output) {
        String taskCode = "";
        String json = output.substring(3,output.length());

        Log.d("suggestions","got answer 1 "+json);

        if(output.length()>=3){
            taskCode = output.substring(0,3);
        }

        if(taskCode.equals("023")){

            try {
                productsJson = json;
                initResponseList(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(taskCode.equals("025")){
            try {
                JSONObject jObject = new JSONObject(json);
                String pName = jObject.getString("productname");
                int pId = jObject.getInt("productID");
                addProductToShoppingList(pId,pName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(taskCode.equals("026")){

            ((MainActivity) getActivity()).openFragment_allShoppingLists();
        }

        if(taskCode.equals("028")){
            try {
                JSONObject jObject = new JSONObject(json);
                String dateString = jObject.getString("dateOfCreation");

                Log.d("datestring",dateString);
                Date date = null;
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.GERMANY);
                    date = dateFormatter.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    date = dateFormatter.parse(dateString);
                } catch (ParseException e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.GERMAN);
                    try {
                        date = sdf.parse(dateString);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                sList = new ShoppingList(jObject.getInt("id"),jObject.getString("name"),date);
                updateUI(sList.getName(),sList.getDateOfCreation());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(taskCode.equals("029")){

            try {
                Log.d("suggestions","got answer 2 "+json);
                util.getProductsNotStoredProductsFromJson(json);
                sugegestedProductsDialog(util.getProductsNotStoredProductsFromJson(json));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }
}
