package project21.rideshareapp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import backend.EventEntity;

/**
 * Created by Hongman Cho on 2016-11-29.
 */

public class EventListArrayAdapter extends ArrayAdapter<EventEntity> {
    private final Context context;
    private final EventEntity[] events;
    //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");

    public EventListArrayAdapter(Context context, EventEntity[] events) {
        super(context, -1, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.event_result_list_item, parent, false);
        TextView eventTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.secondLineDate);
        TextView locationTextView = (TextView) rowView.findViewById(R.id.secondLineLocation);
        eventTextView.setText((String)(events[position]).get("eventName"));
        //dateTextView.setText(sdf.format((events[position]).get("eventDate")));
        dateTextView.setText((String)events[position].get("eventDate"));
        String eventAddress = (String)(events[position]).get("eventAddress");
        if (eventAddress.length() > 15){
            eventAddress = eventAddress.substring(0, 15);
            eventAddress += "...";
        }
        locationTextView.setText(eventAddress);
        return rowView;
    }
}
