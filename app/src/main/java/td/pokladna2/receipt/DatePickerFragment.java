package td.pokladna2.receipt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;
import td.pokladna2.receipt.EmployeeEetManage;
import td.pokladna2.reporting.DatePickerInterface;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DatePickerInterface activity;

    public DatePickerFragment(DatePickerInterface activity) {
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.d("EET","Picked date" + year + "/" + month + "/" + day);
        activity.updateTable(year, month, day);

    }
}
