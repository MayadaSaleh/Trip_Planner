package mobile.and.web.iti.navigationdrawer.dialoge.alert;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import mobile.and.web.iti.navigationdrawer.maps.FloatingViewService;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;

public class DialogActivity extends AppCompatActivity {

    NotificationCompat.Builder mBuilder;
    private Trip_DTO trip_dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        this.setFinishOnTouchOutside(false);
        receiver();
    }


    public void notificationInitializing(String name , String myId){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        intent.putExtra("key" , "ok");
        intent.putExtra("name" , name);
        intent.putExtra("id" , myId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.valueOf(myId), intent, 0);

        mBuilder = new NotificationCompat.Builder(this , name)
                .setSmallIcon(R.drawable.m)
                .setContentTitle(name)
                .setContentText("be ready for trip !!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).setOngoing(true);
    }

    public void createAlertDialog(final String eventName , final String eventId , final Intent intent){
        Log.i("Alert" , "Abl Initialize");
        String name = intent.getStringExtra("Name");
        if(name != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);
            builder.setCancelable(false)
                    .setTitle(eventName)
                    .setMessage("Your Trip To "+ trip_dto.getEnd_point() + " will start after 30 min , do you want to start now?")
                    .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            stopService(new Intent(DialogActivity.this, AlarmService.class));
                            long retVal = updateStatus("done" , eventId);
                            Log.i("update retval done" , retVal+"");
//                            Toast.makeText(DialogActivity.this, "yalla bena 3al map yala", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(DialogActivity.this,FloatingViewService.class);
                            in.putExtra("id",eventId);
                            startService(in);

                            //route from my current position to destination point
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+intent.getStringExtra("End_Latitude")+","+intent.getStringExtra("End_Longitude"));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try
                            {
                                startActivity(mapIntent);
                                finish();

                            }
                            catch(ActivityNotFoundException ex)
                            {
                                try
                                {
                                    Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    startActivity(unrestrictedIntent);
                                    finish();
                                }
                                catch(ActivityNotFoundException innerEx)
                                {
                                    Toast.makeText(DialogActivity.this, "Please install Google maps application", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    })
                    .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            builder.setCancelable(false);
                            notificationInitializing(eventName, eventId);
                            // this means that we have this trip in our application then we should fire a notification .. \\
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify(Integer.valueOf(eventId), mBuilder.build());
                            stopService(new Intent(DialogActivity.this, AlarmService.class));
                            //\\
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            builder.setCancelable(true);
                            long retval = updateStatus("cancelled" , eventId);
//                            Toast.makeText(DialogActivity.this, retval+"", Toast.LENGTH_SHORT).show();
                            stopService(new Intent(DialogActivity.this, AlarmService.class));
                            finish();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }else{
            // here I should get data from shared preferences where I saved in receiver()
            String key = intent.getStringExtra("key");
            Log.i("key" , key);
            if(key.equals("ok")) {
                final String eId = intent.getStringExtra("id");
                final String eName = intent.getStringExtra("name");
                Log.i("id" , eId);
                final Trip_DTO myTrip = getTripById(eId);
                if(myTrip != null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);
                    builder.setCancelable(false)
                            .setTitle(myTrip.getName())
                            .setMessage("Your Trip To " + myTrip.getEnd_point() + " will start after 30 min , do you want to start now?")
                            .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    Toast.makeText(DialogActivity.this, "yalla bena 3al map yala", Toast.LENGTH_SHORT).show();
                                    long retVal = updateStatus("done" , eId);
//                                    Log.i("update retval done" , retVal+"");

                                    Intent in = new Intent(DialogActivity.this,FloatingViewService.class);
                                    in.putExtra("id",eId);
                                    startService(in);
                                    //route from my current position to destination point
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+myTrip.getEndLatitude()+","+myTrip.getEndLongitude());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    try
                                    {
                                        startActivity(mapIntent);
                                        finish();

                                    }
                                    catch(ActivityNotFoundException ex)
                                    {
                                        try
                                        {
                                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            startActivity(unrestrictedIntent);
                                            finish();
                                        }
                                        catch(ActivityNotFoundException innerEx)
                                        {
                                            Toast.makeText(DialogActivity.this, "Please install Google maps application", Toast.LENGTH_LONG).show();
                                        }
                                    }


                                }
                            })
                            .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    builder.setCancelable(false);
                                    notificationInitializing(eName, eId);
                                    // this means that we have this trip in our application then we should fire a notification .. \\
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                    // notificationId is a unique int for each notification that you must define
                                    notificationManager.notify(Integer.valueOf(eId), mBuilder.build());
                                    //\\
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    builder.setCancelable(true);
                                    long retVal = updateStatus("cancelled" , eId);
//                                    Toast.makeText(DialogActivity.this, retVal+"", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                }
            }
        }
    }



    public void receiver(){
        Intent myIntent = getIntent();
        if(myIntent != null) {
            String id_broadcast =  myIntent.getStringExtra("Event_ID");
            Log.i("Calendar" , "Before check null");
            if(id_broadcast != null) {
                startService(new Intent(DialogActivity.this, AlarmService.class));
                trip_dto = new Trip_DTO();
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                trip_dto.setName(myIntent.getStringExtra("Name"));
                trip_dto.setStart_point(myIntent.getStringExtra("Start_Point"));
                trip_dto.setEnd_point(myIntent.getStringExtra("End_Point"));
                createAlertDialog(myIntent.getStringExtra("Name") , id_broadcast , myIntent);
                trip_dto.setNotes(myIntent.getStringExtra("Notes"));
                trip_dto.setDateStr(myIntent.getStringExtra("Date"));
                trip_dto.setTimeStr(myIntent.getStringExtra("Time"));
                trip_dto.setTripStatus(myIntent.getStringExtra("Status"));
                trip_dto.setTripType(myIntent.getStringExtra("Type"));
                trip_dto.setStartLatitude(myIntent.getStringExtra("Start_Latitude"));
                trip_dto.setStartLongitude(myIntent.getStringExtra("Start_Longitude"));
                trip_dto.setEndLatitude(myIntent.getStringExtra("End_Latitude"));
                trip_dto.setEndLongitude(myIntent.getStringExtra("End_Longitude"));
                trip_dto.setEventId(myIntent.getStringExtra("Event_ID"));

                // save in sharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(trip_dto.getName() , MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("dEventId" , trip_dto.getEventId());
                editor.apply();

                // Logs
                Log.i("Trip Name",trip_dto.getName()+"");
                Log.i("Trip Start_Point",trip_dto.getStart_point()+"");
                Log.i("Trip End_Point",trip_dto.getEnd_point()+"");
                Log.i("Trip Notes",trip_dto.getNotes()+"");
                Log.i("Trip Date",trip_dto.getDateStr()+"");
                Log.i("Trip Time",trip_dto.getTimeStr()+"");
                Log.i("Trip Status",trip_dto.getTripStatus()+"");
                Log.i("Trip Type",trip_dto.getTripType()+"");
                Log.i("Trip Start_Lat",trip_dto.getStartLatitude()+"");
                Log.i("Trip Start Lng",trip_dto.getStartLongitude()+"");
                Log.i("Trip End_Lat",trip_dto.getEndLatitude()+"");
                Log.i("Trip End_Lng",trip_dto.getEndLongitude()+"");
                Log.i("Trip Event ID",trip_dto.getEventId()+"");
            }else{
                createAlertDialog("Domy Name" , "123" , myIntent);
            }
        }else{
            Toast.makeText(DialogActivity.this, "Wrong Broadcast Receiver", Toast.LENGTH_SHORT).show();
        }
    }


    public Trip_DTO getTripById(String event_id){
        DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
        Trip_DTO trip;
        trip = adapter.retrieveTripByEventId(event_id);
        if (trip.getEventId() != null) {
//            Toast.makeText(this, "Start Lat = " +trip.getStartLatitude() +"\nStart Lng = "+trip.getStartLongitude(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Event ID = " + trip.getEventId(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Retrieved..", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Something went wrong while retrieving data..", Toast.LENGTH_SHORT).show();
        }
        return trip;
    }


    public long updateStatus(String status , String event_id){
        DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
        return  adapter.updateTripStatus(status , event_id);
    }


}
