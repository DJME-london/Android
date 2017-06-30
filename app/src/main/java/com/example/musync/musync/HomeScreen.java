package com.example.musync.musync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

public class HomeScreen extends AppCompatActivity {
    CardView host,join;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        host=(CardView) findViewById(R.id.hostevent);
        join=(CardView)findViewById(R.id.join);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Host.class);
                startActivity(i);
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Join.class);
                startActivity(i);
            }
        });
        try {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Preferences", getApplicationContext().MODE_PRIVATE);
            String youtubename = sharedPref.getString("YoutubeName","null");
            Log.d("Youtube Name",youtubename);
        }
        catch(Exception e)
        {
            Log.d("Shared Preference","An Exception in Home Screen");
        }
    }

}
