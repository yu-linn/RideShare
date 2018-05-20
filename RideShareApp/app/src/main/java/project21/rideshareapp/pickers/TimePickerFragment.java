package project21.rideshareapp.pickers;

import android.app.DialogFragment;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/*
    A DialogFragment that attaches a "TimePicker" to the appropriate EditText.

    NOTE:
    Code sourced from Google's official Android tutorials, with some modifications
    made to accommodate listeners.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TimeListener listener;

    // Notifies listeners when a date is selected
    public interface TimeListener {
        void onTimeSelected(Calendar time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        listener = (TimeListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Save the entered date
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        // Notify listeners
        if(listener != null) {
            listener.onTimeSelected(c);
        }

    }
}