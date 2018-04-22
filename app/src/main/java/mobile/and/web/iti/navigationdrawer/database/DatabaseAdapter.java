package mobile.and.web.iti.navigationdrawer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;

/**
 * Created by Hesham on 2/20/2018.
 */

public class DatabaseAdapter {

    private Context context;
    private Trip_Table trip;

    public DatabaseAdapter(Context context){
        trip = new Trip_Table(context);
        this.context = context;
    }

                        // trip table DDL \\
    public long insertTrip(Trip_DTO trip_dto){
        SQLiteDatabase db = trip.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip_Table.KEY_VAL1, trip_dto.getName());
        contentValues.put(Trip_Table.KEY_VAL2, trip_dto.getStart_point());
        contentValues.put(Trip_Table.KEY_VAL3, trip_dto.getEnd_point());
        contentValues.put(Trip_Table.KEY_VAL4, trip_dto.getNotes()); // trip_dto.getNotes()
        contentValues.put(Trip_Table.KEY_VAL5, trip_dto.getDateStr()); // trip_dto.getDate()
        contentValues.put(Trip_Table.KEY_VAL6, trip_dto.getTimeStr()); // trip_dto.getTime()
        contentValues.put(Trip_Table.KEY_VAL7, trip_dto.getTripStatus());
        contentValues.put(Trip_Table.KEY_VAL8, trip_dto.getTripType());
        contentValues.put(Trip_Table.KEY_VAL9, trip_dto.getStartLatitude());
        contentValues.put(Trip_Table.KEY_VAL10, trip_dto.getStartLongitude());
        contentValues.put(Trip_Table.KEY_VAL11, trip_dto.getEndLatitude());
        contentValues.put(Trip_Table.KEY_VAL12, trip_dto.getEndLongitude());
        contentValues.put(Trip_Table.KEY_VAL13, trip_dto.getEventId());
        contentValues.put(Trip_Table.KEY_VAL14, trip_dto.getUser_id());

        long retval = db.insert(Trip_Table.TABLE_NAME , null , contentValues);
        return retval;
    }

    public Trip_DTO retrieveTrip(){
        Cursor c;
        Trip_DTO trip_dto = new Trip_DTO();
        SQLiteDatabase db = trip.getReadableDatabase();
        String[] columns = {Trip_Table.KEY_VAL1, Trip_Table.KEY_VAL2 , Trip_Table.KEY_VAL3 , Trip_Table.KEY_VAL4 , Trip_Table.KEY_VAL5 , Trip_Table.KEY_VAL6 , Trip_Table.KEY_VAL7
                , Trip_Table.KEY_VAL8 , Trip_Table.KEY_VAL9 , Trip_Table.KEY_VAL10 , Trip_Table.KEY_VAL11 , Trip_Table.KEY_VAL12 , Trip_Table.KEY_VAL13};
        c = db.query(Trip_Table.TABLE_NAME , columns , null , null , null , null , null);
        while(c.moveToNext()) {
            trip_dto.setName(c.getString(0));
            trip_dto.setStart_point(c.getString(1));
            trip_dto.setEnd_point(c.getString(2));
            trip_dto.setNotes(c.getString(3));
            trip_dto.setDateStr(c.getString(4));
            trip_dto.setTimeStr(c.getString(5));
            trip_dto.setTripStatus(c.getString(6));
            trip_dto.setTripType(c.getString(7));
            trip_dto.setStartLatitude(c.getString(8));
            trip_dto.setStartLongitude(c.getString(9));
            trip_dto.setEndLatitude(c.getString(10));
            trip_dto.setEndLongitude(c.getString(11));
            trip_dto.setEventId(c.getString(12));
        }
        return trip_dto;
    }


    public Trip_DTO retrieveTripByEventId(String eventId){
        Cursor c;
        Trip_DTO trip_dto = new Trip_DTO();
        SQLiteDatabase db = trip.getReadableDatabase();
        String selection = Trip_Table.KEY_VAL13 + "=?";
        String[] columns = {Trip_Table.KEY_VAL1, Trip_Table.KEY_VAL2 , Trip_Table.KEY_VAL3 , Trip_Table.KEY_VAL4 , Trip_Table.KEY_VAL5 , Trip_Table.KEY_VAL6 , Trip_Table.KEY_VAL7
                , Trip_Table.KEY_VAL8 , Trip_Table.KEY_VAL9 , Trip_Table.KEY_VAL10 , Trip_Table.KEY_VAL11 , Trip_Table.KEY_VAL12 , Trip_Table.KEY_VAL13};
        c = db.query(Trip_Table.TABLE_NAME , columns , selection ,  new String[]{eventId} , null , null , null);
        while(c.moveToNext()) {
            Log.i("From Database retr." , c.getString(0) +"");
            trip_dto.setName(c.getString(0));
            trip_dto.setStart_point(c.getString(1));
            trip_dto.setEnd_point(c.getString(2));
            trip_dto.setNotes(c.getString(3));
            trip_dto.setDateStr(c.getString(4));
            trip_dto.setTimeStr(c.getString(5));
            trip_dto.setTripStatus(c.getString(6));
            trip_dto.setTripType(c.getString(7));
            trip_dto.setStartLatitude(c.getString(8));
            trip_dto.setStartLongitude(c.getString(9));
            trip_dto.setEndLatitude(c.getString(10));
            trip_dto.setEndLongitude(c.getString(11));
            trip_dto.setEventId(c.getString(12));
        }
        return trip_dto;
    }


    public long updateEventId(String event_id , String initId){
        SQLiteDatabase db = trip.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip_Table.KEY_VAL13, event_id);
        long retval = db.update(Trip_Table.TABLE_NAME , contentValues , Trip_Table.KEY_VAL13+"=?" ,new String[]{initId});
        return retval;
    }

    public ArrayList<Trip_DTO> retreiveAllTrips(){
        Cursor c;
        ArrayList<Trip_DTO> myTripsList = new ArrayList<>();
        SQLiteDatabase db = trip.getReadableDatabase();
        String[] columns = {Trip_Table.KEY_VAL1, Trip_Table.KEY_VAL2 , Trip_Table.KEY_VAL3 , Trip_Table.KEY_VAL4 , Trip_Table.KEY_VAL5 , Trip_Table.KEY_VAL6 , Trip_Table.KEY_VAL7 , Trip_Table.KEY_VAL8 , Trip_Table.KEY_VAL9 , Trip_Table.KEY_VAL10 , Trip_Table.KEY_VAL11 , Trip_Table.KEY_VAL12 , Trip_Table.KEY_VAL13};
        c = db.query(Trip_Table.TABLE_NAME , columns , null , null , null , null , null);
        while(c.moveToNext()) {
            Trip_DTO trip_dto = new Trip_DTO();
            trip_dto.setName(c.getString(0));
            trip_dto.setStart_point(c.getString(1));
            trip_dto.setEnd_point(c.getString(2));
            trip_dto.setNotes(c.getString(3));
            trip_dto.setDateStr(c.getString(4));
            trip_dto.setTimeStr(c.getString(5));
            trip_dto.setTripStatus(c.getString(6));
            trip_dto.setTripType(c.getString(7));
            trip_dto.setStartLatitude(c.getString(8));
            trip_dto.setStartLongitude(c.getString(9));
            trip_dto.setEndLatitude(c.getString(10));
            trip_dto.setEndLongitude(c.getString(11));
            trip_dto.setEventId(c.getString(12));
            myTripsList.add(trip_dto);
        }
        return myTripsList;
    }

    public ArrayList<Trip_DTO> retrieveUpcomingTrips(String user_id){
        Cursor c;
        ArrayList<Trip_DTO> myTripsList = new ArrayList<>();
        String selection = Trip_Table.KEY_VAL7 + "=? And "+Trip_Table.KEY_VAL14 + "=?";
        SQLiteDatabase db = trip.getReadableDatabase();
        String[] columns = {Trip_Table.KEY_VAL1, Trip_Table.KEY_VAL2 , Trip_Table.KEY_VAL3 , Trip_Table.KEY_VAL4 , Trip_Table.KEY_VAL5 , Trip_Table.KEY_VAL6 , Trip_Table.KEY_VAL7 , Trip_Table.KEY_VAL8 , Trip_Table.KEY_VAL9 , Trip_Table.KEY_VAL10 , Trip_Table.KEY_VAL11 , Trip_Table.KEY_VAL12 , Trip_Table.KEY_VAL13};
        c = db.query(Trip_Table.TABLE_NAME , columns , selection , new String[]{"upcoming" , user_id} , null , null , null);
        while(c.moveToNext()) {
            Trip_DTO trip_dto = new Trip_DTO();
            trip_dto.setName(c.getString(0));
            trip_dto.setStart_point(c.getString(1));
            trip_dto.setEnd_point(c.getString(2));
            trip_dto.setNotes(c.getString(3));
            trip_dto.setDateStr(c.getString(4));
            trip_dto.setTimeStr(c.getString(5));
            trip_dto.setTripStatus(c.getString(6));
            trip_dto.setTripType(c.getString(7));
            trip_dto.setStartLatitude(c.getString(8));
            trip_dto.setStartLongitude(c.getString(9));
            trip_dto.setEndLatitude(c.getString(10));
            trip_dto.setEndLongitude(c.getString(11));
            trip_dto.setEventId(c.getString(12));
            myTripsList.add(trip_dto);
        }
        return myTripsList;
    }

    public ArrayList<Trip_DTO> retrieveHistroryTrips(String user_id){
        Cursor c;
        ArrayList<Trip_DTO> myTripsList = new ArrayList<>();
        String selection = Trip_Table.KEY_VAL7 + "!=? And " + Trip_Table.KEY_VAL14 + "=?";
        SQLiteDatabase db = trip.getReadableDatabase();
        String[] columns = {Trip_Table.KEY_VAL1, Trip_Table.KEY_VAL2 , Trip_Table.KEY_VAL3 , Trip_Table.KEY_VAL4 , Trip_Table.KEY_VAL5 , Trip_Table.KEY_VAL6 , Trip_Table.KEY_VAL7 , Trip_Table.KEY_VAL8 , Trip_Table.KEY_VAL9 , Trip_Table.KEY_VAL10 , Trip_Table.KEY_VAL11 , Trip_Table.KEY_VAL12 , Trip_Table.KEY_VAL13};
        c = db.query(Trip_Table.TABLE_NAME , columns , selection , new String[]{"upcoming" , user_id} , null , null , null);
        while(c.moveToNext()) {
            Trip_DTO trip_dto = new Trip_DTO();
            trip_dto.setName(c.getString(0));
            trip_dto.setStart_point(c.getString(1));
            trip_dto.setEnd_point(c.getString(2));
            trip_dto.setNotes(c.getString(3));
            trip_dto.setDateStr(c.getString(4));
            trip_dto.setTimeStr(c.getString(5));
            trip_dto.setTripStatus(c.getString(6));
            trip_dto.setTripType(c.getString(7));
            trip_dto.setStartLatitude(c.getString(8));
            trip_dto.setStartLongitude(c.getString(9));
            trip_dto.setEndLatitude(c.getString(10));
            trip_dto.setEndLongitude(c.getString(11));
            trip_dto.setEventId(c.getString(12));
            myTripsList.add(trip_dto);
        }
        return myTripsList;
    }

    public String[] getNotesById(String eventId){
        Cursor c;
        String noteBeforeSplit = "";
        String[] noteList = null;
        SQLiteDatabase db = trip.getReadableDatabase();
        String selection = Trip_Table.KEY_VAL13 + "=?";
        String[] columns = { Trip_Table.KEY_VAL4 };
        c = db.query(Trip_Table.TABLE_NAME , columns , selection ,  new String[]{eventId} , null , null , null);
        while(c.moveToNext()) {
            Log.i("From Database retr." , c.getString(0) +"");
            noteBeforeSplit = c.getString(0);
        }
        if(noteBeforeSplit != null)
        if(!noteBeforeSplit.equals("")){
            noteList  = noteBeforeSplit.split(",");
        }
        return noteList;
    }


//    public long insertCoorditates(Trip_DTO trip_dto){
//        SQLiteDatabase db = trip.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(Trip_Table.KEY_VAL14, trip_dto.getImageUrl());
//        contentValues.put(Trip_Table.KEY_VAL15, trip_dto.getPolyLine());
//        contentValues.put(Trip_Table.KEY_VAL16, trip_dto.getDistance());
//        contentValues.put(Trip_Table.KEY_VAL17, trip_dto.getDuration());
//        contentValues.put(Trip_Table.KEY_VAL18, trip_dto.getVelocity());
//
//        long retval = db.insert(Trip_Table.TABLE_NAME , null , contentValues);
//        return retval;
//    }


    public long updateTrip(Trip_DTO trip_dto){
        SQLiteDatabase db = trip.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip_Table.KEY_VAL1, trip_dto.getName());
        contentValues.put(Trip_Table.KEY_VAL2, trip_dto.getStart_point());
        contentValues.put(Trip_Table.KEY_VAL3, trip_dto.getEnd_point());
        contentValues.put(Trip_Table.KEY_VAL5, trip_dto.getDateStr()); // trip_dto.getDate()
        contentValues.put(Trip_Table.KEY_VAL6, trip_dto.getTimeStr()); // trip_dto.getTime()
        contentValues.put(Trip_Table.KEY_VAL9, trip_dto.getStartLatitude());
        contentValues.put(Trip_Table.KEY_VAL10, trip_dto.getStartLongitude());
        contentValues.put(Trip_Table.KEY_VAL11, trip_dto.getEndLatitude());
        contentValues.put(Trip_Table.KEY_VAL12, trip_dto.getEndLongitude());
        long retval = db.update(Trip_Table.TABLE_NAME , contentValues , Trip_Table.KEY_VAL13+"=?" ,new String[]{trip_dto.getEventId()});
        return retval;
    }


    public long deleteTrip(String event_id){
        SQLiteDatabase db = trip.getWritableDatabase();
        String selection = Trip_Table.KEY_VAL13 + "=?";
         return  db.delete(Trip_Table.TABLE_NAME , selection , new String[]{event_id} );
    }


    public long updateTripStatus(String status, String event_id){
        SQLiteDatabase db = trip.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip_Table.KEY_VAL7, status);

         return db.update(Trip_Table.TABLE_NAME , contentValues , Trip_Table.KEY_VAL13+"=?" ,new String[]{event_id});
    }

                                    ///\\\





    public static class Trip_Table extends SQLiteOpenHelper{

        private final static String TABLE_NAME = "Trips";
        private static final String KEY_ID = "ID";
        private static final String KEY_VAL1 = "Name";
        private static final String KEY_VAL2 = "Start_Point";
        private static final String KEY_VAL3 = "End_Point";
        private static final String KEY_VAL4 = "Notes";
        private static final String KEY_VAL5 = "Date";
        private static final String KEY_VAL6 = "Time";
        private static final String KEY_VAL7 = "Status";
        private static final String KEY_VAL8 = "Type";
        private static final String KEY_VAL9 = "Start_Latitude";
        private static final String KEY_VAL10 = "Start_Longitude";
        private static final String KEY_VAL11 = "End_Latitude";
        private static final String KEY_VAL12 = "End_Longitude";
        private static final String KEY_VAL13 = "Event_ID";
        private static final String KEY_VAL14 = "user_id";
//        private static final String KEY_VAL15 = "polyLine";
//        private static final String KEY_VAL16 = "distance";
//        private static final String KEY_VAL17 = "duration";
//        private static final String KEY_VAL18 = "velocity";
        private static final String DATABASE_NAME = "mydb.db";
        private static final int DATABASE_VERSION = 1;




        public Trip_Table(Context context) {
            super(context, DATABASE_NAME, null , DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                + " , " + KEY_VAL1 + " VarChar2(50) "
                + " , " + KEY_VAL2 + " VarChar2(50) "
                + " , " + KEY_VAL3 + " VarChar2(50) "
                + " , " + KEY_VAL4 + " VarChar2(1000) "
                + " , " + KEY_VAL5 + " VarChar2(15) "
                + " , " + KEY_VAL6 + " VarChar2(15) "
                + " , " + KEY_VAL7 + " VarChar2(50) "
                + " , " + KEY_VAL8 + " VarChar2(50) "
                + " , " + KEY_VAL9 + " VarChar2(50) "
                + " , " + KEY_VAL10 + " VarChar2(50) "
                + " , " + KEY_VAL11 + " VarChar2(50) "
                + " , " + KEY_VAL12 + " VarChar2(50) "
                + " , " + KEY_VAL13 + " VarChar2(50) "
                + " , " + KEY_VAL14 + " VarChar2(50) "
//                + " , " + KEY_VAL15 + " VarChar2(1000) "
//                + " , " + KEY_VAL16 + " VarChar2(50) "
//                + " , " + KEY_VAL17 + " VarChar2(50) "
//                + " , " + KEY_VAL18 + " VarChar2(50) "
                + "  )"  );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }




}
