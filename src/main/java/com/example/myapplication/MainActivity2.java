package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    TextView txvTop, txvMid, txvBot;
    int datacount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        WeatherData wdh = null;
        wdh = (WeatherData) getIntent().getSerializableExtra("WeatherData");
        datacount = getIntent().getIntExtra("datacount", 0);

        txvMid = findViewById(R.id.textView3);
        String s = "";
        s = s + wdh.startTime[datacount] + '\n' + wdh.endTime[datacount] + '\n'
                + wdh.parameterName[datacount] + wdh.parameterUnit[datacount] + " "
        ;
        txvMid.setText(s);


    }

}