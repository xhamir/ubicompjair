package com.example.recordatoriointeligente;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_layout);

        TextView textViewChange = (TextView) findViewById(R.id.ConfTab_title);
        textViewChange.setText(textViewChange.getText() + " " + getBTName());
        textViewChange = (TextView) findViewById(R.id.ConfTab_mac);
        textViewChange.setText(textViewChange.getText() + " " + getBTAddres());
        
        String alertPref = loadPref();
        System.out.println("Got " + alertPref);
        if( alertPref.equals("VOICE")){
        	RadioButton rb = (RadioButton) findViewById(R.id.radio_voice);
        	rb.setChecked(true);
        }else if( alertPref.equals("TONE")){
        	RadioButton rb = (RadioButton) findViewById(R.id.radio_tone);
        	rb.setChecked(true);
        }else if( alertPref.equals("SONG")){
        	RadioButton rb = (RadioButton) findViewById(R.id.radio_song);
        	rb.setChecked(true);
        }else if( alertPref.equals("VIBRATION")){
        	RadioButton rb = (RadioButton) findViewById(R.id.radio_vibr);
        	rb.setChecked(true);
        }
    }
    
    public void onRadioButtonClick(View v){
    	RadioButton button = (RadioButton) v;
    	int buttonID = button.getId();
    	String chos = "";
    	switch(buttonID){
    	case R.id.radio_voice:
    		chos = "VOICE";
    		savePref("Alert", chos);
    		break;
    	case R.id.radio_tone:
    		chos = "TONE";
    		savePref("Alert", chos);
    		break;
    	case R.id.radio_song:
    		chos = "SONG";
    		savePref("Alert", chos);
    		break;
    	case R.id.radio_vibr:
    		chos = "VIBRATION";
    		savePref("Alert", chos);
    		break;
    	default:
    		chos = "ERR";
    		savePref("Alert", chos);
    		break;
    	}
    	
    	//Toast.makeText(ConfActivity.this, button.getText() + " was chosen.", Toast.LENGTH_SHORT).show();
    	Toast.makeText(ConfActivity.this, chos + " saved to configuration file", Toast.LENGTH_SHORT).show();
    }
    
    public String getBTName(){
    	BluetoothAdapter BTadapter = null;
    	if(BTadapter == null){
    		BTadapter = BluetoothAdapter.getDefaultAdapter();
    	}
    	String name = BTadapter.getName();
    	return name;
    }
    
    public String getBTAddres(){
    	BluetoothAdapter BTadapter = BluetoothAdapter.getDefaultAdapter();
    	System.out.println(BTadapter.getAddress());
    	return BTadapter.getAddress();
    }
    
    public String loadPref(){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	String prefVal = sp.getString("Alert", "ERR");
    	return prefVal;
    }
    
    public void savePref(String key, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	Editor edit = sp.edit();
    	edit.putString(key, value);
    	edit.commit();
    	System.out.println("Conf OK");
    }
}