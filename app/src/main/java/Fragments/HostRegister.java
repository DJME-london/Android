package Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musync.musync.Playlist;
import com.example.musync.musync.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.musync.musync.HostPanel;
import com.example.musync.musync.VolleySingleton;

import org.json.JSONObject;

/**
 * Created by tejas on 5/6/2017.
 */

public class HostRegister extends Fragment{
    private View rootview;
    ImageView pass;
    String wifimac;
    EditText t;
    String TAG = "Volley";
    RequestQueue queue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.hostsession_register, container, false);
        pass=(ImageView)rootview.findViewById(R.id.sessionpass);
        t=(EditText)rootview.findViewById(R.id.input_session);
        queue= VolleySingleton.getInstance(getContext()).getRequestQueue();
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissions();
                setupwifi(t.getText().toString());

                String q = t.getText().toString();

                String[] songs = getMusic();

                Map<String,String> postParam = new HashMap<String, String>();
                postParam.put("Email","tejasav1997@gmail.com");
                postParam.put("Name", q);
                for(int i=0; i<songs.length; i++)
                {
                    String j = Integer.toString(i+1);
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
                queue.add(jsonObjReq);


                Intent i = new Intent(getContext(), Playlist.class);
                startActivity(i);



               /* queue = Volley.newRequestQueue(getContext());
                String url = "https://musynco.herokuapp.com/hostedsession?Name=" + q;

                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(request);
                request.setTag(TAG); */




            }
        });





        return rootview;
    }

    private String [] getMusic() {
        try {
            final Cursor mCursor = getActivity().managedQuery(
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


    @Override
    public void onStop() {
        super.onStop();
        queue.cancelAll(TAG);
    }

    public HostRegister() {
        super();
    }
    void askPermissions()
    {
        Uri uri = MediaStore.Audio.Media.getContentUri("EXTERNAL_CONTENT_URI");
        getContext().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("status","it worked");

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                    Toast.makeText(getContext(),"Give permissions",Toast.LENGTH_SHORT);
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1337);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            }
        }


        return;
    }



    void setupwifi(String h)
    {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
        if (    (wifiManager.isWifiEnabled())) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(getContext(),"Turning on wifi for MAC",Toast.LENGTH_SHORT).show();
            wifimac=getMacId();
            Toast.makeText(getContext(),"Turning on wifi for MAC",Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(false);
            Toast.makeText(getContext(),"wifi procedure",Toast.LENGTH_SHORT).show();
        }




        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        boolean methodFound = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite( getActivity().getApplicationContext().getApplicationContext())){
                // Do stuff here
                Log.d("stato","already available");
            }
            else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" +  getActivity().getApplicationContext().getApplicationContext().getPackageName()));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,0);
            }
        }
        for (Method method: wmMethods) {
            if (method.getName().equals("setWifiApEnabled")) {
                methodFound = true;
                Log.d("check","done");
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = h;
                netConfig.preSharedKey="DataAnywhere";
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                try {
                    boolean apstatus = (Boolean) method.invoke(wifiManager, netConfig, true);

                    for (Method isWifiApEnabledmethod: wmMethods) {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
                            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {};
                            for (Method method1: wmMethods) {
                                if (method1.getName().equals("getWifiApState")) {
                                    int apstate;
                                    apstate = (Integer) method1.invoke(wifiManager);
                                    Log.i(this.getClass().toString(), "Apstate ::: "+apstate);
                                }
                            }
                        }
                    }
                    if (apstatus) {

                        Log.d("Splash Activity", "Access Point created");
                        Toast.makeText(getContext(),"Hotspot toggled",Toast.LENGTH_SHORT).show();
                        //move to next
                        Intent i=new Intent(getContext(), HostPanel.class);
                        startActivity(i);
                        SharedPreferences sharedPref = getContext().getSharedPreferences("Preferences",getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        WifiInfo info=wifiManager.getConnectionInfo();
                        editor.putString("SelfBSSIDs",wifimac);
                        editor.commit();

                        //send the bssid to the server along with the youtube host name
                        Log.d("My Bssid Is","it is " + wifimac);
                    }
                    else {
                        Log.d("Splash Activity", "Access Point creation failed");
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!methodFound) {
            Log.d("Splash Activity",
                    "cannot configure an access point");
        }
    }
    public String getMacId() {

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }
}
