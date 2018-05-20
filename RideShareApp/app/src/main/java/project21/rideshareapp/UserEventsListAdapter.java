package project21.rideshareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import backend.Event;
import backend.EventEntity;
import project21.rideshareapp.R;

/**
 * Created by Xie on 2016/12/4.
 */

public class UserEventsListAdapter extends BaseAdapter {
    private final Context mContext;
    private LayoutInflater mLayoutInflater;
//    private final EventEntity[] events;
//    private ArrayList<EventEntity> event = new ArrayList<EventEntity>();

    private ArrayList<Event> event = new ArrayList<Event>();
    public UserEventsListAdapter(Context context, ArrayList<Event> events) {
        this.event = events;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        Event eventDemo = new Event(3,"bob","Hack MIT","17 October 2016", null, "fun");
//        events.add(eventDemo);
    }




    @Override
    public int getCount() {
        return event.size();
    }

    @Override
    public Object getItem(int i) {
        return event.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        RelativeLayout itemView;
        if (convertView == null) {
            itemView = (RelativeLayout) mLayoutInflater.inflate(
                    R.layout.user_events_list, parent, false);
        } else {
            itemView = (RelativeLayout) convertView;
        }
        TextView eventNameText = (TextView)
                itemView.findViewById(R.id.event_name);
        TextView dateLocationText = (TextView)
                itemView.findViewById(R.id.event_date_location);
        TextView driverInfo = (TextView)
                itemView.findViewById(R.id.driver_info);
        TextView driverName = (TextView)
                itemView.findViewById(R.id.driver_name);
        TextView driverOrPassenger = (TextView)
                itemView.findViewById(R.id.driver_or_pass);

        eventNameText.setText(event.get(i).getEventname());
        dateLocationText.setText(event.get(i).getEventDatetime());
        driverName.setText(event.get(i).getPostContent());
        return itemView;
    }
    public void upDateEntries(ArrayList<Event> entries) {
        event = entries;
        notifyDataSetChanged();
    }

}
