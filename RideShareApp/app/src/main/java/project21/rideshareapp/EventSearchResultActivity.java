package project21.rideshareapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import backend.EventEntity;

public class EventSearchResultActivity extends AppCompatActivity implements View.OnClickListener{
    Client mKinveyClient;
    EventEntity[] eventList;
    ListView listview;
    ProgressDialog waitDialog;
    TextView resultTextView;
    Button createEventBtn;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search_result);
        mKinveyClient = Client.sharedInstance();

        userName = getIntent().getStringExtra("userName");
        // Log.d("result userName", userName);
        resultTextView = (TextView)findViewById(R.id.result_text_view);
        createEventBtn = (Button)findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(this);
        String eventName = getIntent().getStringExtra("eventName");
        searchForEvent(eventName);
    }

    public void searchForEvent(String eventName){

        listview = (ListView) findViewById(R.id.search_result_listview);

        Query myQuery = mKinveyClient.query();
        myQuery.regEx("eventName", '^' + eventName);
        
        AsyncAppData<EventEntity> myEvents = mKinveyClient.appData("events", EventEntity.class);
        waitDialog = ProgressDialog.show(this, "", "Please wait", true, false);
        myEvents.get(myQuery, new KinveyListCallback<EventEntity>() {
            @Override
            public void onSuccess(EventEntity[] results) {
                waitDialog.cancel();
                eventList = results;
                Log.v("TAG", "received "+ results.length + " events");
                if (results.length == 0){
                    resultTextView.setText("No Result");
                }else{
                    resultTextView.setText("Search Result");
                }
                final EventListArrayAdapter adapter = new EventListArrayAdapter(getApplicationContext(),
                        eventList);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        EventEntity event = eventList[position];
                        final String event_id = (String)event.get("_id");
                        Log.e("onItemClick","position" + position + "clicked!");
                        Log.e("clicked event_id", event_id);
                        // Should direct to eventPageActivity and populate this page with all the data
                        Intent intent = new Intent(getApplicationContext(), EventDisplayActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("event", event);
                        startActivity(intent);
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
    public void onClick(View view) {
        if (view == createEventBtn){
            Intent intent = new Intent(getApplicationContext(), EventCreateActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
    }
}
