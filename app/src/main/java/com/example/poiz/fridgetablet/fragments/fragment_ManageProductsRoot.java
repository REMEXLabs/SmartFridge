package com.example.poiz.fridgetablet.fragments;

import android.app.FragmentManager;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poiz.fridgetablet.R;

/**
 * Created by poiz on 14.09.2016.
 */

/**
 * Rootview wich holds the Tabview and the Fragments if the Tabs
 */
public class fragment_ManageProductsRoot extends Fragment {
    private FragmentTabHost mTabHost;

    /**
     * onCreate
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manageproducts_tablayout,container, false);


        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("addProductsFragment").setIndicator("Hinzufügen"),
                Fragment_ManageProducts.class, null);
    /*    mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Öffnen"),
                Fragment_OpenProducts.class, null);*/
        mTabHost.addTab(mTabHost.newTabSpec("deleteProductsFragment").setIndicator("Entfernen"),
                Fragment_DeleteProducts.class, null);


        return rootView;
    }

}
