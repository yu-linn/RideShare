package project21.rideshareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

// these lines are giving compile error. just commented out.
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import backend.Event;
import backend.EventEntity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private View rectangleBorder;
    private TextView eventName;
    private TextView eventDateLocation;
    private TextView driverOrPassenger;
    private TextView driver_Name;
    private TextView driverInformation;
    private ImageButton profileB;
    private ImageButton createEventB;
    private Button signOutBtn;
    private ImageView searchButton;
    private EditText searchEvent;

    private ListView lists;
    private String userName;
    private Client myKinveyClient;
    private  ArrayList<String> allEventNames;
    private ArrayList<Event> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileB = (ImageButton) findViewById(R.id.profileButton);
        profileB.setOnClickListener(this);
        myKinveyClient = Client.sharedInstance();
//        rectangleBorder = (View) findViewById(R.id.myRectangleView);
//        rectangleBorder.setOnClickListener(this);
        createEventB = (ImageButton) findViewById(R.id.createEventButton);
        createEventB.setOnClickListener(this);
        signOutBtn = (Button) findViewById(R.id.signOutButton);
        signOutBtn.setOnClickListener(this);
//        eventName = (TextView) findViewById(R.id.event_name);
//        eventDateLocation = (TextView) findViewById(R.id.event_date_location);
//        driverOrPassenger = (TextView) findViewById(R.id.driver_or_pass);
//        driver_Name = (TextView) findViewById(R.id.driver_name);
//        driverInformation = (TextView) findViewById(R.id.driver_info);

        userName = myKinveyClient.user().getUsername();

        searchEvent = (EditText) findViewById(R.id.search_event);
        searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        lists = (ListView) findViewById(R.id.user_events);
        allEventNames = new ArrayList<String>();
        events = new ArrayList<Event>();

        // Searches for event names as rider, in callback searches for event names as driver, then renders it
        searchEventsWhereUserIsDriver();

        //replace with data

        // If this is their first time logging in, display the tutorial
        // Right now, it is always turned off
        // TODO

        String check = getIntent().getStringExtra("newUser");
        if (check != null && check.equals("true")) {
            tutorialWalkThrough();
        }

    }


    // defines action when onClick
    @Override
    public void onClick(View v) {
        if (v == profileB) {
            goToProfile();
        } else if (v == createEventB) {
            goToCreateEvent();
        } else if (v == searchButton) {
            String findEventName = searchEvent.getText().toString();
            Intent intent = new Intent(getApplicationContext(), EventSearchResultActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("eventName", findEventName);
            startActivity(intent);
        } else if (v == signOutBtn) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void goToProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void goToEvent() {
        Intent intent = new Intent(HomeActivity.this, EventPageActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void goToCreateEvent() {
        Intent intent = new Intent(HomeActivity.this, EventCreateActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    public void onBackPressed() {
        // does not allow pressing back to go to login page
    }

    // Code to display tutorial upon first time logging in
    private void tutorialWalkThrough() {
        // Start the popup
        startActivity(new Intent(HomeActivity.this, TutorialPopup.class));
    }

    private void searchEventsWhereUserIsDriver() {

        Query q = myKinveyClient.query();
        q.equals("userName", userName);

        AsyncAppData<EventEntity> myData = myKinveyClient.appData("drivers", EventEntity.class);
        myData.get(q, new KinveyListCallback<EventEntity>() {
            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "Error fetching data: " + t.getMessage());
            }

            @Override
            public void onSuccess(EventEntity[] eventEntities) {
                for (EventEntity event : eventEntities) {
                    allEventNames.add(event.getEventname());
                }
                searchEventsWhereUserIsRider();

            }
        });
    }
    private ArrayList<String> searchEventsWhereUserIsRider() {
        final ArrayList<String> eventNames = new ArrayList<String>();

        Query q = myKinveyClient.query();
        q.equals("userName", userName);

        AsyncAppData<EventEntity> myData = myKinveyClient.appData("riders", EventEntity.class);
        myData.get(q, new KinveyListCallback<EventEntity>() {
            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "Error fetching data: " + t.getMessage());
            }

            @Override
            public void onSuccess(EventEntity[] eventEntities) {
                for (EventEntity event : eventEntities) {
                    allEventNames.add(event.getEventname());
                }

                for (String eventname: allEventNames){
                    getEventFromEventName(eventname);
                }

            }
        });

        return eventNames;
    }



    /**
     * Send a query to events with event name and convert Event Entity to event
     *
     * @param eventName
     * @return
     */
    private void getEventFromEventName(String eventName) {

        final EventEntity[] eventEntityArray = new EventEntity[1];
        Event event;

        Query q = myKinveyClient.query();
        q.equals("eventName", eventName);

        AsyncAppData<EventEntity> myData = myKinveyClient.appData("events", EventEntity.class);
        myData.get(q, new KinveyListCallback<EventEntity>() {
            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "Error fetching data: " + t.getMessage());
            }

            @Override
            public void onSuccess(EventEntity[] eventEntities) {
                eventEntityArray[0] = eventEntities[0];

                EventEntity eventEntity = eventEntityArray[0];
                ArrayList<Float> eventGeolocs = new ArrayList<Float>();
                /*for (double geoloc : eventEntity.getEventGeoloc()){
                eventGeolocs.add((Float)((Double)geoloc).floatValue());
                }*/
                
                Event event = new Event((String) eventEntity.get("_id").toString(), eventEntity.getEventAdmins().get(0),
                        eventEntity.getEventname(), eventEntity.getEventDatetime(),
                        eventGeolocs, eventEntity.getPostContent());
                events.add(event);


                UserEventsListAdapter adapter = new UserEventsListAdapter(getApplicationContext(), events);

                lists.setAdapter(adapter);
            }
        });


    }
    
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Home Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }

}
