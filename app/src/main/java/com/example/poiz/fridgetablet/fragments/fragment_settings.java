package com.example.poiz.fridgetablet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.poiz.fridgetablet.R;

/**
 * Created by poiz on 29.09.2016.
 */


/**
 * Fragment hold by the MainActivity
 * Settings fragment to Activate or deactivate the Voicecontrole and set the IP of the server
 */
public class fragment_settings  extends Fragment {
    LayoutInflater inflater;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    EditText ip1Txt;
    EditText ip2Txt;
    EditText ip3Txt;
    EditText ip4Txt;
    Switch voiceOnOff;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater=inflater;




        FragmentActivity faActivity  = (FragmentActivity)    super.getActivity();
        LinearLayout rootLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment_settings, container, false);

        ip1Txt = (EditText) rootLayout.findViewById(R.id.ip1);
        ip2Txt = (EditText) rootLayout.findViewById(R.id.ip2);
        ip3Txt = (EditText) rootLayout.findViewById(R.id.ip3);
        ip4Txt = (EditText) rootLayout.findViewById(R.id.ip4);
        voiceOnOff = (Switch) rootLayout.findViewById(R.id.voiceOnOff);



        Button setBtn = (Button) rootLayout.findViewById(R.id.button);


        sharedPref = getActivity().getSharedPreferences("IP", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Log.d("voiceswitch"," "+sharedPref.getBoolean("voiceRec",false));
        if(sharedPref.getBoolean("voiceRec",false)){
            voiceOnOff.setText("ON");
            voiceOnOff.setChecked(true);
        }else{
            voiceOnOff.setText("OFF");
            voiceOnOff.setChecked(false);
        }


        voiceOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVoiceRecState(isChecked);
            }
        });


        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIpTOSharedPrefernces();
            }
        });

        ip1Txt.setHint(""+sharedPref.getInt("ip1",0));
        ip2Txt.setHint(""+sharedPref.getInt("ip2",0));
        ip3Txt.setHint(""+sharedPref.getInt("ip3",0));
        ip4Txt.setHint(""+sharedPref.getInt("ip4",0));


        return rootLayout;
    }

    /**
     * writs the State of the Voicecontorle to the SharedPreferences
     * @param isChecked status of the checkbox
     */
    public void setVoiceRecState(boolean isChecked){
        if(isChecked){
            editor.putBoolean("voiceRec",true);
            voiceOnOff.setText("ON");
        }else{
            editor.putBoolean("voiceRec",false);
            voiceOnOff.setText("OFF");
        }
        editor.commit();

    }

    /**
     * Writes the Entered Ip to the Shared Preferences
     */
    public void setIpTOSharedPrefernces(){
        editor.putInt("ip1", Integer.parseInt(ip1Txt.getText().toString()));
        editor.putInt("ip2", Integer.parseInt(ip2Txt.getText().toString()));
        editor.putInt("ip3", Integer.parseInt(ip3Txt.getText().toString()));
        editor.putInt("ip4", Integer.parseInt(ip4Txt.getText().toString()));
        editor.commit();
    }
}
