package project21.rideshareapp;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import backend.DriverEntity;
import backend.EventEntity;
import project21.rideshareapp.pickers.DatePickerFragment;
import project21.rideshareapp.pickers.TimePickerFragment;

public class RegisterDriverActivity extends AppCompatActivity implements DatePickerFragment.DateListener, TimePickerFragment.TimeListener, View.OnClickListener{

    private TextView eventNameTextView;
    private EditText availableSeatsEditText;
    private EditText meetingAddressText;
    private EditText meetingDateText;
    private EditText meetingTimeText;
    private EditText driverMessageEditText;
    private EditText feePerRider;
    private Button registerDriverBtn;
    private Client mKinveyClient;
    private String eventId;
    private String eventName;
    private String userName;
    private Calendar eventDate;
    private Calendar eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        mKinveyClient = Client.sharedInstance();

        userName = mKinveyClient.user().getUsername();

        eventNameTextView = (TextView) findViewById(R.id.eventText);
        availableSeatsEditText = (EditText) findViewById(R.id.seatsText);
        driverMessageEditText = (EditText) findViewById(R.id.messageText);
        meetingAddressText = (EditText) findViewById(R.id.addressText);
        meetingDateText = (EditText) findViewById(R.id.meetingDateText);
        meetingTimeText = (EditText) findViewById(R.id.meetingTimeText);
        feePerRider = (EditText) findViewById(R.id.feeText);

        registerDriverBtn = (Button) findViewById(R.id.submitButton);
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        eventNameTextView.setText(eventName);
        Log.d("event id", eventId);
        Log.d("register userName", userName);

        registerDriverBtn.setOnClickListener(this);

        // Event date opens calendar on click
        meetingDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showDatePickerDialog(v);

            }
        });

        // Event time opens clock on click
        meetingTimeText.setOnClickListener(new View.OnClickListener() {
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
    public void onDateSelected(Calendar date){
        this.eventDate = date;
        meetingDateText.setText(this.formatCalendar(date, "yyyy / MM / dd"));
    }

    @Override
    public void onTimeSelected(Calendar time){
        this.eventTime = time;
        meetingTimeText.setText(this.formatCalendar(time, "HH : mm"));
    }

    private String formatCalendar(Calendar c, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(c.getTime());
    }

    @Override
    public void onClick(View view) {
        if (view == registerDriverBtn){
            // check if user is already posted as a driver for this event.
            Query q1 = mKinveyClient.query().equals("userName", userName);
            Query q2 = mKinveyClient.query().equals("eventName", eventName);
            Query query = q1.and(q2);
            AsyncAppData<DriverEntity> myData = mKinveyClient.appData("drivers", DriverEntity.class);
            myData.get(query, new KinveyListCallback<DriverEntity>() {
                @Override
                public void onFailure(Throwable t) {
                    Log.d("TAG", "Error fetching data: " + t.getMessage());
                    return;
                }

                @Override
                public void onSuccess(DriverEntity[] driverEntities) {
                    if (driverEntities.length > 0) {
                        // TODO: user already posted as driver. go to display driver activity.
                        Toast.makeText(RegisterDriverActivity.this, "You have already posted as a driver!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        DriverEntity driverPost = new DriverEntity();
                        driverPost.put("userName", mKinveyClient.user().getUsername());
                        driverPost.put("eventName", eventName);
                        driverPost.put("meetingAddress", meetingAddressText.getText().toString());
                        driverPost.put("eventDate", meetingDateText.getText().toString());
                        driverPost.put("eventTime", meetingTimeText.getText().toString());
                        driverPost.put("feePerRider", Float.valueOf(feePerRider.getText().toString()));
                        driverPost.put("numSeatsLeft", Integer.parseInt(availableSeatsEditText.getText().toString()));
                        driverPost.put("eventId", eventId);
                        driverPost.put("postContent", driverMessageEditText.getText().toString());

                        AsyncAppData<DriverEntity> carOffers = mKinveyClient.appData("drivers", DriverEntity.class);

                        carOffers.save(driverPost, new KinveyClientCallback<DriverEntity>() {
                            @Override
                            public void onSuccess(DriverEntity driverEntity) {
                                Log.e("TAG", "data saved successfully.");
                                Toast.makeText(RegisterDriverActivity.this, "Success!",
                                        Toast.LENGTH_LONG).show();
                                // TODO: go to driver search acitivty
                                // go to home page for now
                                goToHomePage();
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Log.e("TAG", "failed to save event data", e);
                            }

                        });
                    }
                }
            });
        }
    }

    private void goToHomePage(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

}
