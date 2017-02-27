package com.example.poiz.fridgetablet.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.example.poiz.fridgetablet.data.Picture;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.data.ProductStatus;
import com.example.poiz.fridgetablet.data.ShoppingList;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poiz on 29.01.2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 9;

    // Database Name
    private static final String DATABASE_NAME = "fridgeDB";

    // Products table name
    private static final String TABLE_Products = "Products";

    //Products table fields
    private static final String Product_StoreID = "storeID";
    private static final String Product_Name = "productname";
    private static final String Product_Categorie = "categorie";
    private static final String Product_Shelflife = "shelflife";
    private static final String Product_ExpireDate = "expireDate";
    private static final String Product_StoreDate = "storeDate";
    private static final String Product_ProductID = "productID";


    // Products table name
    private static final String TABLE_Pictures = "prictures";

    //Products table fields
    private static final String Picture_ID = "id";
    private static final String Picture_Name = "name";
    private static final String Picture_Photo = "photo";
    private static final String Picture_Timestamp = "timestamp";


    // Products table name
    private static final String TABLE_ShoppingList = "shoppinglist";

    //Products table fields
    private static final String ShoppingList_ID = "id";
    private static final String ShoppingList_Name = "name";
    private static final String ShoppingList_Date = "date";

    // Products table name
    private static final String TABLE_ProductsForShoppingList = "productsInShoppingLists";

    //Products table fields
    private static final String ProductsForShoppingList_ID = "id";
    private static final String ProductsForShoppingList_Name = "name";


    // Products table name
    private static final String TABLE_ShoppingListProduct = "shoppinglistproduct";

    //Products table fields
    private static final String ShoppingListProduct_ID = "id";
    private static final String ShoppingListProduct_PID = "productid";
    private static final String ShoppingListProduct_SID = "shoppinglistid";




    Util util = new Util(this);

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_Products + "("
                + Product_StoreID + " INTEGER PRIMARY KEY,"
                + Product_Name + " TEXT,"
                + Product_Categorie + " INTEGER,"
                + Product_Shelflife + " INTEGER,"
                + Product_ExpireDate + " DATETIME,"
                + Product_StoreDate + " DATETIME,"
                + Product_ProductID + " INTEGER" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_PICTURES_TABLE = "CREATE TABLE " + TABLE_Pictures + "("
                + Picture_ID + " INTEGER PRIMARY KEY,"
                + Picture_Name + " TEXT,"
                + Picture_Photo + " BLOB,"
                + Picture_Timestamp + " long" + ")";
        sqLiteDatabase.execSQL(CREATE_PICTURES_TABLE);

        String CREATE_SHOPPINGLIST_TABLE = "CREATE TABLE " + TABLE_ShoppingList + "("
                + ShoppingList_ID + " INTEGER PRIMARY KEY,"
                + ShoppingList_Name + " TEXT,"
                + ShoppingList_Date + " DATETIME" + ")";
        sqLiteDatabase.execSQL(CREATE_SHOPPINGLIST_TABLE);

        String CREATE_PRODUCTFORSHOPPINGLIST_TABLE = "CREATE TABLE " + TABLE_ProductsForShoppingList + "("
                + ProductsForShoppingList_ID + " INTEGER PRIMARY KEY,"
                + ProductsForShoppingList_Name + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_PRODUCTFORSHOPPINGLIST_TABLE);

        String CREATE_SHOPPINGLISTPRODUCT_TABLE = "CREATE TABLE " + TABLE_ShoppingListProduct + "("
                + ShoppingListProduct_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ShoppingListProduct_PID + " INTEGER,"
                + ShoppingListProduct_SID + " INTEGER" + ")";
        sqLiteDatabase.execSQL(CREATE_SHOPPINGLISTPRODUCT_TABLE);
    }

    public void inserPicture(Picture pic){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues ();
        values.put(Picture_Name, pic.getName());
        values.put(Picture_Photo, pic.getImageBytes());

        values.put(Picture_Timestamp, pic.getTimestamp());

        // Inserting Row
        db.insert(TABLE_Pictures, null, values);
        db.close(); // Closing database connection
    }
    public void deletePhoto(String picName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_Pictures +" where "+Picture_Name+" = '"+picName+"'");
        db.close();
    }

    public List<Picture> getPictures(){
        List<Picture> picList = new ArrayList<Picture>();
        // Select All Query
        String selectQuery = "";
        selectQuery = "SELECT  * FROM " + TABLE_Pictures;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Picture pic = new Picture();
                pic.setName(cursor.getString(1));
                pic.setImageBytes(cursor.getBlob(2));
                pic.setTimestamp(cursor.getLong(3));
                picList.add(pic);
            } while (cursor.moveToNext());
        }else{
            picList = null;
        }

        // return contact list
        return picList;
    }

    public String getPicTimestampsJson() throws JSONException {
        List<Picture> picList = getPictures();
        JSONArray all = new JSONArray();
        if(picList!=null)
        for(int i = 0; i < 2; i++){
            JSONObject single = new JSONObject();
            single.put("name", picList.get(i).getName());
            single.put("timestamp",  picList.get(i).getTimestamp());
            all.put(single);
        }

        return all.toString();
    }


    public void insertProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues ();
        values.put(Product_StoreID, product.getStoreid());
        values.put(Product_Name, product.getName());
        values.put(Product_Categorie, product.getCatID());
        values.put(Product_Shelflife, product.getShelflife());

        Log.d("newdate",product.getExpDate().toString());

        values.put(Product_ExpireDate, (new SimpleDateFormat("yyyy-MM-DD").format( product.getExpDate())));
        values.put(Product_StoreDate,(new SimpleDateFormat("yyyy-MM-DD").format( product.getStoreDate())));



        values.put(Product_ProductID, product.getId());

        // Inserting Row
        db.insert(TABLE_Products, null, values);
        db.close(); // Closing database connection
    }

    public void deleteAllProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_Products);
        db.close();
    }

    public List<Product> getAllProductsByCategorie(int cat) throws ParseException {
        List<Product> prodList = new ArrayList<Product>();
        // Select All Query
        String selectQuery = "";
        if(cat == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_Products;
        }else{
            selectQuery = "SELECT  * FROM " + TABLE_Products+" where categorie = "+cat;
        }

        Log.d("selProducts","got it");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product prod = new Product();
                prod.setStoreid(Integer.parseInt(cursor.getString(0)));
                prod.setName(cursor.getString(1));
                prod.setCatID(Integer.parseInt(cursor.getString(2)));
                prod.setShelflife(Integer.parseInt(cursor.getString(3)));
                prod.setExpDate(util.parseDates(cursor.getString(4)));
                prod.setStoreDate(util.parseDates(cursor.getString(5)));
                prod.setId(Integer.parseInt(cursor.getString(6)));
                prodList.add(prod);

            } while (cursor.moveToNext());
        }

        // return contact list
        return prodList;
    }

    public ProductStatus getCountAndStoreID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.rawQuery("SELECT MAX("+Product_StoreID+"),COUNT(*) FROM "+TABLE_Products, null);
        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.getString(0) != null && cursor.getString(1)!= null) {

            ProductStatus productStatus = new ProductStatus(Integer.parseInt(cursor.getString(0)),
                    Integer.parseInt(cursor.getString(1)));
            return productStatus;
        }
        return new ProductStatus(0,0);
    }

    //[{"id":22,"name":"Einkaufen Aldi","dateOfCreation":"2016-09-23","productCount":0,"products":[{"barcode":0,"productname":"Spinat","categorie":"0","shelflife":0,"stored":null,"expireDate":null,"productID":65,"storeDate":null,"storeID":0},{"barcode":0,"productname":"Wasser","categorie":"4","shelflife":0,"stored":null,"expireDate":null,"productID":38,"storeDate":null,"storeID":0}]}

    public void insertShoppingList(ShoppingList slist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values2 = new ContentValues ();
        //inser shoppinglist
        ContentValues values = new ContentValues ();
        values.put(ShoppingList_ID,  slist.getId());
        values.put(ShoppingList_Name, slist.getName());
        values.put(ShoppingList_Date, slist.getDateOfCreation().toString());

        // Inserting Row
        db.insert(TABLE_ShoppingList, null, values);

        //insert products for shoppinglist
        for(int i = 0; i<slist.getProducts().size(); i++){

            Product p = slist.getProducts().get(i);
            values2 = new ContentValues ();
            values = new ContentValues ();
            values.put(ProductsForShoppingList_ID,  p.getId());
            values.put(ProductsForShoppingList_Name, p.getName());
            // Inserting Row
            db.insert(TABLE_ProductsForShoppingList, null, values);


            values2.put(ShoppingListProduct_PID, p.getId());
            values2.put(ShoppingListProduct_SID,slist.getId());
            db.insert(TABLE_ShoppingListProduct, null, values2);

        }
        //insert connection between shoppinglist and Product


        db.close(); // Closing database connection
    }

    public void deleteAllShoppingLists(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_ShoppingList);
        db.execSQL("delete from "+ TABLE_ProductsForShoppingList);
        db.execSQL("delete from "+ TABLE_ShoppingListProduct);
        db.close();
    }

    public List<ShoppingList> getAllShoppingLists(){


        List<ShoppingList> sLists = new ArrayList<ShoppingList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ShoppingList;



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {



                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                Date date = util.parseDates(cursor.getString(2));
                ShoppingList sList = new ShoppingList(id,name,date);
                sList.setProducts(getAllProductsOfShoppingList(id));
                sLists.add(sList);

            } while (cursor.moveToNext());
        }

        return sLists;
    }
    public List<Product> getAllProductsOfShoppingList(int shoppingListID){
        List<Product> pList = new ArrayList<Product>();
        String selectQuery = "SELECT DISTINCT p.* FROM "+TABLE_ProductsForShoppingList+" p JOIN "+TABLE_ShoppingListProduct+" sp ON p.id = sp."+ShoppingListProduct_PID+" where sp."+ShoppingListProduct_SID+" = "+shoppingListID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                pList.add(new Product(id,name));

            } while (cursor.moveToNext());
        }
        return pList;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Products);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Pictures);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ShoppingList);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ProductsForShoppingList);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ShoppingListProduct);


        // Create tables again
        onCreate(db);
    }
}
