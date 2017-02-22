package com.example.poiz.fridgetablet.activitys;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.adapters.NavDrawerListAdapter;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.Product;
import com.example.poiz.fridgetablet.data.ShoppingList;
import com.example.poiz.fridgetablet.fragments.Fragment_DeleteProducts;
import com.example.poiz.fridgetablet.fragments.Fragment_ViewPhotos;
import com.example.poiz.fridgetablet.fragments.Fragment_ViewProducts;
import com.example.poiz.fridgetablet.fragments.Fragment_ManageProducts;
import com.example.poiz.fridgetablet.fragments.fragment_AllShoppingLists;
import com.example.poiz.fridgetablet.fragments.fragment_EditShoppingList;
import com.example.poiz.fridgetablet.fragments.fragment_ManageProductsRoot;
import com.example.poiz.fridgetablet.fragments.fragment_settings;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.navigationModel.NavDrawerItem;
import com.example.poiz.fridgetablet.voiceRecognision.ListeningActivity;
import com.example.poiz.fridgetablet.voiceRecognision.VoiceRecognitionListener;

import org.json.JSONException;

/**
 * Mainactivity. Starts when the App is started and holds all the Fragments
 */
public class MainActivity extends ListeningActivity implements AsyncResponse {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private static String MY_PREFS_NAME = "GCM";

    ShoppingList sList;

    /**
     *initialisation of the class  variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendGCMToken();


        //voicelistener
        context = getApplicationContext();
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening();


        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
       Log.d("das ","Das ist die länge: "+navMenuIcons.length());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],  navMenuIcons.getResourceId((0+1), -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId((0+2), -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId((0+3), -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
               // R.drawable.drawer_btn, //nav menu toggle icon                                      !!!!!!-----------
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }



    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     *onCreateOptionsMenu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);                               //!!!!!-----
        return true;
    }

    /**
     *onOptionsItemSelected
     * @param item
     * @return the in the drawer selected item/fragment
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(item.getItemId()==R.id.action_settings){
            Fragment fragment = new fragment_settings();

            if (fragment != null) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack( "settings" ).commit();

            } else {
                // error in creating fragment
                Log.e("MainActivity", "Error in creating fragment");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
       // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * opens the Fragment Fragment_ManageProducts in main Activity
     * @param p object of a Product
     */
    public void openFragment_AddProducts(Product p){
        Bundle bundle = new Bundle();

        bundle.putInt("id",p.getId());
        bundle.putString("name", p.getName());
        bundle.putInt("adddelopen",3);
        bundle.putInt("catID",p.getCatID());
        bundle.putString("storeDate", p.getStoreDate().toString());
        bundle.putString("expDate", p.getExpDate().toString());
        bundle.putInt("shelflife", p.getShelflife());
        bundle.putInt("storeid", p.getStoreid());

        Fragment fragment = new Fragment_ManageProducts();
        fragment.setArguments(bundle);

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment,"editStoredProductsFragment").addToBackStack( "editStoredProductsFragment" ).commit();

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    /**
     * Opens Fragment_ViewProducts in mainActivity
     */
    public void openFragment_ViewProducts(){


        Fragment fragment = new Fragment_ViewProducts();

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack( "viewProductsFragment" ).commit();

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    /**
     * Opens fragment_AllShoppingLists in mainActivity
     */
    public void openFragment_allShoppingLists(){
        Fragment fragment = new fragment_AllShoppingLists();

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack( "viewAllShoppingLists" ).commit();

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    /**
     * Method wich is Called from Fragments to retrive the Shoppinglist from the mainactivity
     * @return ShoppingList Object contains a shoppinglist wich was received from the server earlier
     */
    public ShoppingList getShoppingList(){
        return sList;
    }
    public void openFragment_EditShoppingList(ShoppingList shoppingList){
        sList =  shoppingList;

        Bundle b = new Bundle();
        Fragment fragment = new fragment_EditShoppingList();

        if (fragment != null) {
            if(sList!=null) {
                b.putBoolean("new", false);//es wird eine bestehende shoüpingliste geöffnet
            }else{
                b.putBoolean("new", true);
            }
            fragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack( "editShoppingLists" ).commit();

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }


    }






    /**
     * Diplaying fragment view for the selected nav drawer list item
     * @param position
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        String fragmentTag = "";
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                fragment = new fragment_ManageProductsRoot();
                fragmentTag = "manageProductsFragmentRoots";

                break;

            case 1:
                fragment = new Fragment_ViewProducts();
                fragmentTag = "viewProductsFragment";
                break;
            case 2:
                fragment = new fragment_AllShoppingLists();
                fragmentTag = "viewAllShoppingLists";
                break;
            case 3:
                fragment = new Fragment_ViewPhotos();
                fragmentTag = "viewPhotosFragment";
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, fragmentTag).addToBackStack( fragmentTag ).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    /**
     * Sets the titel of the actionbar
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    /**
     *needed for the ActionBarDrawerToggle to sync the current state
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * needed for the ActionBarDrawerToggle to set changes
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Sends the createt GCMToken to the server. GCMToken is needed to send GoogleMsg from the server to
     * the specific client
     */
    public void sendGCMToken(){
        Log.d("hallo", "Token gesendet!");
        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        String gcmToken = prefs.getString("GCMTOKEN", null);
        Log.d(MY_PREFS_NAME, "Refreshed token in main ist: " + gcmToken);
        if(gcmToken != null && gcmToken.length()>10){
            new SocketClientAsyncTask(this,this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"012"+gcmToken);
        }
    }

    /**
     * overwritten method of the imlemented interface Asyncresponse
     * @param output
     */
    @Override
    public void processFinish(String output) {

    }


    /////////--------Sprachverabeitung-------//////////////

    /**
     * overwritten method of motherclass ListeningActivity. Gets called to process voicecomands.
     * The Metod calls ChcekVoiceCommand whith the VoiceCommand if one of the Buzzwords was detected
     * @param voiceCommands
     */
    @Override
    public void processVoiceCommands(String... voiceCommands) {

        String addCommand = "hinzufügen"; //action 0
        String categorieCommand = "kategorie"; //action 1
        String dateCommand = "datum"; //action 2
        String delCommand = "löschen"; //action 3



        for (String command : voiceCommands) {

            if(ChcekVoiceCommand(command,addCommand,0)==true)
                break;
            if(ChcekVoiceCommand(command,categorieCommand,1)==true)
                break;
            if(ChcekVoiceCommand(command,dateCommand,2)==true)
                break;
            if(ChcekVoiceCommand(command,delCommand,3)==true)
                break;
        }
        restartListeningService();
    }

    /**
     * Gets called if on of the Buzzwords (hinzufügen,kategorie,datum,löschen) was detected. The method selects the
     * fragment where the Voicecommand is needed
     * @param command
     * @param commandWord
     * @param action
     * @return returns true if a voicecommand was successfully recognized
     */
    private boolean ChcekVoiceCommand(String command,String commandWord, int action) {

        if (command.contains(commandWord)) {

            if (command.length() > command.indexOf(commandWord) + commandWord.length()) {
                String voiceMsg = command.substring(command.indexOf(commandWord) + commandWord.length() + 1);
                Log.d("spracheingabe","die nachricht: "+voiceMsg);
                fragment_ManageProductsRoot rootFragment = null;
                Fragment_ManageProducts fragmentE = null;

                try {
                    rootFragment = (fragment_ManageProductsRoot) getSupportFragmentManager().findFragmentByTag("manageProductsFragmentRoots");
                }catch(NullPointerException e){

                }
                try {
                    fragmentE = (Fragment_ManageProducts) getSupportFragmentManager().findFragmentByTag("editStoredProductsFragment");
                    Log.d("edit","erfolgreich in main 1");
                }catch(NullPointerException e){
                    Log.d("edit","error");
                }

                //Produkt bearbeiten
                if((action == 1 || action == 2) && sendVoiceCommandToManageProductsFragment(fragmentE,voiceMsg,action)){
                    return true;
                }


                if (rootFragment != null && rootFragment.isVisible()) {

                    Fragment_ManageProducts fragmentM = (Fragment_ManageProducts) rootFragment.getChildFragmentManager().findFragmentByTag("addProductsFragment");
                    Fragment_DeleteProducts fragmentD = (Fragment_DeleteProducts) rootFragment.getChildFragmentManager().findFragmentByTag("deleteProductsFragment");

                    //produkt löschen
                    if(action == 3 && sendVoiceCommandToDeleteProductsFragment(fragmentD,voiceMsg,action)){
                        return true;
                    }

                    //produkt hinzufügen
                    if((action != 3)&& sendVoiceCommandToManageProductsFragment(fragmentM,voiceMsg,action)){
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Sends the voicecommand to a specific fragment
     * @param fragment
     * @param voiceMsg
     * @param action
     * @return true if succesfull, false if not
     */
    public boolean sendVoiceCommandToManageProductsFragment(Fragment_ManageProducts fragment, String voiceMsg, int action){
        if (fragment != null && fragment.isVisible()) {

            if (action == 0)
                fragment.addProductByVoice(voiceMsg);

            if (action == 1) {
                try {
                    Log.d("edit","erfolgreich in main 3");
                    fragment.setCategorieByVoice(voiceMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (action == 2)
                fragment.setDateByVoice(voiceMsg);

            return true;
        }
        return false;
    }

    /**
     * sends the voicecommand to Fragment_DeleteProducts
     * @param fragment
     * @param voiceMsg
     * @param action
     * @return true if succesfull, false if not
     */
    private boolean sendVoiceCommandToDeleteProductsFragment(Fragment_DeleteProducts fragment, String voiceMsg, int action) {
        if (fragment != null && fragment.isVisible()) {
            if (action == 3){
                fragment.deleteProductByVoice(voiceMsg);
                return true;
            }
        }
        return false;
    }

}