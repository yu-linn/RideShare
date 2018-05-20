package project21.rideshareapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.List;

import backend.Driver;
import backend.Event;
import backend.RideShareSystem;
import backend.Rider;
import backend.User;

/*
    This activity represents the page this will display an event's
    data, and allows users to sign up, etc.

    The event data (or some way to access it) is expected to be passed
    into this activity
 */

public class EventPageActivity extends AppCompatActivity {

    private TextView eventName;
    private TextView createdBy;
    private TextView dateAndLocation;
    private TextView registeredStatus;
    private TextView driverCapacity;
    private Space riderSpace;
    private Event event;
    private RideShareSystem rideShareSystem;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        /* Commented out for demo
        event = (Event)getIntent().getExtras().get("Event");
        eventSystem = (EventSystem)getIntent().getExtras().get("EventSystem");
        user = (User)getIntent().getExtras().get("User");
        */

        eventName = (TextView) findViewById(R.id.eventName);
        createdBy = (TextView) findViewById(R.id.createdBy);
        dateAndLocation = (TextView) findViewById(R.id.dateAndLocation);
        registeredStatus = (TextView) findViewById(R.id.registeredStatus);
        riderSpace = (Space) findViewById(R.id.riderSpace);

        //eventName.setText(event.getEventname());
        eventName.setText("HackMIT");  // For Demo only
        //setTitle(event.getEventname());
        setTitle("HackMIT"); // For Demo only
        createdBy.setText("Created By John Smith");
        //dateAndLocation.setText(event.getEventDatetime() + " at Location");
        dateAndLocation.setText("6pm, September 17, 2016 at 77 Massachusetts Ave, Cambridge");

        // For Demo
        onCreateNotRegistered();

    }

    private void onCreateDriver(Driver driver){

        registeredStatus.setText("Registered as Driver");
        driverCapacity = (TextView) findViewById(R.id.driverCapacity);
        driverCapacity.setText("Total Number of Seats: " + driver.getTotalSeats());

        TextView riderListTextView = (TextView) findViewById(R.id.riderList);
        riderListTextView.setVisibility(View.VISIBLE);
        List<String> riders = driver.getAcceptedList();
        List<String> requests = driver.getRequestedList();

        createListofRiders(riders);
        createListOfRequestedRiders(requests);


    }

    private void onCreateRider(Rider rider){

        registeredStatus.setText("Registered as Rider");

    }

    private void onCreateNotRegistered(){
        registeredStatus.setText("Not Registered");
        Button registerAsDriver = (Button) findViewById(R.id.registerAsDriver);
        Button registerAsRider = (Button) findViewById(R.id.registerAsRider);
        registerAsDriver.setVisibility(View.VISIBLE);
        registerAsRider.setVisibility(View.VISIBLE);
        registerAsDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RegisterDriverActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserID", user.getUserID());
                bundle.putString("EventID", event.getEventID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void joinAsDriver(){

    }

    private void joinAsRider(){

    }

    private void createListofRiders(List<String> riderIDs){

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.activity_event_page);
        TextView title = new TextView(this);
        title.setText("In Car:");
        layout.addView(title);

        // For each rider, add a link to the user's profile
        for (final String riderID : riderIDs){

            // With backend: get rider name from riderID

            // Create textfield that links to rider's profile
            final TextView newTextView = new TextView(this);
            newTextView.setText(riderID);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                public void onClick(View view){
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("RiderID", riderID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            };
            layout.addView(newTextView);
        }
    }

    private void createListOfRequestedRiders(List<String> requestedRiderIDs){

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.activity_event_page);
        TextView title = new TextView(this);
        title.setText("Requests:");
        layout.addView(title);

        // For each rider, add a link to the user's profile
        for (final String riderID : requestedRiderIDs){

            // With backend: get rider name from riderID

            // Create textfield that links to rider's profile
            final TextView newTextView = new TextView(this);
            newTextView.setText(riderID);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                public void onClick(View view){
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("RiderID", riderID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            };
            layout.addView(newTextView);

            // Create accept button
            ImageButton acceptButton = new ImageButton(this);
            acceptButton.setBackgroundResource(R.drawable.ic_check_black_24dp);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    // accept rider request
                }
            });
            layout.addView(acceptButton);

            // Create reject button
            ImageButton rejectButton = new ImageButton(this);
            rejectButton.setBackgroundResource(R.drawable.ic_close_black_24dp);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    // delete rider request
                }
            });
            layout.addView(rejectButton);
        }

    }
}
