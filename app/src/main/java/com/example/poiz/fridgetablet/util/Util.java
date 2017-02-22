package com.example.poiz.fridgetablet.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import com.example.poiz.fridgetablet.data.Picture;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.data.ShoppingList;
import com.example.poiz.fridgetablet.dataBase.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Util holds Methods wich are needed in many different Classes
 */
public class Util {

    DatabaseHandler dbHandler;
    public Util (DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;
    }
    public Util (){}

    /**
     * adds a given amount of days to a given date to get a new date
     * @param date date where dayst want to be added
     * @param days amount of days which want to be added to a date
     * @return new date
     */




    public Date addDaysToDate(Date date, int days)
    {
        if(date != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            return cal.getTime();
        }
        else return null;

    }

    /**
     * returns the date of the day the method is called
     * @return date of the day the method is called
     */
    public Date getTodaysDate(){

        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
        Date todayDate = today.getTime();

        return todayDate;
    }

    /**
     * calculates the amount of days between to dates
     * @param date1 first date
     * @param date2 second date
     * @return days between the first and the second given date
     */
    public int getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return (int) TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    /**
     * converts a json object of a product into a java object
     * @param productJson json object of a product
     * @return java object of a product
     */
    public Product getProductFromJson(String productJson){
        Product prod = null;
        int productID = 0;
        String  productName = "";
        int productCategorie = 0;
        int shelflife = 0;
        int storeid = 0;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(productJson);

            productName = jsonObject.getString("productname");
            productCategorie = jsonObject.getInt("categorie");
            shelflife = jsonObject.getInt("shelflife");
            productID = jsonObject.getInt("productID");
            storeid = jsonObject.getInt("storeID");
            prod = new Product(productID, productName, productCategorie, shelflife);
            prod.setStoreid(storeid);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prod;

    }

    /**
     * onverts a list of json objects of products into a javaList of products
     * @param productsJson
     * @return JavaList of product objects
     * @throws JSONException
     * @throws ParseException
     */
    public List<Product> getProductsFromJson(String productsJson) throws JSONException, ParseException {

        dbHandler.deleteAllProducts();

        List<Product> prods = new ArrayList<>();
        int productID = 0;
        String storeDateStr = null;
        String expDateStr = null;
        String  productName = "";
        int productCategorie = 0;
        int storeid = 0;
        int shelflife = 0;
        Log.d("util","util aufgerufen");
        JSONArray jsonarray = new JSONArray(productsJson);
        for (int i = 0; i < jsonarray.length(); i++) {

            JSONObject jsonobject = jsonarray.getJSONObject(i);
            productName = jsonobject.getString("productname");
            Log.d("util aufgerufen",productName);
            productCategorie = jsonobject.getInt("categorie");
            Log.d("util aufgerufen",""+productCategorie);
            shelflife = jsonobject.getInt("shelflife");
            Log.d("util aufgerufen",""+shelflife);
            productID = jsonobject.getInt("productID");
            Log.d("util aufgerufen",""+productID);
            storeDateStr = jsonobject.getString("storeDate");
            Log.d("util aufgerufen",storeDateStr);
            expDateStr = jsonobject.getString("expireDate");
            //expDateStr = jsonobject.getString("storeDate");
            storeid = jsonobject.getInt("storeID");
            Log.d("util aufgerufen",""+storeid);

            Product prod;
          /*  try{
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.GERMAN);
                Log.d("util aufgerufen","util aufgerufen"+productID+" "+ productName+" "+ productCategorie+" "+ shelflife+" "+ sdf.parse(storeDateStr)+" "+ sdf.parse(expDateStr)+" "+ storeid);
                prod = new Product(productID, productName, productCategorie, shelflife, sdf.parse(storeDateStr), sdf.parse(expDateStr), storeid);
            }catch (ParseException p){
                Log.d("utilaufgerufen","fail0");
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    Log.d("util aufgerufen","util aufgerufen"+productID+" "+ productName+" "+ productCategorie+" "+ shelflife+" "+ sdf.parse(storeDateStr)+" "+ sdf.parse(expDateStr)+" "+ storeid);
                    prod = new Product(productID, productName, productCategorie, shelflife, sdf.parse(storeDateStr), sdf.parse(expDateStr), storeid);

                }catch (ParseException k){
                    Log.d("utilaufgerufen","fail1");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.GERMAN);
                    Log.d("util aufgerufen","util aufgerufen"+productID+" "+ productName+" "+ productCategorie+" "+ shelflife+" "+ sdf.parse(storeDateStr)+" "+ sdf.parse(expDateStr)+" "+ storeid);
                    prod = new Product(productID, productName, productCategorie, shelflife, sdf.parse(storeDateStr), sdf.parse(expDateStr), storeid);
                }



            }*/
            prod = new Product(productID, productName, productCategorie, shelflife, parseDates(storeDateStr), parseDates(expDateStr), storeid);
            prods.add(prod);
            Log.d("prefDate","last: "+prod.getExpDate().toString());

            dbHandler.insertProduct(prod);
        }

        return prods;
    }

    /**
     * converts a jason List of pictures to a JavaList ist Pictures
     * @param pictureJson
     * @return
     * @throws JSONException
     */
    public List<Picture>getPicturesFromJson(String pictureJson) throws JSONException {
        Log.d("loginf", "get pics from json!");
        List<Picture> pics = new ArrayList<>();
        String pictureName = "";
        byte[] imageBytes = null;
        long timestamp = 0;
        String byteArrayString = "";
        JSONArray jsonarray = new JSONArray(pictureJson);
        longInfo(byteArrayString.toString());
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            pictureName = jsonobject.getString("name");
            //byteArrayString = jsonobject.getString("imageB64");
            byteArrayString = jsonobject.getString("imageBytes");
            imageBytes = Base64.decode(byteArrayString,Base64.DEFAULT);
            timestamp = jsonobject.getLong("timestamp");
            pics.add(new Picture(pictureName,imageBytes,timestamp));
        }

        return pics;
    }

    public static void longInfo(String str) {
        if (str.length() > 1000) {
            Log.d("loginf", str.substring(0, 1000));
            longInfo(str.substring(1000));
        } else
            Log.d("loginf", str);
    }

    public String getDateFromTimestamp(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("EEE MMM dd HH:mm:ss z yyyy", cal).toString();
        return date;
    }

    /**
     * converts a jason list of products which are not stored in the fridge to a JavaList of Products
     * @param productsJson
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    public List<Product> getProductsNotStoredProductsFromJson(String productsJson) throws JSONException, ParseException {
        Log.d("suggestions","got answer 3 "+productsJson);

        List<Product> prods = new ArrayList<>();
        int productID = 0;
        String storeDateStr = null;
        String expDateStr = null;
        String  productName = "";
        int productCategorie = 0;
        int storeid = 0;
        int shelflife = 0;
        JSONArray jsonarray = new JSONArray(productsJson);
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            productName = jsonobject.getString("productname");
            Log.d("suggestions","got answer 4 "+productName);
            productCategorie = jsonobject.getInt("categorie");
            Log.d("suggestions","got answer 5 "+productCategorie);
            shelflife = jsonobject.getInt("shelflife");
            Log.d("suggestions","got answer 6 "+shelflife);
            productID = jsonobject.getInt("productID");
            Log.d("suggestions","got answer 7 "+productID);
            prods.add(new Product(productID, productName, productCategorie, shelflife));

        }
        return prods;
    }
    public List<ShoppingList> getShoppingListsFromJson(String shoppingListsJson) throws JSONException, ParseException {
        List<ShoppingList> sLists = new ArrayList<>();
        String shoppingListName;
        int shoppingListId;
        String shoppingListDateStr;
        Date shoppingListDate = null;
        dbHandler.deleteAllShoppingLists();

        JSONArray jsonarray = new JSONArray(shoppingListsJson);
        for (int i = 0; i < jsonarray.length(); i++) {
            ShoppingList sList;
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            shoppingListName = jsonobject.getString("name");
            Log.d("httpget","response: in util name "+shoppingListName);
            shoppingListId = jsonobject.getInt("id");
            shoppingListDateStr = jsonobject.getString("dateOfCreation");
            Log.d("httpget","response: in util date "+shoppingListDateStr);
            /**  try{
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.GERMAN);
                shoppingListDate=sdf.parse(shoppingListDateStr);
            }catch (ParseException p){
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    shoppingListDate=sdf.parse(shoppingListDateStr);
                }catch (ParseException k){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.GERMAN);
                    shoppingListDate=sdf.parse(shoppingListDateStr);
                }
            }*/
            shoppingListDate = parseDates(shoppingListDateStr);
            Log.d("httpget","response: in util date pared "+shoppingListDate.toString());
            JSONArray productsJsonArray = jsonobject.getJSONArray("products");
            sList = new ShoppingList(shoppingListId,shoppingListName,shoppingListDate);
            sList.setProducts( getShoppinListProductsFromJson(productsJsonArray.toString()));
            dbHandler.insertShoppingList(sList);
            sLists.add(sList);
        }

        return sLists;
    }

    /**
     * json of shoppinglist to java objects
     * @param productsJson
     * @return JavaList of ShoppingList objects
     * @throws JSONException
     * @throws ParseException
     */
    public List<Product> getShoppinListProductsFromJson(String productsJson) throws JSONException, ParseException {
        List<Product> prods = new ArrayList<>();
        int productID = 0;
        String  productName = "";
        int productCategorie = 0;
        int shelflife = 0;
        JSONArray jsonarray = new JSONArray(productsJson);
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            productName = jsonobject.getString("productname");
            productCategorie = jsonobject.getInt("categorie");
            shelflife = jsonobject.getInt("shelflife");
            productID = jsonobject.getInt("productID");
            prods.add(new Product(productID, productName, productCategorie, shelflife));

        }
        return prods;
    }

    public Date parseDates(String datestr){
        SimpleDateFormat sdf;
        Date date = null;



        String[] dateFormates = new String[5];
        dateFormates[0] = "EEE MMM dd HH:mm:ss z yyyy";
        dateFormates[1] = "yyyy-MM-dd";
        dateFormates[2] = "MMM dd, yyyy";
        dateFormates[3] = "EEE MMM dd HH:mm:ss";
        dateFormates[4] = "yyyy-MM-dd";

        for(int i=0;i<dateFormates.length;i++){
            try {
                sdf = new SimpleDateFormat(dateFormates[i], Locale.US);
                date=sdf.parse(datestr);
                Log.d("prefDate","first:"+ datestr);
                return date;

            } catch (ParseException e) {
                try {
                    sdf = new SimpleDateFormat(dateFormates[i], Locale.GERMAN);
                    date=sdf.parse(datestr);
                    Log.d("prefDate","first:"+ datestr);
                    return date;
                } catch (ParseException y) {
                    y.printStackTrace();
                }
                e.printStackTrace();
            }

        }
        return null;
    }

    public String strToUtf8(String str){
        byte[] byteArray = new byte[0];
        String utfText = "";
        try {
            byteArray = str.getBytes("UTF-8");
            utfText = new String( byteArray, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return utfText;
    }


}