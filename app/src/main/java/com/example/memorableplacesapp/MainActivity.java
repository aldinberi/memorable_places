package com.example.memorableplacesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;



import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView places;
    ArrayList<String> placesArray = new ArrayList<>();
    ArrayList<String> savedPlacesArray = new ArrayList<>();
    ArrayList<Double> latArray = new ArrayList<>();
    ArrayList<Double> longArray = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                assert data != null;
                String result = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude",0);
                double longitude = data.getDoubleExtra("longitude",0);

                Location loc = new Location("");

                loc.setLatitude(latitude);
                latArray.add(latitude);
                longArray.add(longitude);

                loc.setLongitude(longitude);

                Log.i("LOKACIJA:", result);
                placesArray.add(result);
                savedPlacesArray.add(result);


                try {
                    sharedPreferences.edit().putString("place",ObjectSerializer.serialize(savedPlacesArray)).apply();
                    sharedPreferences.edit().putString("lat", ObjectSerializer.serialize(latArray)).apply();
                    sharedPreferences.edit().putString("long", ObjectSerializer.serialize(longArray)).apply();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                places.setAdapter(arrayAdapter);



            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        places = findViewById(R.id.placesList);


        sharedPreferences = getSharedPreferences("com.example.memorableplacesapp", Context.MODE_PRIVATE);

        placesArray.add("Add a new place");

        try {
            savedPlacesArray = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("place",ObjectSerializer.serialize(new ArrayList<String>())));
            latArray = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));
            longArray = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("long",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        placesArray.addAll(savedPlacesArray);

        arrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, placesArray);

        places.setAdapter(arrayAdapter);
        places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(getApplicationContext(), Map.class);
                    startActivityForResult(intent, 0);
                } else{
                    Intent intent = new Intent(getApplicationContext(), Map.class);
                    intent.putExtra("latitude", latArray.get(position-1));
                    intent.putExtra("longitude",longArray.get(position-1));
                    startActivity(intent);
                }
            }
        });



    }
}
