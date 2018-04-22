package mobile.and.web.iti.navigationdrawer.main.fragments.adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.maps.FloatingViewService;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;
import mobile.and.web.iti.navigationdrawer.update.view.trips.ViewTrip;
import mobile.and.web.iti.navigationdrawer.main.fragments.ViewHolder;
import mobile.and.web.iti.navigationdrawer.update.view.trips.update_trip;


/**
 * Created by Alaa on 3/5/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<Trip_DTO> myTrips;

    Context _context ;
    String TrName;
    String TrFrom;
    String TrTo ;
    String TrDate ;
    String TrTime ;
    Bitmap TrImg ;
    String Id;

    public MyAdapter(Context context , ArrayList<Trip_DTO> myTrips) {
        _context=context;
        this.myTrips = myTrips;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_view,parent,false);

        ViewHolder vh = new ViewHolder(v) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (myTrips.size() > 0) {
            if(myTrips.get(position).getPlaceImage() != null)
                holder.getMyImg().setImageBitmap(myTrips.get(position).getPlaceImage());
            else
                holder.getMyImg().setImageResource(R.mipmap.image);

            //   holder.getMyImg().setImageResource(myCakes[position].getImage());

            holder.getDesc().setText(myTrips.get(position).getName());


            holder.getOverFlow().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrFrom = myTrips.get(position).getStart_point();
                    TrTo = myTrips.get(position).getEnd_point();
                    TrDate = myTrips.get(position).getDateStr();
                    TrTime=myTrips.get(position).getTimeStr();
                    TrImg = myTrips.get(position).getPlaceImage();
                    Id = myTrips.get(position).getEventId();
                    TrName = holder.getDesc().getText().toString();
                    PopupMenu popup = new PopupMenu(_context, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.choose_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position , Id ,TrName , TrFrom ,TrTo , TrDate ,TrTime ,TrImg));
                    popup.show();
                }
            });

        }



    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String trip ;
        String trFrom;
        String trTo ;
        String trDate ;
        String trTime ;
        Bitmap trImg ;
        String ID ;
        int position;


        public MyMenuItemClickListener(int pos , String id ,String name , String from , String to , String date , String time , Bitmap img) {
            trip=name;
            trFrom = from;
            trTo =to;
            trDate=date ;
            trImg=img;
            trTime= time;
            ID = id;
            position = pos;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.btnStart:
                    long retVal = updateStatus("done" , ID);
                    Log.i("update retval done" , retVal+"");
                    Intent in = new Intent(_context,FloatingViewService.class);
                    in.putExtra("id",ID);
                    _context.startService(in);

                    //route from my current position to destination point
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=31.262931,29.994045");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    try
                    {
                        _context.startActivity(mapIntent);

                    }
                    catch(ActivityNotFoundException ex)
                    {
                        try
                        {
                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            _context.startActivity(unrestrictedIntent);
                        }
                        catch(ActivityNotFoundException innerEx)
                        {
                            Toast.makeText(_context, "Please install Google maps application", Toast.LENGTH_LONG).show();
                        }
                    }


                    Toast.makeText(_context, trip +" started", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.btnUpdate:

                    Intent intent_update = new Intent(_context , update_trip.class);
                    intent_update.putExtra("name",trip);
                    intent_update.putExtra("from",trFrom);
                    intent_update.putExtra("to",trTo);
                    intent_update.putExtra("date",trDate);
                    intent_update.putExtra("time",trTime);
                    ByteArrayOutputStream _bs1;
                    if(trImg != null) {
                        _bs1 = new ByteArrayOutputStream();
                        trImg.compress(Bitmap.CompressFormat.PNG, 50, _bs1);
                        intent_update.putExtra("byteArray", _bs1.toByteArray());
                    }
                    else {
                        intent_update.putExtra("byteArray", null+"");
                    }
                    intent_update.putExtra("id",ID);
                    _context.startActivity(intent_update);






                    Toast.makeText(_context, trip+" updated", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.btnDelete:

                   confirmDialog(ID , position);
                    return true;
                case R.id.btnView:

                    Intent intent = new Intent(_context , ViewTrip.class);
                    intent.putExtra("name",trip);
                    intent.putExtra("from",trFrom);
                    intent.putExtra("to",trTo);
                    intent.putExtra("date",trDate);
                    intent.putExtra("time",trTime);
                    ByteArrayOutputStream _bs;
                    if(trImg != null) {
                        _bs = new ByteArrayOutputStream();
                        trImg.compress(Bitmap.CompressFormat.PNG, 50, _bs);
                        intent.putExtra("byteArray", _bs.toByteArray());
                    }
                   else {
                        intent.putExtra("byteArray", null+"");
                    }
                    intent.putExtra("id",ID);
                    _context.startActivity(intent);




//                    Toast.makeText(_context, "trip Viewed", Toast.LENGTH_SHORT).show();




                    return true;

                default:
            }
            return false;
        }

        private void confirmDialog(final String event_id , final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);

            builder
                    .setMessage("Are you sure you want to delete this trip?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // Yes-code
                            int result = deleteCalendarEvent(event_id);
                            myTrips.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(_context, "deleted ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }



        // delete trip in calendar and in SQLite \\



        public  int deleteCalendarEvent(String event_id){
            int rows = -1;
            long retVal = deleteTrip(event_id);
            if(retVal != -1) {
                ContentResolver cr = _context.getContentResolver();
                ContentValues values = new ContentValues();
                Uri deleteUri = null;
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.valueOf(event_id));
                rows = cr.delete(deleteUri, null, null);
                Log.i("delete calendar", "Rows deleted: " + rows);
            }else{
                Log.i("delete calendar", "No Rows deleted: " + rows);
            }
            return rows;
        }


        public long deleteTrip (String event_id){
            DatabaseAdapter adapter = new DatabaseAdapter(_context);
            long retVal = adapter.deleteTrip(event_id);
            Log.i("delete SQL", retVal+"");
//            Toast.makeText(_context, "retVal = "+retVal, Toast.LENGTH_SHORT).show();
            return retVal;
        }

        ////////////\\\\\\\\\\\\\



    }


    @Override
    public int getItemCount() {
        return myTrips.size();
    }


    public long updateStatus(String status , String event_id){
        DatabaseAdapter adapter = new DatabaseAdapter(_context);
        return  adapter.updateTripStatus(status , event_id);
    }




}
