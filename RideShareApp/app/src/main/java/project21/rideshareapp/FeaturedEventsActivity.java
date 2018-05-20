package project21.rideshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;

import backend.EventEntity;
import backend.RiderEntity;

/*
    This activity represents a page in which we display a list of featured
    events. Not crucial for the final product but would be nice to include.
 */

public class FeaturedEventsActivity extends AppCompatActivity {

    public View v1;
    public TextView e1n;
    public TextView e1d;
    public TextView e1s;
    public View v2;
    public TextView e2n;
    public TextView e2d;
    public TextView e2s;
    public View v3;
    public TextView e3n;
    public TextView e3d;
    public TextView e3s;
    public Client myKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_events);

        myKinveyClient = Client.sharedInstance();

        // Make views invisible until we have got all the data
        v1 = findViewById(R.id.event1);
        e1n = (TextView) findViewById(R.id.event1_name);
        e1d = (TextView) findViewById(R.id.event1_date_location);
        e1s = (TextView) findViewById(R.id.event1_spots);
        v2 = findViewById(R.id.event2);
        e2n = (TextView) findViewById(R.id.event2_name);
        e2d = (TextView) findViewById(R.id.event2_date_location);
        e2s = (TextView) findViewById(R.id.event2_spots);
        v3 = findViewById(R.id.event3);
        e3n = (TextView) findViewById(R.id.event3_name);
        e3d = (TextView) findViewById(R.id.event3_date_location);
        e3s = (TextView) findViewById(R.id.event3_spots);
        v1.setVisibility(View.GONE);
        e1n.setVisibility(View.GONE);
        e1d.setVisibility(View.GONE);
        e1s.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
        e2n.setVisibility(View.GONE);
        e2d.setVisibility(View.GONE);
        e2s.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        e3n.setVisibility(View.GONE);
        e3d.setVisibility(View.GONE);
        e3s.setVisibility(View.GONE);

        // Pull 3 events from the database, should technically have some logic or strategy behind
        // it, but I'm just gonna take 3 random events, assuming they exist.

        // Okay, so this is a huge hack but whatever. I can't query the database unless a user is
        // logged in, so I log in a test user I have, and then log them out after the query is
        // finished.
        myKinveyClient.user().logout().execute();
        myKinveyClient.user().login("featuredEventTestUser", "featuredEventPassword", new KinveyUserCallback() {

            // This shouldn't ever happen
            @Override
            public void onFailure(Throwable t) {
                CharSequence text = "Error encountered on loading events";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            // Display the events
            @Override
            public void onSuccess(User u) {
                AsyncAppData<EventEntity> myEvents = myKinveyClient.appData("events", EventEntity.class);
                Query queryEvents = new Query().startsWith("eventName", "");
                myEvents.get(queryEvents, new KinveyListCallback<EventEntity>() {

                    @Override
                    public void onSuccess(EventEntity[] events) {
                        Log.d("TEST", Integer.toString(events.length));
                        showEvents(events);
                        // Finished displaying everything, log out the test user
                        myKinveyClient.user().logout().execute();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("TAG", "failed to find in riders", throwable);
                    }
                });
            }
        });
    }

    public void showEvents(EventEntity[] events){

        // If there are no events found, which should be pretty rare in our final product,
        // just give a standard user message

        if(events.length == 0){
            TextView desc = (TextView) findViewById(R.id.description);
            desc.setText("We couldn't find any events near you! :(");
        } else {
            // Format and show first event

            List<EventEntity> eventList = new ArrayList<>();
            for (int i = 0; i < events.length; i++)
            {
                eventList.add(events[i]);
            }
            Collections.shuffle(eventList);
            EventEntity curEvent = eventList.get(0);

            if(curEvent.getEventname().length() >= 17){
                e1n.setTextSize(24);
            }

            e1n.setText(curEvent.getEventname());
            e1d.setText(curEvent.getEventDatetime() + " -- " + curEvent.getEventTime());
            e1s.setText(curEvent.getEventAddress());
            v1.setVisibility(View.VISIBLE);
            e1n.setVisibility(View.VISIBLE);
            e1d.setVisibility(View.VISIBLE);
            e1s.setVisibility(View.VISIBLE);

            if(events.length > 1){
                // Format and show second event
                curEvent = eventList.get(1);

                if(curEvent.getEventname().length() >= 17){
                    e2n.setTextSize(24);
                }

                e2n.setText(curEvent.getEventname());
                e2d.setText(curEvent.getEventDatetime() + " -- " + curEvent.getEventTime());
                e2s.setText(curEvent.getEventAddress());
                v2.setVisibility(View.VISIBLE);
                e2n.setVisibility(View.VISIBLE);
                e2d.setVisibility(View.VISIBLE);
                e2s.setVisibility(View.VISIBLE);
            }

            if(events.length > 2){
                // Format and show third event
                curEvent = eventList.get(2);

                if(curEvent.getEventname().length() >= 17){
                    e3n.setTextSize(24);
                }

                e3n.setText(curEvent.getEventname());
                e3d.setText(curEvent.getEventDatetime() + " -- " + curEvent.getEventTime());
                e3s.setText(curEvent.getEventAddress());
                v3.setVisibility(View.VISIBLE);
                e3n.setVisibility(View.VISIBLE);
                e3d.setVisibility(View.VISIBLE);
                e3s.setVisibility(View.VISIBLE);
            }
        }
    }
}
