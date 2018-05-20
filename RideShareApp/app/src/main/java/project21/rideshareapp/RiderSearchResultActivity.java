package project21.rideshareapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.DriverEntity;
import backend.RiderEntity;

import static project21.rideshareapp.R.id.eventName;

public class RiderSearchResultActivity extends AppCompatActivity {
    private Client mKinveyClient;
    private ListView listview;
    private DriverEntity[] driverList;
    private ProgressDialog dialog;
    private String event_id;
    private TextView titleText;
    private TextView errorText;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_search_result);
        mKinveyClient = Client.sharedInstance();
        event_id  = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Drivers for: " + eventName);
        errorText = (TextView) findViewById(R.id.errorText);

        // The reason to use Map here is so that we can add more stuffs to
        // query without having to change method signature
        HashMap data = new HashMap();
        data.put("event_id", event_id);
        searchForCars(data);
    }

    public void searchForCars(HashMap data){
        listview = (ListView) findViewById(R.id.rider_search_result_listview);
        event_id = (String)data.get("event_id");

        // Queries to send to DB
        Query myQuery = mKinveyClient.query();
        myQuery.equals("eventId", event_id);

        dialog = ProgressDialog.show(RiderSearchResultActivity.this, "", "Searching for drivers", true, false);
        AsyncAppData<DriverEntity> results = mKinveyClient.appData("drivers", DriverEntity.class);
        results.get(myQuery, new KinveyListCallback<DriverEntity>() {

            @Override
            public void onSuccess(DriverEntity[] driverEntities) {
                if (dialog.isShowing()){
                    dialog.cancel();
                }
                List<DriverEntity> temp = new ArrayList<DriverEntity>();
                for (int i=0; i < driverEntities.length; i++) {
                    int numSeatsLeft = (int) driverEntities[i].get("numSeatsLeft");
                    if (numSeatsLeft > 0) {
                        temp.add(driverEntities[i]);
                    }
                }
                driverList = temp.toArray(new DriverEntity[temp.size()]);
                final DriverListArrayAdapter adapter = new DriverListArrayAdapter(RiderSearchResultActivity.this,
                        driverList);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String userName = mKinveyClient.user().getUsername();
                        final DriverEntity selectedCar = driverList[position];
                        // 1. checks whether rider user is requesting the his/her own driver post.
                        int seatsLeft = (int) selectedCar.get("numSeatsLeft");
                        if (selectedCar.get("userName").equals(userName)) {
                            errorText.setText("You can't send request to yourself.");
                            return;
                        // 2. no seat available.
                        } else if (seatsLeft == 0) {
                            errorText.setText("Sorry, no seat is available for that driver.");
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(RiderSearchResultActivity.this);
                        builder.setMessage("Are you sure you want to request " + (String)selectedCar.get("userName") + "'s car?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, final int id) {
                            // TODO: What happens when user request the car goes here
                            Query q1 = mKinveyClient.query().equals("userName", mKinveyClient.user().getUsername());
                            Query q2 = mKinveyClient.query().equals("eventName", selectedCar.get("eventName"));
                            Query q3 = mKinveyClient.query().equals("driverID", selectedCar.get("userName"));
                            Query query = q1.and(q2).and(q3);

                            //final String driverID = (String) selectedCar.get("id");

                            AsyncAppData<RiderEntity> myData = mKinveyClient.appData("riders", RiderEntity.class);
                            myData.get(query, new KinveyListCallback<RiderEntity>() {
                                @Override
                                public void onSuccess(RiderEntity[] riderEntities) {
                                    if(riderEntities.length == 0){
                                        // Rider isn't registered yet, add rider to database
                                        RiderEntity riderRequest = new RiderEntity();
                                        riderRequest.put("userName", mKinveyClient.user().getUsername());
                                        riderRequest.put("eventName", selectedCar.get("eventName"));
                                        riderRequest.put("driverID", selectedCar.get("userName") );

                                        AsyncAppData<RiderEntity> allRequests = mKinveyClient.appData("riders", RiderEntity.class);

                                        allRequests.save(riderRequest, new KinveyClientCallback<RiderEntity>() {
                                            @Override
                                            public void onSuccess(RiderEntity riderEntity) {
                                                Log.e("TAG", "data saved successfully.");
                                                Toast.makeText(RiderSearchResultActivity.this, "Request Sent!",
                                                        Toast.LENGTH_LONG).show();
                                                // update driver num seats left
                                                DriverEntity driverEntity = new DriverEntity();
                                                String driverID = (String) selectedCar.get("_id");
                                                Log.e("TAG", "driverID is: " + driverID);
                                                AsyncAppData<DriverEntity> myData = mKinveyClient.appData("drivers", DriverEntity.class);
                                                myData.getEntity(driverID, new KinveyClientCallback<DriverEntity>() {
                                                    @Override
                                                    public void onSuccess(DriverEntity driver) {
                                                        int numSeatsLeft = (int) driver.get("numSeatsLeft");
                                                        driver.put("numSeatsLeft", numSeatsLeft - 1);
                                                        // now save
                                                        AsyncAppData<DriverEntity> mydrivers = mKinveyClient.appData("drivers", DriverEntity.class);
                                                        mydrivers.save(driver, new KinveyClientCallback<DriverEntity>() {
                                                            @Override
                                                            public void onFailure(Throwable e) {
                                                                Log.e("TAG", "failed to save event data", e);
                                                            }
                                                            @Override
                                                            public void onSuccess(DriverEntity r) {
                                                                Log.e("TAG", "Driver num seats left updated successfully.");
                                                                // TODO: go to driver search acitivty
                                                                // go to home page for now
                                                                goToHomePage();
                                                            }
                                                        });
                                                    }
                                                    @Override
                                                    public void onFailure(Throwable error) {
                                                        Log.e("TAG", "failed to fetchByFilterCriteria", error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Throwable e) {
                                                Log.e("TAG", "failed to save event data", e);
                                            }

                                        });

                                    } else {
                                        // Rider already requested this ride
                                        Toast.makeText(RiderSearchResultActivity.this, "You have already requested this ride!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    // Don't do anything
                                }
                            });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

    }

    private void goToHomePage(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}
