package project21.rideshareapp;

/**
 * Created by Zack on 2016-12-04.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import backend.DriverEntity;

public class DriverListArrayAdapter extends ArrayAdapter<DriverEntity> {
    private final Context context;
    private final DriverEntity[] driverList;

    public DriverListArrayAdapter(Context context, DriverEntity[] driverList) {
        super(context, -1, driverList);
        this.context = context;
        this.driverList = driverList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.driver_result_list_item, parent, false);
        TextView driverTextView = (TextView) rowView.findViewById(R.id.driver_result_list_driver_name);
        TextView driverMessageTextView = (TextView) rowView.findViewById(R.id.driver_result_list_message);
        TextView driverCarCapacityTextView = (TextView) rowView.findViewById(R.id.driver_result_list_capacity);
        driverTextView.setText((String)("Username: " + (driverList[position]).get("userName")));
        driverCarCapacityTextView.setText("Capacity: " + String.valueOf((driverList[position]).get("numSeatsLeft")));
        driverMessageTextView.setText("Message: " + (String)(driverList[position]).get("postContent"));

        return rowView;
    }
}