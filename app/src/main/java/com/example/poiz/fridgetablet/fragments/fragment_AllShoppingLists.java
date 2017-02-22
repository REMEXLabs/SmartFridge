package com.example.poiz.fridgetablet.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.activitys.MainActivity;
import com.example.poiz.fridgetablet.connection.SocketClientAsyncTask;
import com.example.poiz.fridgetablet.data.ShoppingList;
import com.example.poiz.fridgetablet.dataBase.DatabaseHandler;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;
import com.example.poiz.fridgetablet.util.Util;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by poiz on 21.09.2016.
 */

/**
 * Fragment hold by Mainactivity
 * Shows the Shoppinglists wich are Stored on the Server
 */
public class fragment_AllShoppingLists extends Fragment implements AsyncResponse {

    LinearLayout        rootLayout;
    ScrollView shoppingList_ScrollView;
    LinearLayout scrollViewChild;
    List<ShoppingList> sList;
    Util util;
    Button newShoppingListBtn;
    DatabaseHandler dbhandler;

    LayoutInflater inflater;

    private SimpleDateFormat dateFormatter;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the created view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentActivity faActivity = (FragmentActivity) super.getActivity();
        rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_shoppinglists, container, false);

        shoppingList_ScrollView = (ScrollView) rootLayout.findViewById(R.id.scrollView);

        newShoppingListBtn = (Button)rootLayout.findViewById(R.id.new_shoppinglist);

        scrollViewChild = new LinearLayout(super.getActivity());
        scrollViewChild.setOrientation(LinearLayout.VERTICAL);

        shoppingList_ScrollView.addView(scrollViewChild);

        util = new Util(new DatabaseHandler(this.getContext()));

        this.inflater = inflater;

        newShoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openFragment_EditShoppingList(null);
            }
        });


        new SocketClientAsyncTask(this,this.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"021");
        dbhandler = new DatabaseHandler(this.getContext());
        fillScrollView(dbhandler.getAllShoppingLists());

        return rootLayout;
    }

    /**
     * Fills the Scrollview with Childviews wich hold the Shoppinglists
     * @param shopinglist object of a shoppinglist wich want to be shown in the scrollview
     */
    private void fillScrollView(List<ShoppingList> shopinglist){

        scrollViewChild.removeAllViews();
        for (ShoppingList s : shopinglist) {
            dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
            View view = inflater.inflate(R.layout.listviewelement_shoppinglists, null);
            view.setTag(s);
            TextView shoppingListName = (TextView) view.findViewById(R.id.ShppingListName);
            TextView shoppingListDate = (TextView) view.findViewById(R.id.shoppingListDate);
            TextView productCount = (TextView) view.findViewById(R.id.productCount);

            shoppingListName.setText(s.getName());
            shoppingListDate.setText(dateFormatter.format(s.getDateOfCreation()));
            productCount.setText(""+s.getProductCount());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).openFragment_EditShoppingList((ShoppingList)v.getTag());
                }
            });

            scrollViewChild.addView(view);
        }
    }

    /**
     * Overwritten Method from AsyncResponse
     * gets calld whe SocketClientAsynctask was finished to get the Result from the server
     * @param output the from the server received message
     */
    @Override
    public void processFinish(String output) {

        String taskCode = "";
        String json = output.substring(3,output.length());
        Log.d("einkaufslisten",json);
        Log.d("httpget","response: in shop lst");
        Log.d("httpget","response: in shop lst"+json.toString());

        if(output.length()>=3){
            taskCode = output.substring(0,3);
        }
        if(taskCode.equals("021")){
            try {
                sList = util.getShoppingListsFromJson(json);
                fillScrollView(sList);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
