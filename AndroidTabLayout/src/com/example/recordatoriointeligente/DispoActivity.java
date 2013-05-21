package com.example.recordatoriointeligente;

/*
import android.os.Bundle;
import java.util.ArrayList;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DispoActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.photos_layout);
        
        ArrayList<String> yourlist = new ArrayList<String>();
		for(int i = 0;i<20;i++){
			yourlist.add("Dispositivo : "+i);
		}
		
		ListView lstView = getListView();
        lstView.setChoiceMode(2); //ESCOGE MULTIPLES
        lstView.setTextFilterEnabled(true);
        
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, yourlist));
	
	}

	public void onListItemClick(ListView parent, View v, int position, long id, ArrayList<String>yourlist)
	{
		parent.setItemChecked(position,parent.isItemChecked(position));
		Toast.makeText(this, "You have selected" + yourlist.get(position), Toast.LENGTH_SHORT).show();
	}
}
*/



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class Corredor implements Runnable {
	private BTTask task;
	public Corredor(BTTask t) {
		this.task = t;
	}
	public void run() {
		this.task.execute();
	}
	
}


class BTTask extends AsyncTask< Void, Void, Void>{
	private DispoActivity a;
	
	public BTTask(DispoActivity activity){
		super();
		this.a = activity;
	}
	@Override
	protected Void doInBackground(Void ...params) {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth == null) return null;
		
		while( bluetooth.getState() != 12 ){
			
			System.out.println("Esperando pro el estado prendido.");
			
		}
		
		bluetooth.startDiscovery();
		try{
			Message msg = new Message();
			msg.obj = "clear";
			DispoActivity.mHandler.sendMessage(msg);
		}catch(Exception e){
			System.err.println("Something is wrong. :"+e);
		}
		this.a.intentScan();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bluetooth.cancelDiscovery();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void parametros){
		this.a.addTask();
	}
	
}

public class DispoActivity extends Activity implements OnInitListener {

private static final int REQUEST_ENABLE_BT = 1;
	
	Button btnScanDevice;
	Button fav;
	TextView stateBluetooth;
	EditText retrasoField;
	Button retrasoSave;
	private static TextToSpeech mTts;
	
	BluetoothAdapter bluetoothAdapter;
	
	static Handler mHandler;
	private static final String FILENAME = "devices.txt";
	private static final String DELAY = "retraso";
    private ListView listDevicesFound;
    private int retraso;
	private ArrayAdapter<String> btArrayAdapter;
	
	/** Called when the activity is first created. */
    //ate ArrayAdapter<String> btArrayAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTts = new TextToSpeech(this, this);
        fav = (Button)findViewById(R.id.fav);
        btnScanDevice = (Button)findViewById(R.id.scandevice);
        retrasoSave = (Button)findViewById(R.id.save);
        retrasoField = (EditText)findViewById(R.id.retraso);
        loadRetraso();
        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        listDevicesFound = (ListView)findViewById(R.id.devicesfound);
        listDevicesFound.setChoiceMode(2); //CHOICE_MODE_MULTIPLE
        listDevicesFound.setTextFilterEnabled(false);
        
        btArrayAdapter = new ArrayAdapter<String>(DispoActivity.this, android.R.layout.simple_list_item_multiple_choice);
        listDevicesFound.setAdapter(btArrayAdapter);
        CheckBlueToothState();
        Corredor corr = new Corredor(new BTTask(this));
		DispoActivity.mHandler = new Handler(Looper.getMainLooper()) {
			@Override
    		public void handleMessage(Message msg){
	    		if(msg.obj == "clear"){
	    			System.out.println("Supuestamente se limpio la tabla");
	    			btArrayAdapter.clear();
	    		}	
	    		super.handleMessage(msg);
    		}
		};
		this.retraso = 3000;
		DispoActivity.mHandler.postDelayed(corr, this.retraso);
        
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
        //v.setOnClickListener(favOnClickListener);
        retrasoSave.setOnClickListener(retrasoSaveOnClickListener);
       
        listDevicesFound.setOnItemClickListener( new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
        		          Toast.LENGTH_SHORT).show();
        		textToSpeech((String) ((TextView) view).getText());
        	}
        });
    }
    
    public void addTask(){
    	Corredor corr = new Corredor(new BTTask(this));
    	DispoActivity.mHandler.postDelayed(corr, this.retraso);
    }
    
    public void listClear(){
    	
    	
    }
    
    public void intentScan(){
    	registerReceiver(ActionFoundReceiver, 
        		new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }
    
    public void textToSpeech(String device){
    	String textToSay = "Haz perdido este dispositivo: "+device+", joder!! ostia tio!";
    	mTts.speak(textToSay, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    public void saveDeviceInFavorites(String device){
    	String brinco = "\n";
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.write(device.getBytes());
			fos.write(brinco.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		alert("Se ha guardado en favoritos "+device);
    }
    
    public void verifyDeviceInFavorites(ArrayList<String> devices){
    	String archivoString = null;
    	try{
			BufferedReader archivo = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
			ArrayList<String> favorites = new ArrayList<String>();
			while( (archivoString = archivo.readLine()) != null ){
				favorites.add(archivoString);
			}
			for(String value : favorites){
				System.out.println("Leyendo de archivo: "+value);
			}
			for(String device : devices){
				System.out.println("Leyendo de archivo: "+device);
				if(!favorites.contains(device)) alarm(device);
			}
			if(favorites.contains(devices)){
				alert("Si lo contiene");
			}
			
		} catch(FileNotFoundException e){
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void alarm(String device){
		alert("Se te ha perdido el dispositivo: "+device);
		textToSpeech(device);
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mTts != null) {
			   mTts.stop();
			   mTts.shutdown();
			   mTts = null;
		}
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
	}

	private void CheckBlueToothState(){
    	if (bluetoothAdapter == null){
        	stateBluetooth.setText("Bluetooth es soportado");
        }else{
        	if (bluetoothAdapter.isEnabled()){
        		if(bluetoothAdapter.isDiscovering()){
        			stateBluetooth.setText("Bluetooth is currently in device discovery process.");
        		}else{
        			stateBluetooth.setText("Bluetooth is Enabled.");
        			btnScanDevice.setEnabled(true);
        		}
        	}else{
        		stateBluetooth.setText("Bluetooth is NOT Enabled!");
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}
        }
    }
    
    private Button.OnClickListener btnScanDeviceOnClickListener
    = new Button.OnClickListener(){

		
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			btArrayAdapter.clear();
			bluetoothAdapter.startDiscovery();
		}};

	private Button.OnClickListener retrasoSaveOnClickListener = new Button.OnClickListener(){
		
		public void onClick(View arg0){
			saveRetraso(DELAY,Integer.valueOf(retrasoField.getText().toString()));
		}
	};
	
	public Button.OnClickListener favOnClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			SparseBooleanArray checked = listDevicesFound.getCheckedItemPositions();
	        ArrayList<String> selectedItems = new ArrayList<String>();
	        for (int i = 0; i < checked.size(); i++) {
	            // Item position in adapter
	            int position = checked.keyAt(i);
	            // Add sport if it is checked i.e.) == TRUE!
	            if (checked.valueAt(i))
	                selectedItems.add(btArrayAdapter.getItem(position));
	        }
	 
	        String[] outputStrArr = new String[selectedItems.size()];
	 
	        for (int i = 0; i < selectedItems.size(); i++) {
	            outputStrArr[i] = selectedItems.get(i);
	            System.out.println(outputStrArr[i]);
	        }
	        
	        	        
	        Intent intent = new Intent(getApplicationContext(),
	                FavsActivity.class);
	 
	        // Create a bundle object
	        Bundle b = new Bundle();
	        b.putStringArray("selectedItems", outputStrArr);
	 
	        // Add the bundle to the intent.
	        intent.putExtras(b);
	 
	        // start the ResultActivity
	        startActivity(intent);
						
		}
		
	};
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQUEST_ENABLE_BT){
			CheckBlueToothState();
		}
	}
    
	private void saveRetraso(String key, int value){
		
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
		alert("Retraso guardado.");
		
	}
	
	private void alert(String string) {
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		CharSequence text = string;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private void loadRetraso(){
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		this.retraso = sp.getInt(DELAY, 10000);
		System.out.println("Este es el retraso: "+this.retraso);
		retrasoField.setText(String.valueOf(this.retraso), TextView.BufferType.EDITABLE);
		
	}
	
	
	
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            System.out.println("device:"+device.getName()+" address: "+device.getAddress());
	            btArrayAdapter.notifyDataSetChanged();
	        }
		}};

	
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
}
