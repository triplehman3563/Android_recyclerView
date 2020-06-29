package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Externalizable;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    WeatherDataHandler wdh = new WeatherDataHandler();
    String TAG = "TAG";
    RecyclerView recyclerView;
    Context Main;
    int image[];
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        image = new int[]{R.drawable.icon_hot, R.drawable.icon_cold};
        Main = this;
        myThread.start();
        //welcome message
        SharedPreferences shared = getSharedPreferences("info", MODE_PRIVATE);
        String lastStatus = null;
        lastStatus = shared.getString("Last Operation", "null");
        if (lastStatus.compareTo("null") != 0) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();

        }

        recyclerView = findViewById(R.id.recycleView);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 101) {
                    Log.d(TAG, "handleMessage: dataUpdate");
                    myAdapter = new MyAdapter(Main, wdh.starttime, wdh.endtime, wdh.parametername, wdh.parameterunit, image);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Main));
                    //myAdapter.notifyDataSetChanged();
                }

            }
        };


    }


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref;
        pref = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Last Operation", "Stop");
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences pref;
        pref = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Last Operation", "Destroy");
        editor.commit();


    }

    public void changeActivity(int datacount) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MainActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("WeatherData", (Serializable) wdh.weatherData);
        bundle.putInt("datacount", datacount);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {


            Log.d(TAG, "run: ");
            wdh.jsonParse(mQueue);

            synchronized (this) {
                while (wdh.weatherData.startTime == null) {
                }
            }

            Message msg = Message.obtain();
            msg.what = 101;
            msg.obj = null;
            handler.sendMessage(msg);


        }
    });
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 101) {


            }
            return false;
        }
    });

}


class WeatherDataHandler {
    String TAG = "TAG";
    WeatherData weatherData;
    String[] starttime, endtime, parametername, parameterunit;

    String url = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=C" +
            "WB-9A3F0E3E-CC4F-4284-84D3-F57EBDAB6A08&format=JSON&locationName=%E8%87%BA%E5%8C%97%E5%B8%82&elementName=MinT";

    public void jsonParse(RequestQueue mQueue) {
        weatherData = new WeatherData(starttime, endtime, parametername, parameterunit);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONObject("records")
                                    .getJSONArray("location")
                                    .getJSONObject(0)
                                    .getJSONArray("weatherElement")
                                    .getJSONObject(0)
                                    .getJSONArray("time");
                            starttime = new String[jsonArray.length()];
                            endtime = new String[jsonArray.length()];
                            parametername = new String[jsonArray.length()];
                            parameterunit = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject time = jsonArray.getJSONObject(i);
                                String startTime = time.getString("startTime");
                                String endTime = time.getString("endTime");
                                String parameterName = time.getJSONObject("parameter").getString("parameterName");
                                String parameterUnit = time.getJSONObject("parameter").getString("parameterUnit");
                                Log.d("startTime" + ": ", "" + startTime);
                                Log.d("endTime" + ": ", "" + endTime);
                                Log.d("parameterName" + ": ", "" + parameterName);
                                Log.d("parameterUnit" + ": ", "" + parameterUnit);
                                starttime[i] = startTime;
                                endtime[i] = endTime;
                                parametername[i] = parameterName;
                                parameterunit[i] = parameterUnit;
                                weatherData.startTime = starttime;
                                weatherData.endTime = endtime;
                                weatherData.parameterName = parametername;
                                weatherData.parameterUnit = parameterunit;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}

class WeatherData implements Serializable {
    String[] startTime;
    String[] endTime;
    String[] parameterName;
    String[] parameterUnit;

    WeatherData(String[] startTime,
                String[] endTime,
                String[] parameterName,
                String[] parameterUnit) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.parameterName = parameterName;
        this.parameterUnit = parameterUnit;
    }
}