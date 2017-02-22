package com.example.poiz.fridgetablet.navigationModel;

/**
 * Created by poiz on 05.09.2016.
 */

/**
 * holds a Item of the Navigationdrawer
 */
public class NavDrawerItem {

    private String title;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;

    /**
     * constructor
     */
    public NavDrawerItem(){}

    /**
     * constructor
     * @param title titel of the drawer
     * @param icon icon of the drawer
     */
    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    /**
     * constructor
     * @param title titel of the drawer
     * @param icon icon of the drawer
     * @param isCounterVisible state of visability
     * @param count the count
     */
    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    /**
     * returns the Titel
     * @return  titel of the drawer
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * returns the Icon
     * @return icon of the drawer
     */
    public int getIcon(){
        return this.icon;
    }

    /**
     * returns the count
     * @return the count
     */
    public String getCount(){
        return this.count;
    }

    /**
     * returns the visability of the counter
     * @return state of visability
     */
    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

    /**
     * sets the titel
     * @param title
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * sets the icon
     * @param icon the Icon of the drawer
     */
    public void setIcon(int icon){
        this.icon = icon;
    }

    /**
     * sets the count
     * @param count the count
     */
    public void setCount(String count){
        this.count = count;
    }

    /**
     * sets the visibility of the counter
     * @param isCounterVisible state of visability
     */
    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }
}
