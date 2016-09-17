package org.citas.basicdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListDisplayActivity extends AppCompatActivity {
    ReadingArrayAdapter _adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);

        Intent intent = getIntent();
        String content = intent.getStringExtra("CONTENT");

        List<Reading> readings = decodeContentJson("{\"content\" : " + content + "}");
        _adapter = new ReadingArrayAdapter(this, readings, R.layout.list_readings_custom);

        ListView list = (ListView) findViewById(R.id.list_contents);
        list.setAdapter(_adapter);

        return;
    }

    private List<Reading> decodeContentJson(String content) {
        List<Reading> readings = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(content);

            JSONArray dataArr = rootObj.getJSONArray("content");

            int iDataCount = dataArr.length();
            for(int i = 0; i < iDataCount; i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                String source = dataObj.getString("source");

                JSONObject actualDataObj = dataObj.getJSONObject("data");
                String type = actualDataObj.getString("type");
                double val = actualDataObj.getDouble("value");

                readings.add(new Reading(source, type, val));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return readings;
    }
}
