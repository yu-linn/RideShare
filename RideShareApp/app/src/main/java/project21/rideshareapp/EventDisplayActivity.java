package project21.rideshareapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.java.AppData;
import com.kinvey.java.Query;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import backend.EventEntity;

public class EventDisplayActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView eventNameText;
    private TextView eventLocationStreetText;
    private TextView eventLocationCityText;
    private TextView eventLocationStateText;
    private TextView eventLocationCountryText;
    private TextView eventDescriptionText;
    private TextView eventDateText;
    private TextView eventTimeText;
    private TextView dateCreatedText;

    private Button ridersBtn;
    private Button driversBtn;
    private ImageButton homeBtn;

    private Client mKinveyClient;

    private Map<String, String> event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        mKinveyClient = Client.sharedInstance();

        event = (HashMap<String, String>) getIntent().getSerializableExtra("event");

        eventNameText = (TextView) findViewById((R.id.eventName));
        eventLocationStreetText = (TextView) findViewById(R.id.eventStreet);
        eventLocationCityText = (TextView) findViewById(R.id.eventCity);
        eventLocationStateText = (TextView) findViewById(R.id.eventState);
        eventLocationCountryText = (TextView) findViewById(R.id.eventCountry);
        eventDateText = (TextView) findViewById(R.id.eventDate);
        eventTimeText = (TextView) findViewById(R.id.eventTime);
        eventDescriptionText = (TextView) findViewById(R.id.eventDescription);
        dateCreatedText = (TextView) findViewById(R.id.dateCreated);
        ridersBtn = (Button) findViewById(R.id.ridersBtn);
        driversBtn = (Button) findViewById(R.id.driversBtn);
        homeBtn = (ImageButton) findViewById(R.id.goBackHome);
        ridersBtn.setOnClickListener(this);
        driversBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        displayEvent();
    }

    private void displayEvent() {
        if(event.get("eventName").length() >= 19){
            eventNameText.setTextSize(30);
        }
        eventNameText.setText((String) event.get("eventName"));
        String[] address = ((String) event.get("eventAddress")).split(",");
        eventLocationStreetText.setText(address[0]);
        eventLocationCityText.setText(address[1]);
        eventLocationStateText.setText(address[2]);
        eventLocationCountryText.setText(address[3]);
        eventDescriptionText.setText((String) event.get("postContent"));
        eventDateText.setText((String) event.get("eventDate"));
        eventTimeText.setText((String) event.get("eventTime"));
        dateCreatedText.setText((String) event.get("dateCreated"));
//        Query myQuery = mKinveyClient.query();
//        myQuery.equals("eventName",eventName);
//        AppData<EventEntity> myEvents = mKinveyClient.appData("events", EventEntity.class);
//        try{
//            EventEntity[] results = myEvents.getBlocking(myQuery).execute();
//            if (results.length > 0) {
//                EventEntity event = results[0];
//                eventNameText.setText((String) event.get("eventName"));
//                String[] address = ((String) event.get("eventAddress")).split(",");
//                eventLocationStreetText.setText(address[0]);
//                eventLocationCityText.setText(address[1]);
//                eventLocationStateText.setText(address[2]);
//                eventLocationCountryText.setText(address[3]);
//                eventDescriptionText.setText((String) event.get("postContent"));
//                eventDateText.setText((String) event.get("eventDate"));
//                eventTimeText.setText((String) event.get("eventTime"));
//                dateCreatedText.setText((String) event.get("dateCreated"));
//            }
//        }catch (IOException e){
//            System.out.println("Couldn't get! -> " + e);
//            // return to homepage.
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public void onClick(View view) {
        if (view == driversBtn){
            Intent intent = new Intent(getApplicationContext(), RegisterDriverActivity.class);
            intent.putExtra("eventName", event.get("eventName"));
            intent.putExtra("eventId", event.get("_id"));
            startActivity(intent);
        }else if(view == ridersBtn){
            Intent intent = new Intent(getApplicationContext(), RiderSearchResultActivity.class);
            intent.putExtra("eventId", event.get("_id"));
            intent.putExtra("eventName", event.get("eventName"));
            startActivity(intent);
        }else if(view == homeBtn){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }
}
