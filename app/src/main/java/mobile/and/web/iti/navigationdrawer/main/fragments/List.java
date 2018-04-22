package mobile.and.web.iti.navigationdrawer.main.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.maps.MapHistoryImageIntentService;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;
import mobile.and.web.iti.navigationdrawer.main.fragments.adapters.MyArrayAdapter;

import static android.content.Context.MODE_PRIVATE;
import static mobile.and.web.iti.navigationdrawer.add.trip.AddTripActivity.DIRECTORY_NAME;


public class List extends Fragment {

    ListView myList ;
    View myListView ;
    Trip_DTO dtoss;
    private RequestQueue rQueue;
    String result1,result2,result3,result;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser user;
    ArrayList<Trip_DTO> tripDtos;
    String durationpart2;
    Double doubleDuration;
    ProgressDialog dialog;
    MyArrayAdapter myArr;


    public List() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
            Log.i("user id" , user_id);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
            Log.i("user id" , user_id);
        }

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       myListView= inflater.inflate(R.layout.fragment_list, container, false);


       if(user_id != null) {
           tripDtos = getTrips();
           Log.i("user id", user_id);

        if(tripDtos.size() > 0) {
            ArrayList<Trip_DTO> tripsWithImage = setAllImagesFromStorage(tripDtos);

            if(tripsWithImage.size() > 0) {
                 myArr = new MyArrayAdapter(getContext(), R.layout.rowcustom, R.id.Name, tripsWithImage);

                myList = myListView.findViewById(R.id.list);
                myList.setAdapter(myArr);
                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        PopupMenu popup = new PopupMenu(getContext(), view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.trip_history_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(tripDtos.get(i).getEventId() ,tripDtos.get(i).getStartLatitude(),tripDtos.get(i).getStartLongitude(),tripDtos.get(i).getEndLatitude(),tripDtos.get(i).getEndLongitude() ) );
                        popup.show();





                    }
                });
            }

        }else{
            Toast.makeText(getContext(), "No Trips", Toast.LENGTH_SHORT).show();

        }
       }else{
//           Toast.makeText(getContext(), "user id = null", Toast.LENGTH_SHORT).show();
       }



        return myListView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public ArrayList<Trip_DTO> getTrips(){
        ArrayList<Trip_DTO> trips = null;
        DatabaseAdapter adapter = new DatabaseAdapter(getContext());
        trips = adapter.retrieveHistroryTrips(user_id);
        if(trips.size() > 0) {
            if (trips.get(0).getEventId() != null) {
                Log.i("nela elmota5alefa", trips.get(0).getEventId());
            } else {
                Log.i("nela elmota5alefa", "kan fe w 5eles");
            }
        }
        return trips;
    }

    private ArrayList<Trip_DTO> setAllImagesFromStorage(ArrayList<Trip_DTO> trip_dtos)
    {

        if(trip_dtos.size() > 0) {
            for (int i = 0; i < trip_dtos.size(); i++) {
                try {
                    File f = new File(getContext().getDir(DIRECTORY_NAME, MODE_PRIVATE), trip_dtos.get(i).getEventId() + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    trip_dtos.get(i).setPlaceImage(b);
                    Log.i("Try Load index:"+i, "Array Loaded successfully");

                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    Log.i("Catch Load index:"+i , ":(");
                }
            }
        }
        return trip_dtos;
    }


    //////////////////////////////////////Menu Code///////////////////////////////////////////////////////////

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        String ID;
        String startLat , startLong , endLat , endLong ;


        public MyMenuItemClickListener(String id ,String startLat ,String startLong , String endLat , String endLong ) {

            ID = id;
            this.startLat =startLat;
            this.startLong = startLong;
            this.endLat = endLat;
            this.endLong = endLong;

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.btnShow:

////////////////////////////////////////////////////////////Maps direction////////////////////////////////////

                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(  getActivity() ,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                    }



                    final String url="https://maps.googleapis.com/maps/api/directions/json?origin="
                            +startLat+ "," +startLong
                            + "&destination=" +endLat+ "," +endLong
                            +"&key="+getResources().getString(R.string.api_key);
                    Log.i("zeft",url);
                    Log.i("nela",startLat);
                    Log.i("nela",startLong);
                    rQueue = Volley.newRequestQueue(getContext());

                    Log.i("a","1");
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray jsonArray = null;
                                    try {
                                        dialog = new ProgressDialog(getContext());
                                        dialog.setMessage("please wait..");
                                        dialog.show();

                                        Log.i("volley", "1");
                                        jsonArray = response.getJSONArray("routes");
                                        JSONObject routes = jsonArray.getJSONObject(0);
                                        JSONObject overview_polyline = routes.getJSONObject("overview_polyline");
                                        result1 = overview_polyline.getString("points");

                                        //    to Reterieve Duration
                                        //     JSONObject jsonObject = new JSONObject(stringBuilder);
                                        //    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                        //    JSONObject routes = jsonArray.getJSONObject(0);
                                        JSONArray legs = routes.getJSONArray("legs");
                                        JSONObject legss = legs.getJSONObject(0);
                                        JSONObject dur = legss.getJSONObject("duration");
                                        result2 = dur.getString("text");

                                        /// to Reterieve Distance
                                        //JSONObject jsonObject = new JSONObject(stringBuilder);
                                        //JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                        // JSONObject routes = jsonArray.getJSONObject(0);
                                        JSONArray legsdistance = routes.getJSONArray("legs");
                                        JSONObject legssdis = legsdistance.getJSONObject(0);
                                        JSONObject dis = legssdis.getJSONObject("distance");
                                        result3 = dis.getString("text");

                                        result=result1+"-"+result2+"-"+result3;

                                        if(result!= null) {
                                            Log.i("a","2");

                                            String[] parts = result.split("-");
                                            String part1 = parts[0];
                                            String part2 = parts[1];
                                            String part3 = parts[2];
                                            Log.i("a","3");

                                            //to get duration
                                            /////////////////////////////////mayada code////////////////////////////////////////
                                            if (part2.contains("hour") && part2.contains("min")){
                                                Log.i("dur",part2);
                                                String[] duraionparts = part2.split(" ");

                                                Log.i("dur",duraionparts[0]);
                                                Log.i("dur",duraionparts[1]);

                                                String hoursNumber = duraionparts[0];
                                                String minsNumber = duraionparts[2];

                                                doubleDuration= (Double.parseDouble(hoursNumber)*60) +Double.parseDouble(minsNumber);
                                                durationpart2="mins";

                                            }else{
                                                String[] duraionparts = part2.split(" ");
                                                String duraionpart1 = duraionparts[0];
                                                durationpart2 = duraionparts[1];
                                                doubleDuration=Double.parseDouble(duraionpart1);
                                            }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                                            String[] duraionparts = part2.split(" ");
//                                            String duraionpart1 = duraionparts[0];
//                                            String durationpart2 = duraionparts[1];
//                                            Double doubleDuration=Double.parseDouble(duraionpart1);

                                            String[] distanceparts = part3.split(" ");
                                            String distancepart1 = distanceparts[0];
                                            String distancepart2 = distanceparts[1];
                                            String distancepart1Edit ;
                                            Double doubleDistance;
                                            if(distancepart1.contains(",")){
                                                String [] distanceDiv = distancepart1.split(",");
                                                distancepart1Edit = distanceDiv[0] + "." + distanceDiv[1];
                                                doubleDistance=Double.parseDouble(distancepart1Edit);
                                            }else {
                                                doubleDistance = Double.parseDouble(distancepart1);
                                            }
                                            //Toast.makeText(Maps.this,"Sucess Duration"+s.toString(), Toast.LENGTH_SHORT).show();
                                            //                                     durat.setText("Trip Duration"+ part2);
                                            //  distnc.setText("Trip Distance"+part3);
                                            Double veloc=(Double)(doubleDistance/doubleDuration);
                                            //velocity.setText("Trip Velocity"+(round(veloc, 4))+distancepart2+"/"+durationpart2);


                                            //duratview.setText(duraionpart1);
                                            //distncview.setText(doubleDistance);
                                            // velocityview.setText(veloc);

                                            Log.i("url","gooood");
                                            String bimg ="https://maps.googleapis.com/maps/api/staticmap?size=400x400&center="+startLat+","+startLong+"|"+endLat+","+endLong+"&zoom=8&path=fillcolor:red7Ccolor:red%7Cenc:"+part1+"&key="+getResources().getString(R.string.api_key);
                                            Log.i("zeft2sentactivity",bimg);
                                            Intent intent=new Intent(getContext(),MapHistoryImageIntentService.class);
                                            intent.putExtra("SentURL",bimg);
                                            intent.putExtra("duration",doubleDuration);
                                            intent.putExtra("durationUnit",durationpart2);
                                            intent.putExtra("distance",doubleDistance);
                                            intent.putExtra("distanceUnit",distancepart2);
                                            intent.putExtra("velocity",round(veloc, 4));

                                            Log.i("a","4");

                                            getContext().startService(intent);
                                            //String z="https://maps.googleapis.com/maps/api/staticmap?size=400x400&center="+31.213370+","+29.933364+"|"+31.246188+","+29.967319+"&zoom=11&path=fillcolor:red7Ccolor:red%7Cenc:"+part1+"&key=AIzaSyCbD-YBSWc4jHoWo4bGaH8GmufuMRD10Ro";

                                            //Log.i("zeft2",z);
                                            //Picasso.with(ViewTrip.this)
                                            //.load("https://maps.googleapis.com/maps/api/staticmap?size=400x400&center="+31.213370+","+29.933364+"|"+31.246188+","+29.967319+"&zoom=11&path=fillcolor:red7Ccolor:red%7Cenc:"+part1+"&key=AIzaSyCbD-YBSWc4jHoWo4bGaH8GmufuMRD10Ro")
                                            //.into(imagggeview);
                                            dialog.dismiss();
                                        }else {
                                            Toast.makeText(getContext(),"URL Error", Toast.LENGTH_SHORT).show();
                                            Log.i("url","errorrrr");
                                            dialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Long path , can't draw routes in map", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("volley","2");
                            Toast.makeText(getContext(), "Something went wrong , please try again and check internet connection ..", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    rQueue.add(request);

                    return true;

                case R.id.btnDel:

                    confirmDialog(ID);
                    return true;


                default:
            }
            return false;
        }


    }

    private void confirmDialog(final String event_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setMessage("Are you sure you want to delete this trip?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Yes-code
                        int result = deleteCalendarEvent(event_id);
                        myArr.notifyDataSetChanged();
//                        Toast.makeText(getContext(), "result = "+result, Toast.LENGTH_SHORT).show();
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


    public  int deleteCalendarEvent(String event_id){
        int rows = -1;
        long retVal = deleteTrip(event_id);
        if(retVal != -1) {
            ContentResolver cr = getContext().getContentResolver();
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
        DatabaseAdapter adapter = new DatabaseAdapter(getContext());
        long retVal = adapter.deleteTrip(event_id);
        Log.i("delete SQL", retVal+"");
        Toast.makeText(getContext(), "deleted ", Toast.LENGTH_SHORT).show();
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("myFrag");
        final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        return retVal;
    }

///////////////////////////////////////////////end of menu code //////////////////////////////////////////////////
            public static double round(double value, int places) {
                if (places < 0) throw new IllegalArgumentException();
                long factor = (long) Math.pow(10, places);
                value = value * factor;
                long tmp = Math.round(value);
                return (double) tmp / factor;
            }


}
