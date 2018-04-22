package mobile.and.web.iti.navigationdrawer.dialoge.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;


/**
 * Created by Hesham on 2/23/2018.
 */

public class MyReceiver extends BroadcastReceiver{

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(CalendarContract.ACTION_EVENT_REMINDER)) {
                //Do Something Here to get EVENT ID
                Uri uri = intent.getData();

                String alertTime = uri.getLastPathSegment();
                String selection = CalendarContract.CalendarAlerts.ALARM_TIME + "=?";
                Cursor cursor =
                        context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE ,
                        new String[]{CalendarContract.CalendarAlerts.TITLE} , selection , new String[]{alertTime} , null);
                String title = "";
                while(cursor.moveToNext()) {
                  Log.i("Cursor String" , cursor.getString(0));
                  title = cursor.getString(0);
                }

                Cursor cursor2 =
                        context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE ,
                                new String[]{CalendarContract.CalendarAlerts.EVENT_ID} , selection , new String[]{alertTime} , null);
                long event_id = -1;
                while(cursor2.moveToNext()) {
                    Log.i("Cursor Long" , String.valueOf(cursor2.getLong(0)));
                    event_id = cursor2.getLong(0);
                }

                getTripWithEventId( event_id , context);
            }
        }


        private Trip_DTO getTripWithEventId(long event_id , Context context){
            DatabaseAdapter adapter = new DatabaseAdapter(context);
            Trip_DTO trip;
            trip = adapter.retrieveTripByEventId(String.valueOf(event_id));

            if (trip.getEventId() != null) {
                Log.i("Trip Existance" , "true");
                Log.i("Trip Id" , trip.getEventId()+"");
                Log.i("Trip Name" , trip.getName()+"");
                Intent myIntent = new Intent(context, DialogActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("Name", trip.getName()+"");
                myIntent.putExtra("Start_Point", trip.getStart_point()+"");
                myIntent.putExtra("End_Point", trip.getEnd_point()+"");
                myIntent.putExtra("Notes", trip.getNotes()+"");
                myIntent.putExtra("Date", trip.getDateStr()+"");
                myIntent.putExtra("Time", trip.getTimeStr()+"");
                myIntent.putExtra("Status", trip.getTripStatus()+"");
                myIntent.putExtra("Type", trip.getTripType()+"");
                myIntent.putExtra("Start_Latitude", trip.getStartLatitude()+"");
                myIntent.putExtra("Start_Longitude", trip.getStartLongitude()+"");
                myIntent.putExtra("End_Latitude", trip.getEndLatitude()+"");
                myIntent.putExtra("End_Longitude", trip.getEndLongitude()+"");
                myIntent.putExtra("Event_ID", trip.getEventId()+"");
                context.startActivity(myIntent);
            } else {
                Log.i("Trip Existance" , "false");
            }
            return trip;
        }


}




