package mobile.and.web.iti.navigationdrawer.update.view.trips;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;
import mobile.and.web.iti.navigationdrawer.main.fragments.MainActivity;

public class update_trip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
        , TimePickerDialog.OnTimeSetListener  {
    TextView updateTripName ;
    TextView updateFrom ;
    TextView updateTo ;
    TextView updateDate ;
    TextView updateTime ;
    ImageView img ;
    Button btn_Date;
    Button btn_Time;
    Button btn_update;
    private int day, month, year, hour, minute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private String dateStr, timeStr , event_id;

    private Trip_DTO trip_dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        updateTripName = findViewById(R.id.updateTripName);
        updateFrom = findViewById(R.id.updateFrom);
        updateTo = findViewById(R.id.updateTo);
        updateDate = findViewById(R.id.updateDate);
        updateTime = findViewById(R.id.updateTime);
        btn_Date = findViewById(R.id.btn_Date);
        btn_Time = findViewById(R.id.btn_Time);
        img = findViewById(R.id.updateImage);
        btn_update = findViewById(R.id.btnUpdate);
        trip_dto =  new Trip_DTO();


        Intent intent = getIntent();
        event_id = intent.getStringExtra("id");

        trip_dto = getTripById(event_id);
        updateTripName.setText(intent.getStringExtra("name"));
        updateFrom.setText(intent.getStringExtra("from"));
        updateTo.setText(intent.getStringExtra("to"));
        updateDate.setText(intent.getStringExtra("date"));
        updateTime.setText(intent.getStringExtra("time"));
        if (getIntent().hasExtra("byteArray")) {
            if (getIntent().getByteArrayExtra("byteArray") != null){
                Bitmap _bitmap = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            img.setImageBitmap(_bitmap);
        }else{
                img.setImageResource(R.mipmap.image);
            }

        }
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDateValid()) {
                    trip_dto.setName(updateTripName.getText().toString());
                    trip_dto.setStart_point(updateFrom.getText().toString());
                    trip_dto.setEnd_point(updateTo.getText().toString());
                    if(dateStr != null)
                    trip_dto.setDateStr(dateStr);
                    if(timeStr != null)
                    trip_dto.setTimeStr(timeStr);
                    int result = updateCalendarEvent(trip_dto);
                    Toast.makeText(update_trip.this, "updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(update_trip.this , MainActivity.class));
                }else{
                    Toast.makeText(update_trip.this, "trip time and date should be in future ..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }







    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1;
        dayFinal = i2;

        dateStr = yearFinal + "-" + (monthFinal+1) + "-" + dayFinal;
        updateDate.setText(dateStr);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        timeStr = hourFinal + ":" + minuteFinal;
        updateTime.setText(timeStr);

    }
    public void pickDateU(View view) {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(update_trip.this, update_trip.this, year, month, day);
        datePickerDialog.show();
    }

    public void pickTimeU(View view){
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(update_trip.this, update_trip.this
                , hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    // update trip in calendar and in SQLite \\

    public int updateCalendarEvent(Trip_DTO trip_dto){
        int rows = -1;
        long retVal = updateTripSQL(trip_dto);
        if(retVal != -1) {
            SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd");
            long startMillis;
            Calendar begin = Calendar.getInstance();
            begin.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal); // get time from date and time picker dialogs
            startMillis = begin.getTimeInMillis();


            String dtUntill = form.format(begin.getTime());


            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            Uri updateUri = null;
            // The new title for the event
            values.put(CalendarContract.Events.TITLE, updateTripName.getText().toString()); // get event name from update activity
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DESCRIPTION, "Trip Status : upcoming");
            values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" + dtUntill);  // here we can set a repetition

            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.valueOf(trip_dto.getEventId()));
            rows = cr.update(updateUri, values, null, null);
            Log.i("update calendar", "Rows updated: " + rows);
        }else{
            Log.i("update calendar", "Np Rows updated: " + rows);
        }
        return rows;
    }



    public long updateTripSQL(Trip_DTO trip_dto){
        DatabaseAdapter adapter = new DatabaseAdapter(update_trip.this);
        long retVal = adapter.updateTrip(trip_dto);
        Log.i("update SQL", retVal+"");
//        Toast.makeText(update_trip.this, "retVal = "+retVal, Toast.LENGTH_SHORT).show();
        return retVal;
    }


    public Trip_DTO getTripById(String event_id){
        DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
        Trip_DTO trip = new Trip_DTO();
        trip = adapter.retrieveTripByEventId(event_id);
        if (trip.getEventId() != null) {
//            Toast.makeText(update_trip.this, "keep calm yan lolla :D", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(update_trip.this, "Something went wrong while retrieving", Toast.LENGTH_SHORT).show();
        }
        return trip;
    }


    public boolean isDateValid(){
        boolean retVal = false;
        // get Time in millies for the trip and for now ,then see if it's coming or previous
        long tripTime;
        Calendar begin = Calendar.getInstance();
        begin.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
        tripTime = begin.getTimeInMillis();


        // for now !!
        long nowTime;
        Calendar now = Calendar.getInstance();
        now.set(year, month, day, hour, minute);
        nowTime = now.getTimeInMillis();

        if(tripTime >= nowTime){
            retVal = true;
        }


        return retVal;
    }

}
