package com.example.tringuyen.lab8;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ListView list;
    ArrayAdapter<String> adapter;
    static final String URL = "https://cs.gmu.edu/~white/earthquakes.json";
    HashMap<Integer,String> latHashMap = new HashMap<Integer, String>();
    HashMap<Integer,String> longHashMap = new HashMap<Integer, String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // list = findViewById(R.id.listView);
        list = new ListView(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String valueLat = latHashMap.get(position);
                String valueLong = longHashMap.get(position);
                //String value = adapter.getItem(position);
                // start map with implicit intent here
                //Uri loc = Uri.parse(value);
                Uri loc = Uri.parse("geo:0,0?q="+ valueLat+ " "+ valueLong +"&z=3");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(loc);
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        setContentView(list);

        new HttpGetTask()
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,URL);

    }


    private void onFinishGetRequest(String result) {
        try {

            JSONArray earthquakes = (new JSONArray(result));
            int len = earthquakes.length();
            for (int i = 0;i<len; i++) {
                JSONObject quake = earthquakes.getJSONObject(i);
                String region = quake.getString("region");
                String mag = quake.getString("magnitude");
                String occurred = quake.getString("occurred_at");
                // get Lat/Long here
                //String lat = quake.getString("location");
               // String lat = ""; String longitude = "";
                JSONObject loc = quake.getJSONObject("location");
                String lat = loc.getString("latitude");
                String longitude = loc.getString("longitude");
                latHashMap.put(i,lat);
                longHashMap.put(i,longitude);
                //System.out.print("HELOOOOOOOOOOOOOOOOOO" + region);
                adapter.add(region + "  with magnitude = " + mag + " on " + occurred+ " location: lat " + lat + " longitude "+ longitude);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer data = new StringBuffer();
            BufferedReader br = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new
                        URL(params[0]).openConnection();
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String rawData;
                while ((rawData = br.readLine()) != null) {
                    data.append(rawData);
                }
            } catch (MalformedURLException e1) {e1.printStackTrace();
            } catch (IOException e1) {e1.printStackTrace();
            } finally {
                if (br != null)
                    try {  br.close();
                    } catch (IOException e) {e.printStackTrace();}
            }
            return data.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //          Toast.makeText(getApplicationContext(),"String returned" + result.length(),Toast.LENGTH_SHORT).show();
            onFinishGetRequest(result);
        }
    }



}

