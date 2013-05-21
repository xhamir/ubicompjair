package com.example.recordatoriointeligente;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class RecordatorioInteligente extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost tabHost = getTabHost();
        
        // Tab for Dispositivos
        TabSpec dispospec = tabHost.newTabSpec("Dispositivos");
        dispospec.setIndicator("Dispositivos", getResources().getDrawable(R.drawable.icon_disp_tab));
        Intent dispoIntent = new Intent(this, DispoActivity.class);
        dispospec.setContent(dispoIntent);
        
        // Tab for Favoritos
        TabSpec favspec = tabHost.newTabSpec("Favoritos");
        // setting Title and Icon for the Tab
        favspec.setIndicator("Favoritos", getResources().getDrawable(R.drawable.icon_fav_tab));
        Intent favIntent = new Intent(this, FavsActivity.class);
        favspec.setContent(favIntent);
        
        // Tab for Configurations
        TabSpec confspec = tabHost.newTabSpec("Configuraciones");
        // setting Title and Icon for the Tab
        confspec.setIndicator("Configuraciones", getResources().getDrawable(R.drawable.icon_conf_tab));
        Intent confIntent = new Intent(this, ConfActivity.class);
        confspec.setContent(confIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(dispospec); // Adding dispositivos tab
        tabHost.addTab(favspec); // Adding favoritos tab
        tabHost.addTab(confspec);
    }
}