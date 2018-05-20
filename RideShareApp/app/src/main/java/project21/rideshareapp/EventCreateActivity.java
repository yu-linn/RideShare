package project21.rideshareapp;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.AppData;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import backend.EventEntity;
import project21.rideshareapp.pickers.DatePickerFragment;
import project21.rideshareapp.pickers.TimePickerFragment;

/*

This activity represents the page which allows a user
to create an event

 */

public class EventCreateActivity extends AppCompatActivity implements DatePickerFragment.DateListener, TimePickerFragment.TimeListener, View.OnClickListener {

    private EditText eventNameText;
    private EditText eventLocationStreetText;
    private EditText eventLocationCityText;
    private EditText eventLocationStateText;
    private EditText eventLocationCountryText;
    private EditText eventDateText;
    private EditText eventTimeText;
    private EditText eventDescriptionText;
    private Button createEventBtn;
    private Calendar eventDate;
    private Calendar eventTime;

    private Client mKinveyClient;


    private String formattedAddress;
    private String completeAddress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        mKinveyClient = Client.sharedInstance();

        // Gather XML components
        eventNameText = (EditText) findViewById((R.id.eventName));
        eventLocationStreetText = (EditText) findViewById(R.id.event_street_edit_text);
        eventLocationCityText = (EditText) findViewById(R.id.event_city_edit_text);
        eventLocationStateText = (EditText) findViewById(R.id.event_state_edit_text);
        eventLocationCountryText = (EditText) findViewById(R.id.event_country_edit_text);
        eventDateText = (EditText) findViewById(R.id.eventDate);
        eventTimeText = (EditText) findViewById(R.id.eventTime);
        eventDescriptionText = (EditText) findViewById(R.id.event_description_edit_text);
        createEventBtn = (Button) findViewById(R.id.event_create_btn);

        createEventBtn.setOnClickListener(this);

        // Event date opens calendar on click
        eventDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showDatePickerDialog(v);

            }
        });

        // Event time opens clock on click
        eventTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showTimePickerDialog(v);
            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onClick(View v){
        String eventname = eventNameText.getText().toString();

        // check if same event already exists
        Query query = mKinveyClient.query();
        query.equals("eventName", eventname);
        AsyncAppData<EventEntity> myData = mKinveyClient.appData("events", EventEntity.class);
        myData.get(query, new KinveyListCallback<EventEntity>() {
            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "Error fetching data: " + t.getMessage());
                return;
            }
            @Override
            public void onSuccess(EventEntity[] eventEntities) {
                // if event does not exist.
                if (eventEntities.length == 0) {
                    String addressStreet = eventLocationStreetText.getText().toString();
                    String addressCity = eventLocationCityText.getText().toString();
                    String addressState = eventLocationStateText.getText().toString();
                    String addressCountry = eventLocationCountryText.getText().toString();
                    String description = eventDescriptionText.getText().toString();

                    formattedAddress = addressStreet.replaceAll(" ", "+") + ",+";
                    formattedAddress += addressCity.replaceAll(" ", "+") + ",+";
                    formattedAddress += addressState.replaceAll(" ", "+") + ",+";
                    formattedAddress += addressCountry.replaceAll(" ", "+");

                    completeAddress = addressStreet + ", " + addressCity + ", " + addressState + ", " + addressCountry;

//                // logging is temporary
//                Log.d("CreateEvent", "Name:        | " + eventname);
//                Log.d("CreateEvent", "eventDate:   | " + this.eventDate.getTime());
//                Log.d("CreateEvent", "eventTime:   | " + this.eventTime.getTime());
//                Log.d("CreateEvent", "Location:    | " + completeAddress);
//                Log.d("CreateEvent", "Description: | " + description);

                    new saveEventToDbAsyncTask().execute();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EventCreateActivity.this, R.style.myDialog));
                    builder.setMessage("This event already exists");
                    // Add the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /**
     * Listener for changes in the selected event date.
     *
     * @param date The calendar object based on the selected date.
     */
    @Override
    public void onDateSelected(Calendar date){
        this.eventDate = date;
        eventDateText.setText(this.formatCalendar(date, "yyyy / MM / dd"));
    }

    /**
     * Listener for changes in the selected event time.
     *
     * @param time The calendar object based on the selected time.
     */
    @Override
    public void onTimeSelected(Calendar time){
        this.eventTime = time;
        eventTimeText.setText(this.formatCalendar(time, "HH : mm"));
    }

    /**
     * Returns a formatted string from a calendar c, using format.
     *
     * @return A formatted string representation of the given calendar.
     */
    private String formatCalendar(Calendar c, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(c.getTime());
    }

    private class saveEventToDbAsyncTask extends AsyncTask<Void, Void, String[]> {
        // Operates on a different thread, all the variables don't share with
        // the fragment.
        ProgressDialog dialog;
        //Client mKinveyClient = Client.sharedInstance();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(EventCreateActivity.this, "", "Please wait", true, false);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=" + formattedAddress + "&sensor=false");
                Log.d("response","" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);


                ArrayList<Double> temp = new ArrayList<Double>();
                temp.add(lng);
                temp.add(lat);
                double[] geoloc = new double[]{lng, lat};

                List<String> admins = new ArrayList<String>();
                admins.add(mKinveyClient.user().getId());

                // Adding data into an event entity
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                final EventEntity event = new EventEntity();
                event.put("eventAdmins", admins);
                event.put("eventName", eventNameText.getText().toString());
                event.put("eventDate", eventDateText.getText().toString());
                event.put("eventTime", eventTimeText.getText().toString());
                event.put("eventAddress", completeAddress);
                event.put("eventGeoloc", geoloc);
                event.put("postContent", eventDescriptionText.getText().toString());
                Calendar cal = Calendar.getInstance();
                event.put("date_created", sdf.format(cal.getTime()));

                // Save data to the database
                AsyncAppData<EventEntity> myevents = mKinveyClient.appData("events", EventEntity.class);
                myevents.save(event, new KinveyClientCallback<EventEntity>() {
                    @Override
                    public void onFailure(Throwable e) {
                        Log.e("TAG", "failed to save event data", e);
                    }
                    @Override
                    public void onSuccess(EventEntity r) {
                        Log.d("TAG", "saved data for event entity ");
                        // go to event page
                        //
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        //intent.putExtra("event", event);
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}


