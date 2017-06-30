package com.example.musync.musync;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Join extends AppCompatActivity {
    private ArrayList<String> nearby_Sessions;
    private ArrayAdapter<String> spinner_adapter;
    private Spinner spinner;
    private ImageView join;
    static int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private boolean status= false;
    List<ScanResult> wifiAvailable;
    RequestQueue mRequestqueue;
    WifiManager mainWifi;
    BroadcastReceiver receiverWifi;

    RequestQueue queue;
    String TAG = "Volley";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        spinner=(Spinner)findViewById(R.id.spinnersession);
        nearby_Sessions=new ArrayList<String>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            status= true;
            //do something, permission was previously granted; or legacy device
        }




       /* nearby_Sessions.add("Vishrut");
        nearby_Sessions.add("Gautam");
        nearby_Sessions.add("Sukhad");
        nearby_Sessions.add("Harsh");*/
        spinner_adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,nearby_Sessions);
        //spinner.setSelection(0);
        //spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Tap for Sessions");

        spinner.setAdapter(spinner_adapter);

        join=(ImageView)findViewById(R.id.joinpass);
        mRequestqueue=VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        getRequest();
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Process the syncing of the playlist to the respected session
                String[] songs = getMusic();

                Map<String,String> postParam = new HashMap<String, String>();
                postParam.put("Email","kohlivishrut@gmail.com");
                for(int i=1; i<songs.length; i++)
                {
                    String j = Integer.toString(i);
                    postParam.put("song" + j , songs[i]);
                    Log.d(TAG,songs[i] );
                }




                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, "https://musynco.herokuapp.com/song_saver" ,new JSONObject(postParam), new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }

                },new Response.ErrorListener(){

                    public void onErrorResponse(VolleyError error){
                        VolleyLog.d(TAG,"Error:" + error.getMessage());

                    }
                });
                mRequestqueue.add(jsonObjReq);

                Intent i = new Intent(getApplicationContext(), Playlist.class);
                startActivity(i);






            }
        });



        mainWifi = (WifiManager)getApplicationContext().getSystemService(this.WIFI_SERVICE);


        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        receiverWifi = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiAvailable=mainWifi.getScanResults();
                for(int i=0;i<wifiAvailable.size();++i)
                {

                    Log.d("found Something",wifiAvailable.get(i).SSID);
                    if(wifiAvailable.get(i).SSID.equals("VishrutsBash"))
                    {
                        Log.d("st","match found");
                        nearby_Sessions.add("VishrutsBash");
                        spinner_adapter.notifyDataSetChanged();
                    }
                    //Send the entire list of BSSIDs to te server to check the wifi sessions available there

                }

            }
        };

        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(status == true) {
            mainWifi.startScan();
            Log.d("checkjoin","workdone"
            );
        }
        Toast.makeText(this,"Starting Scan...",Toast.LENGTH_SHORT).show();
    }


    private String [] getMusic() {
        try {
            final Cursor mCursor = managedQuery(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DISPLAY_NAME}, null, null,
                    "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

            int count = mCursor.getCount();

            String[] songs = new String[count];
            int i = 0;
            if (mCursor.moveToFirst()) {
                do {
                    songs[i] = mCursor.getString(0);
                    Log.d("Song Name", songs[i]);
                    i++;
                } while (mCursor.moveToNext());
            }


            mCursor.close();
            String str1 = Arrays.toString(songs);
            Log.d("TAG", str1);
            return songs;

        } catch (Exception e) {
            Log.d("Error in Cursor", e.toString());
            return null;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }



    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    void getRequest()
    {
        StringRequest getNews = new StringRequest(Request.Method.GET, "https://musynco.herokuapp.com/song2",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        /***************** JSON PARSING OF THE RESPONSE*********************/
                        try{
                            Log.d("hey","join request sent");
                            Toast.makeText(getApplicationContext(),"Join request sent",Toast.LENGTH_SHORT).show();


                        }
                        catch (Exception e)
                        {
                            Log.d("JSON Parse Error",e.toString());
                        }


                    }
                },
                //******************** Enable the starting of app even in the case when internet is no available with default banner images **********/
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                    }
                }
        );

        mRequestqueue.add(getNews);
    }
}
