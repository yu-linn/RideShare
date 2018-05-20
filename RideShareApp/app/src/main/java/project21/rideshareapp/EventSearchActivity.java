package project21.rideshareapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import backend.DriverEntity;
import backend.EventEntity;
import backend.RiderEntity;

/*
    This activity represents the page where users can search
    for some event through some input fields.
 */

public class EventSearchActivity extends AppCompatActivity implements View.OnClickListener {

    Client myKinveyClient;
    Button searchEventBtn;
    Button createEventBtn;
    EditText searchEditText;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search);

        // get client instance
        myKinveyClient = Client.sharedInstance();

        listView = (ListView) findViewById(R.id.search_result_listview);

        createEventBtn = (Button)findViewById(R.id.create_event_btn);
        searchEventBtn = (Button)findViewById(R.id.search_event_btn);
        searchEditText = (EditText)findViewById(R.id.search_edit_text);
        createEventBtn.setOnClickListener(this);
        searchEventBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       if (v == createEventBtn){
           Intent intent = new Intent(getApplicationContext(), EventCreateActivity.class);
           startActivity(intent);
        }
        if (v == searchEventBtn){
            //Log.e("SearchEventActivity", "Search button clicked!");
            String searchString = searchEditText.getText().toString();
            Context context = getApplicationContext();
            searchEventThenDisplay(searchString);
        }
    }

    // checks if userID is registered as a driver or rider or neither for given event name.
    public void ifUserAlreadyRegistered(final String eventname, String userID ) {

        Query queryEvent = new Query().equals("eventname", eventname);
        Query queryUser = new Query().equals("userID", userID);
        final Query query = queryEvent.and(queryUser);
        AsyncAppData<DriverEntity> allDrivers = myKinveyClient.appData("drivers", DriverEntity.class);
        final AsyncAppData<RiderEntity> allRiders = myKinveyClient.appData("riders", RiderEntity.class);

        allRiders.get(query, new KinveyListCallback<RiderEntity>() {
            @Override
            public void onSuccess(RiderEntity[] riderEntities) {
                // user is a rider for this event
                if (riderEntities.length == 1) {
                    // send intent to display riders
                    //Intent intent = new Intent(getApplicationContext(), RidersForEvent.class);
                    //intent.putExtra("eventname", eventname);
                    //startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("TAG", "failed to find in riders", throwable);
            }
        });

        allDrivers.get(query, new KinveyListCallback<DriverEntity>() {
            @Override
            public void onSuccess(DriverEntity[] driverEntities) {
                // user is a rider for this event
                if (driverEntities.length == 1) {
                    // send intent to display riders
                    //Intent intent = new Intent(getApplicationContext(), DriversForEvent.class);
                    //intent.putExtra("eventname", eventname);
                    //startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("TAG", "failed to find in drivers", throwable);
            }
        });
    }

    // if user is not registered as a driver nor rider for this event,
    // build button for choosing to send intent to register as either driver or rider activity.
    public void ifUserNotYetRegistered(final String eventname) {
        // prompts user to register as either driver or rider for this event
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Who do you wish to be for this event?");
        // Add the buttons
        builder.setPositiveButton("Driver", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), RegisterDriverActivity.class);
                intent.putExtra("eventname", eventname);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Passenger", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), RegisterRider.class);
                intent.putExtra("eventname", eventname);
                startActivity(intent);
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void buildEventAdapters(final EventEntity[] foundEvents, final String eventname, final String username) {
        //Log.v("TAG", "received "+ foundEvents.length + " events");
        // adds clickable adapter item for each found event in the listview.
        final EventListArrayAdapter adapter = new EventListArrayAdapter(getApplicationContext(),
                foundEvents);
        listView.setAdapter(adapter);

        // when an event is clicked,
        // must check whether user is registered as one of driver or rider.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventEntity event = foundEvents[position];
                final String event_id = (String)event.get("_id"); // ???
                //Log.e("onItemClick","position" + position + "clicked!");
                //Log.e("clicked event_id", event_id);
                // 1. if already registered,
                ifUserAlreadyRegistered(eventname, username);
                // 2. if not prompt for registration.
                ifUserNotYetRegistered(eventname);
            }
        });
    }

    public void searchEventThenDisplay(final String eventname){

        Query searchQuery = myKinveyClient.query();
        searchQuery.equals("event_name", eventname);
        AsyncAppData<EventEntity> allEvents = myKinveyClient.appData("events", EventEntity.class);
        // kinvey db query to search given event name in all events.
        allEvents.get(searchQuery, new KinveyListCallback<EventEntity>() {
            // searched events are returned in results
            @Override
            public void onSuccess(EventEntity[] results) {
                final EventEntity[] foundEvents = results;
                String username = myKinveyClient.user().getUsername();
                buildEventAdapters(foundEvents, eventname, username);
            }
            @Override
            public void onFailure(Throwable error) {
                Log.e("TAG", "failed to fetchByFilterCriteria", error);
            }
        });
    }
}
