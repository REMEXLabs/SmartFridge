package com.example.poiz.fridgetablet.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poiz on 29.01.2017.
 */
public class ProductStatus {
    int count;
    int lastID;

   public ProductStatus( int lastID, int count){
        this.count = count;
        this.lastID = lastID;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLastID() {
        return lastID;
    }

    public void setLastID(int lastID) {
        this.lastID = lastID;
    }


    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("count", getCount());
            jsonObject.put("lastID", getLastID());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }


}
