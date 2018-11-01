package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.edu.swin.sdmd.suncalculatorjava.place.*;

/**
 * Created by 9726446 on 31/10/18 @ LB1-MAC-017.
 * Basic idea: Import code from the recycler-view exercise and then fiddle with as needed
 */

public class MainActivity extends AppCompatActivity {
    private final List<Place> placeList = new ArrayList<>();
    private PlaceAdapter placeAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initaliseUI();
        populatePlaceList();

    }

    private void initaliseUI() {
        RecyclerView recyclerView = findViewById(R.id.rvPlaceList);

        //Documentation says this boosts performance if each item is the same size so...
        recyclerView.setHasFixedSize(true);

        //Set adapter
        placeAdapter = new PlaceAdapter(placeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        recyclerView.setAdapter(placeAdapter);
    }

    /**
     * Read cities from file.
     * @param bufferedReader file to read
     */
    private void updateDataSet(BufferedReader bufferedReader){
        try{
            String strAry[];
            String string;
            //for each line in the reader {
            while ((string = bufferedReader.readLine()) != null){
                strAry = string.split(",");
                placeList.add(new Place(
                        strAry[0],
                        strAry[1],
                        strAry[2],
                        strAry[3]
                ));
            }
        }
        catch (Exception e){
            Log.e("populate", e.toString());
        }
    }

    private void populatePlaceList() {
        try {
            //First, clear (the custom additions from) the old list. Otherwise I'd need a second list...
            placeList.clear();
            //Next read the original names from raw/au_locations
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(getResources().openRawResource(R.raw.au_locations))
            );
            updateDataSet(bufferedReader);
            bufferedReader.close();

            //Then read any changes in the custom list
            bufferedReader = new BufferedReader(
                    new InputStreamReader(openFileInput("CustomPlaces"))
            );
            updateDataSet(bufferedReader);
            bufferedReader.close();
            placeAdapter.notifyDataSetChanged();
        }
        catch (FileNotFoundException fileNotFound){
            Log.e("Read Custom", "File not found.");
        }
        catch (Exception e) {
            Log.e("Read Error", String.format("Couldn't read location data.\n%s", e.toString()));
        }
    }

    /**
     * Send the chosen data to the view app and launch it.
     * Code recycled from 5_2P
     * @param view the view sending the request
     */
    public void viewTimes(View view){
        Intent i = new Intent();
        i.setClass(getApplicationContext(), ViewActivity.class);
        int object = (int)view.getTag();
        try{
            i.putExtra("place", placeList.get(object));
            startActivity(i);
        }
        catch (Exception e){
            Log.e("viewTimes", e.toString());
        }
    }

    /**
     * Launches activity for adding a new location
     * @param view view that called method
     */
    public void lookupNew(View view){
        Intent i = new Intent();
        i.setClass(getApplicationContext(), LookupActivity.class);
        try{
            startActivityForResult(i, 1);
        }
        catch (Exception e){
            Log.e("Lookup-Launch", e.toString());
        }
    }

    /**
     * Handles updates to the display - circa 5.2P .
     * @param requestCode It's a cheesy way to do it, but switch statements are so much fun~
     * @param resultCode Determines if the child activity terminated properly
     * @param intent an intent with data to pass, hopefully!
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        //Get Errors out of the way first.
        if (resultCode != RESULT_OK) Log.e("Parcel Failed", String.format(
                "Result Code %d, Request Code: %d", resultCode, requestCode
        ));
        else {
            populatePlaceList();
        }
    }
}