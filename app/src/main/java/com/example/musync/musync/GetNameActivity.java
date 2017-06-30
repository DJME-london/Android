package com.example.musync.musync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class GetNameActivity extends AppCompatActivity {
    private EditText name;
    private ImageView pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name);
        name=(EditText)findViewById(R.id.input_name);
        pass=(ImageView)findViewById(R.id.namepass);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Preferences",getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                String namee=name.getText().toString();
                editor.putString("Name",namee);
                editor.commit();

                Intent i= new Intent(getApplicationContext(),HomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

}
