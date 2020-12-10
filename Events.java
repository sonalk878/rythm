package edu.gmu.cs477.fall2020.rythm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Events extends AppCompatActivity {

    EditText eventName;    //declaring variables
    EditText date;
    String name = "";
    String dateInput = "";
    String yr = "";
    String mth = "";
    String day = "";
    int y = 0;
    int d = 0;
    int m = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        eventName = findViewById(R.id.eventName);
        date = findViewById(R.id.dateBox);
    }

    public void AddEvent(View view) { // adding event onclick method

        name = eventName.getText().toString();
        dateInput = date.getText().toString();

        if(dateInput.length() != 8 || dateInput.contains(".") || dateInput.contains("-") || dateInput.contains("/"))
        {
            Toast.makeText(this, "Please Enter Valid Date", Toast.LENGTH_SHORT).show();
        }

        else {
            mth = dateInput.substring(0, 2);        //parses date input into integers
            day = dateInput.substring(2, 4);
            yr = dateInput.substring(4);

            d = Integer.parseInt(day);
            m = Integer.parseInt(mth);
            y = Integer.parseInt(yr);
            Toast.makeText(this, Integer.toString(m), Toast.LENGTH_SHORT).show();

            if (m > 12) {                                                                            //a WHOLE bunch of checks to make sure a valid date was entered
                Toast.makeText(this, "Month is Invalid", Toast.LENGTH_SHORT).show();
            } else if (y < 2020) {
                Toast.makeText(this, "Year is Invalid", Toast.LENGTH_SHORT).show();
            } else if (d > 30 && (m != 1 && m != 3 && m != 5 && m != 5 && m != 7 && m != 8 && m != 10 && m != 12)) {
                Toast.makeText(this, "Day is Invalid", Toast.LENGTH_SHORT).show();
            } else if (d > 28 && m == 2) {
                Toast.makeText(this, "Day is Invalid", Toast.LENGTH_SHORT).show();
            } else if (d < 1 || d > 31) {
                Toast.makeText(this, "Day is Invalid", Toast.LENGTH_SHORT).show();
            } else {
                if (!name.isEmpty()) {
                    Date date = new GregorianCalendar(y, m -1, d).getTime(); // creating date and calendar event to send to google calendar

                    Calendar calendarEvent = Calendar.getInstance();
                    calendarEvent.setTime(date);
                    calendarEvent.set(Calendar.HOUR, 3);
                    Intent i = new Intent(Intent.ACTION_EDIT);
                    i.setType("vnd.android.cursor.item/event");
                    i.putExtra("beginTime", calendarEvent.getTimeInMillis());
                    i.putExtra("allDay", true);
                    i.putExtra("rule", "FREQ=YEARLY");
                    i.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
                    i.putExtra("title", name);
                    finish();
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Please Enter Event Name", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
