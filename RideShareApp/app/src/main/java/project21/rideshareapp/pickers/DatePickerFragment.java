package project21.rideshareapp.pickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/*
    A DialogFragment that attaches a "DatePicker" to the appropriate EditText.

    NOTE:
    Code sourced from Google's official Android tutorials, with some modifications
    made to accommodate listeners.
 */

// Gets the date from a date picker, and sends it on callback to listeners
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DateListener listener;

    // Notifies listeners when a date is selected
    public interface DateListener {
        void onDateSelected(Calendar date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // -- Self (1 line)
        listener = (DateListener) getActivity();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Create a calendar for given date, and notify listeners of a converted string
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        if(listener != null) {
              listener.onDateSelected(c);
        }
    }
}