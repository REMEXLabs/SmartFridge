package com.example.poiz.fridgetablet.adapters;

/**
 * Created by poiz on 05.09.2016.
 */


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poiz.fridgetablet.R;
import com.example.poiz.fridgetablet.navigationModel.NavDrawerItem;

/**
 * Adapter Class vor the Navigationdrawer
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    /**
     * Constructior
     * @param context
     * @param navDrawerItems
     */
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    /**
     * returns the quantity of the Navdrawer items
     * @return Integer
     */
    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    /**
     * Returns the NavDrawerItem of a specific position in the Navigationdrawer
     * @param position
     * @return NavDrawerItem
     */
    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    /**
     * Returns itemID
     * @param position
     * @return integer
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *Creates the View wich appears in the NavDrawerList
     * @param position
     * @param convertView
     * @param parent
     * @return returns the NavDrawer view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility()){
            txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

}
