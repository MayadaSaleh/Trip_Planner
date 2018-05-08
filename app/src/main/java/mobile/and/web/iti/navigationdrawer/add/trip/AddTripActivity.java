package mobile.and.web.iti.navigationdrawer.add.trip;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import mobile.and.web.iti.navigationdrawer.login.register.Login;
import mobile.and.web.iti.navigationdrawer.main.fragments.MainActivity;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.adapters.AutoAdapter;
import mobile.and.web.iti.navigationdrawer.add.trip.adapters.Recycler_Listener;
import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
        , TimePickerDialog.OnTimeSetListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    // views declaration \\
    private EditText trip_name, start_point, end_point, trip_notes;
    private TextView trip_date, trip_time , trip_type;
    private Button save_btn, pick_date ;
    private ImageView placeImageView;
    //\\
    // Others \\
    private int day, month, year, hour, minute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private int day_round, month_round, year_round, hour_round, minute_round;
    private int dayFinal_round , monthFinal_round, yearFinal_round, hourFinal_round , minuteFinal_round;
    private String dateStr, timeStr, dateStr_round, timeStr_round;
    private RecyclerView startRecycler, endRecycler;
    private LinearLayoutManager myLinearLayoutManagerStart, myLinearLayoutManagerEnd;
    protected GoogleApiClient myGoogleApiClient;
    private static final LatLngBounds myBounds = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private AutoAdapter myAdapterS, myAdapterE;
    private double startLat = 90, startLng = 190, endLat = 90, endLng = 190;
    //\\
    // Calendar Event ID \\
    long eventId;
    long initId = -1122;
    // broadcast receiver \\
    private Trip_DTO trip_dto, trip_dto_round;
    // place Image to be set in recycler view \\
    Bitmap placeImage, placeImage_round;
    // save image internal in file name;
    public final static String DIRECTORY_NAME = "Trips";
    // Test Components of notes  \\
    private Button add_btn;
    private ArrayList<String> notesList;

    // Round Trip Layout
    private RelativeLayout roundLayout;
    private TextView showDateText, showTimeText;
    //    private Button pickDate_round , pickTime_round;
    private double startLat_round, startLng_round, endLat_round, endLng_round;
    private boolean checkTripType = true ;
    private boolean checkTripTime;

    // place Id \\
    String placeId, placeId_round;

    private FirebaseAuth mAuth;
    private String user_id = "";
    private FirebaseUser user;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        buildGoogleApiClient();
//        calendar();
        Log.i("Paaaaaaaaath1", getApplicationContext().getFilesDir().getPath() + "");
        Log.i("Paaaaaaaaath2", getApplicationContext().getPackageName() + "");
        Log.i("Path resource", getApplicationContext().getPackageResourcePath() + "");
        Log.i("Path code", getApplicationContext().getPackageCodePath() + "");
        Log.i("Path code", getApplicationContext().getDir(DIRECTORY_NAME, MODE_PRIVATE) + "");
        initiallizeViews();
        handleAutoCompleteET();

    }


    private void initiallizeViews() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
        }

        // initialize EditTexts \\
        trip_name = findViewById(R.id.trip_name_et);
        start_point = findViewById(R.id.trip_start_et);
        end_point = findViewById(R.id.trip_end_et);
        trip_notes = findViewById(R.id.trip_notes_et);
        trip_date = findViewById(R.id.trip_date_et);
        trip_time = findViewById(R.id.trip_time_et);
        trip_type = findViewById(R.id.trip_type_et);

        ///\\\
        // initialize TextViews \\

        ///\\\

        // initialize Buttons \\
        save_btn = findViewById(R.id.save_btn);
        pick_date = findViewById(R.id.pick_btn);

        ///\\\

        // initialize imageView to test getPlaces() funciton \\
        placeImageView = findViewById(R.id.placeImg);

        // initialize round trip Layout and compontents \\
        roundLayout = findViewById(R.id.round_layout);
        showDateText = findViewById(R.id.dateText);
        showTimeText = findViewById(R.id.timeText);
//        pickDate_round = findViewById(R.id.pickReturnDate_btn);
//        pickTime_round = findViewById(R.id.pickReturnTime_btn);


        // Recycler Tools Initialization \\
        myAdapterS = new AutoAdapter(this, R.layout.search_row, myGoogleApiClient, myBounds, null);
        myAdapterE = new AutoAdapter(this, R.layout.search_row, myGoogleApiClient, myBounds, null);
        startRecycler = findViewById(R.id.myStarRecycler);
        endRecycler = findViewById(R.id.myEndRecycler);
        myLinearLayoutManagerStart = new LinearLayoutManager(this);
        myLinearLayoutManagerEnd = new LinearLayoutManager(this);
        startRecycler.setLayoutManager(myLinearLayoutManagerStart);
        endRecycler.setLayoutManager(myLinearLayoutManagerEnd);
        startRecycler.setAdapter(myAdapterS);
        endRecycler.setAdapter(myAdapterE);

        //\\

        // notes list Test Views
        notesList = new ArrayList<>();
        add_btn = findViewById(R.id.add_note);


        //trip dtos
        trip_dto = new Trip_DTO();
        trip_dto_round = new Trip_DTO();

        // oneWay trip to setChecked
        RadioButton radioButton =  findViewById(R.id.oneWay);
        radioButton.setChecked(true);
    }



    public void save_btn(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED) {
            if (checkTripType) {
                if (startLat != 90 && endLat != 90) {
                    if (hourFinal != 0 && dayFinal != 0) {
                        if (!(trip_name.getText().toString()).equals("")) {
                            if(isDateValid()) {
                                progressDialog();
                                saveOneWayTrip();
                                dialog.dismiss();
                                emptyViews();
                                startActivity(new Intent(AddTripActivity.this, MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(AddTripActivity.this, "trip time and date should be in future", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddTripActivity.this, "please enter a name to the trip", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddTripActivity.this, "please set date and time ..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddTripActivity.this, "please enter start and end points to save trip", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (startLat != 90 && endLat != 90) {
                    if (hourFinal != 0 && dayFinal != 0 && hourFinal_round != 0 && dayFinal_round != 0) {
                        if (!(trip_name.getText().toString()).equals("")) {
                            if(isDateValid() && isRoundDateValid()) {
                                progressDialog();
                                saveOneWayTrip();
                                emptyViews();
                                saveReturnTrip();
                            }else{
                                Toast.makeText(AddTripActivity.this, "trip time and date should be in future & return trip should be after the trip time", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(AddTripActivity.this, "please enter a name to the trip", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(AddTripActivity.this, "please set date and time for trip and return trip..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddTripActivity.this, "please enter start and end points to save trip", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            ActivityCompat.requestPermissions(AddTripActivity.this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    1);
            ActivityCompat.requestPermissions(AddTripActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    2);
        }
    }


    public void pickDate(View view) {
        checkTripTime = true;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripActivity.this, AddTripActivity.this, year, month, day);
        datePickerDialog.show();
    }



    public void pickTime(View view) {
        checkTripTime = true;
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTripActivity.this, AddTripActivity.this
                , hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
//        Toast.makeText(AddTripActivity.this, "onDate one way = " + checkTripTime, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (checkTripTime) {
            yearFinal = i;
            monthFinal = i1;
            dayFinal = i2;

            dateStr = yearFinal + "-" + (monthFinal+1) + "-" + dayFinal;
            trip_date.setText(dateStr);

        } else {
            yearFinal_round = i;
            monthFinal_round = i1;
            dayFinal_round = i2;
            dateStr_round = yearFinal_round + "-" + (monthFinal_round+1) + "-" + dayFinal_round;
            showDateText.setText(dateStr_round);
//            Toast.makeText(AddTripActivity.this, "onDate one way = " + checkTripTime, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (checkTripTime) {
            hourFinal = i;
            minuteFinal = i1;

            timeStr = hourFinal + ":" + minuteFinal;

            trip_time.setText(timeStr);
//            Toast.makeText(AddTripActivity.this, "onTime one way = " + checkTripTime, Toast.LENGTH_SHORT).show();

        } else {
            hourFinal_round = i;
            minuteFinal_round = i1;

            timeStr_round = hourFinal_round + ":" + minuteFinal_round;
            showTimeText.setText(timeStr_round);
//            Toast.makeText(AddTripActivity.this, "onTime one way = " + checkTripTime, Toast.LENGTH_SHORT).show();
        }
    }

    // add trip to calendar \\
    public void addToCalendar(Context context) {


        SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd");
        long startMillis;
        Calendar begin = Calendar.getInstance();
        begin.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
        startMillis = begin.getTimeInMillis();


//        begin.add(Calendar.DATE, 1);
        String dtUntill = form.format(begin.getTime());

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();


        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.TITLE, trip_name.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, "Trip Status : upcoming");

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" + dtUntill);  // here we can set a repetition
        values.put(CalendarContract.Events.DURATION, "+P1H");
        values.put(CalendarContract.Events.HAS_ALARM, 1);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AddTripActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    2);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Uri uri1 = cr.insert(CalendarContract.Events.CONTENT_URI, values);


        // Reminder before the event fires
        ContentValues remindVal = new ContentValues();
        if (uri1 != null) {
            eventId = Long.parseLong(uri1.getLastPathSegment());
//            Toast.makeText(AddTripActivity.this, "Event Id = " + eventId, Toast.LENGTH_SHORT).show();
        }
        remindVal.put(CalendarContract.Reminders.MINUTES, 30);
        remindVal.put(CalendarContract.Reminders.EVENT_ID, eventId);
        remindVal.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(AddTripActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    2);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, remindVal);

        Log.i("Calendar Event Id", eventId + "");

        Log.i("From Add Calendar", "End");

    }


    private void handleAutoCompleteET() {

        // for Start Point EditText \\
        start_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isNetworkAvailable()) {
                    if (!charSequence.toString().equals("") && myGoogleApiClient.isConnected()) {
                        startRecycler.setVisibility(View.VISIBLE);
                        myAdapterS.getFilter().filter(charSequence.toString());
                    } else if (!myGoogleApiClient.isConnected()) {
                        Toast.makeText(AddTripActivity.this, "No Internet Connection , please check connection ..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddTripActivity.this, "please check the internet connection !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        startRecycler.addOnItemTouchListener(
                new Recycler_Listener(this, new Recycler_Listener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        final AutoAdapter.AT_Place item = myAdapterS.getItem(position);
                        placeId_round = String.valueOf(item.placeId);

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(myGoogleApiClient, placeId_round);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getCount() == 1) {

//                                    Toast.makeText(AddTripActivity.this, String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
                                    start_point.setText(String.valueOf(places.get(0).getAddress()));
                                    placeId_round = places.get(0).getId();
                                    Log.i("PlaceId", placeId_round + "");
                                    getStartPhotos(placeId_round);
                                    Log.i("Photos", "After getPhotos()");
                                    LatLng latLng = places.get(0).getLatLng();
                                    startLat = latLng.latitude;
                                    startLng = latLng.longitude;
                                    Log.i("lat Start", startLat + "");
                                    Log.i("lng", startLng + "");
                                    startRecycler.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                })
        );


        /////////////////////\\\\\\\\\\\\\\\\\\\

        // for End point EditText \\
        end_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isNetworkAvailable()) {
                    if (!charSequence.toString().equals("") && myGoogleApiClient.isConnected()) {
                        endRecycler.setVisibility(View.VISIBLE);
                        myAdapterE.getFilter().filter(charSequence.toString());
                    } else if (!myGoogleApiClient.isConnected()) {
                        Toast.makeText(AddTripActivity.this, "No Internet Connection , please check connection ..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddTripActivity.this, "please check the internet connection !", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        endRecycler.addOnItemTouchListener(
                new Recycler_Listener(this, new Recycler_Listener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        final AutoAdapter.AT_Place item = myAdapterE.getItem(position);
                        placeId = String.valueOf(item.placeId);

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(myGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getCount() == 1) {
//                                    Toast.makeText(AddTripActivity.this, String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
                                    end_point.setText(String.valueOf(places.get(0).getAddress()));
                                    LatLng latLng = places.get(0).getLatLng();
                                    placeId = places.get(0).getId();
                                    Log.i("PlaceId", placeId + "");
                                    getEndPhotos(placeId);
                                    Log.i("Photos", "After getPhotos()");
                                    endLat = latLng.latitude;
                                    endLng = latLng.longitude;
                                    Log.i("lat Start", endLat + "");
                                    Log.i("lng", endLng + "");
                                    endRecycler.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                })
        );

    }


    protected synchronized void buildGoogleApiClient() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!myGoogleApiClient.isConnected() && !myGoogleApiClient.isConnecting()) {
            myGoogleApiClient.connect();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (myGoogleApiClient.isConnected()) {
            myGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        if (startRecycler.getVisibility() == View.VISIBLE) {
            startRecycler.setVisibility(View.GONE);
        } else if (endRecycler.getVisibility() == View.VISIBLE) {
            endRecycler.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }


    // Request photos and metadata for the specified place.
    private void getEndPhotos(final String placeId) {
        Log.i("Photos", "the first of getPhotos()");


        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                Log.i("Photos", "first onComplete()");
                try {
                    // Get the list of photos.
                    final PlacePhotoMetadataResponse[] photos = {task.getResult()};
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos[0].getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            placeImage = photo.getBitmap();
                            Log.i("Photos", "second onComplete()");
                            placeImageView.setImageBitmap(placeImage);
                        }
                    });
                } catch (Exception e) {
                    Log.i("Photos", "Doesn't exist");
                    placeImage = null;
                    placeImageView.setImageResource(R.mipmap.image);
                }
            }
        });
    }


    private void getStartPhotos(final String placeId) {
        Log.i("Photos", "the first of getPhotos()");


        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                Log.i("Photos", "first onComplete()");
                try {
                    // Get the list of photos.
                    final PlacePhotoMetadataResponse[] photos = {task.getResult()};
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos[0].getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            placeImage_round = photo.getBitmap();
                            Log.i("Photos", "second onComplete()");
//                            placeImageView.setImageBitmap(placeImage_round);
                        }
                    });
                } catch (Exception e) {
                    Log.i("Photos", "Doesn't exist");
                    placeImage_round = null;
//                    placeImageView.setImageResource(R.mipmap.image);
                }
            }
        });
    }


//    public void saveImage(View view) {
//        if (placeImage != null) {
//            saveToInternalStorage(placeImage, String.valueOf(eventId));
//        } else {
//            Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show();
//        }
//    }

    private String saveToInternalStorage(Bitmap bitmapImage, String event_Id) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/com.example.hesham.a4_3_hesham/app_Trips
        File directory = cw.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, event_Id + ".jpg");
        Log.i("My PAth", mypath.getPath());
        Log.i("My Abs.PAth", mypath.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    private void loadImageFromStorage(String event_Id) {

        try {
            File f = new File(getApplicationContext().getDir(DIRECTORY_NAME, MODE_PRIVATE), event_Id + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            placeImageView.setImageBitmap(b);
            Log.i("Try Load", "OK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("Catch Load", ":(");
        }

    }


    public void load_Image(View view) {
        loadImageFromStorage(trip_name.getText().toString());
    }


    // Set All Images To All Trip DTOs in Database \\
    private ArrayList<Trip_DTO> setAllImagesFromStorage(ArrayList<Trip_DTO> trip_dtos) {

        if (trip_dtos.size() > 0) {
            for (int i = 0; i < trip_dtos.size(); i++) {
                try {
                    File f = new File(getApplicationContext().getDir(DIRECTORY_NAME, MODE_PRIVATE), trip_dtos.get(i).getEventId() + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    trip_dtos.get(i).setPlaceImage(b);
                    Log.i("Try Load index:" + i, "Array Loaded successfully");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("Catch Load index:" + i, ":(");
                }
            }
        }
        return trip_dtos;
    }

    public void addNotesFn(View view) {
//        notesList.add(trip_notes.getText().toString());
        trip_dto.addNote(trip_notes.getText().toString());
        trip_notes.setText("");
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.round:
                if (checked) {
                    checkTripType = false;
                    roundLayout.setVisibility(View.VISIBLE);
                    trip_dto.setTripType("round");
//                    Toast.makeText(getApplicationContext(), checkTripTime +"", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), dayFinal_round + " " + monthFinal_round + " " + yearFinal_round + " .. " + hourFinal_round + " " + minuteFinal_round , Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.oneWay:
                if (checked) {
                    checkTripType = true;
                    roundLayout.setVisibility(View.GONE);
                    trip_dto.setTripType("oneWay");
//                    Toast.makeText(getApplicationContext(), checkTripTime +"", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), dayFinal_round + " " + monthFinal_round + " " + yearFinal_round + " .. " + hourFinal_round + " " + minuteFinal_round , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void pickDateRound(View view) {
        checkTripTime = false;
        Calendar calendar = Calendar.getInstance();
        year_round = calendar.get(Calendar.YEAR);
        month_round = calendar.get(Calendar.MONTH);
        day_round = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripActivity.this, AddTripActivity.this, year_round, month_round, day_round);
        datePickerDialog.show();
    }

    public void pickTimeRound(View view) {
        checkTripTime = false;
        Calendar calendar = Calendar.getInstance();
        hour_round = calendar.get(Calendar.HOUR_OF_DAY);
        minute_round = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTripActivity.this, AddTripActivity.this
                , hour_round, minute_round, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }


    public void saveOneWayTrip() {
        DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
        trip_dto.setName(trip_name.getText().toString());
        trip_dto.setStart_point(start_point.getText().toString());
        trip_dto.setEnd_point(end_point.getText().toString());
        trip_dto.setNotes(trip_dto.getNotes());
        trip_dto.setDateStr(dateStr);
        trip_dto.setTimeStr(timeStr);
        trip_dto.setTripStatus("upcoming");
        trip_dto.setStartLatitude(String.valueOf(startLat));
        trip_dto.setStartLongitude(String.valueOf(startLng));
        trip_dto.setEndLatitude(String.valueOf(endLat));
        trip_dto.setEndLongitude(String.valueOf(endLng));
        trip_dto.setEventId(String.valueOf(initId));
        trip_dto.setUser_id(user_id);   // set user id here
        long test = adapter.insertTrip(trip_dto);
//        Toast.makeText(AddTripActivity.this, "test = " + test, Toast.LENGTH_SHORT).show();
        if (test != -1) {
            addToCalendar(AddTripActivity.this);
            long retVal = adapter.updateEventId(String.valueOf(eventId), String.valueOf(initId));
            if (retVal != -1) {
                trip_dto.setEventId(String.valueOf(eventId));
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                if (placeImage != null)
                    saveToInternalStorage(placeImage, String.valueOf(eventId));
                Log.i("nela", "habla");
                startRecycler.setVisibility(View.GONE);
                endRecycler.setVisibility(View.GONE);


                Log.i("Test Calendar", "before");
                Log.i("Test Calendar", "After");


            } else {
                Toast.makeText(this, "Something went wrong with event id !", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong while saving", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveReturnTrip() {
        DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
        trip_dto_round.setName(trip_name.getText().toString() + " (Return)");
        trip_dto_round.setStart_point(end_point.getText().toString());
        trip_dto_round.setEnd_point(start_point.getText().toString());
        trip_dto_round.setNotes(trip_dto.getNotes());
        trip_dto_round.setDateStr(dateStr_round);
        trip_dto_round.setTimeStr(timeStr_round);
        trip_dto_round.setTripStatus("upcoming");
        trip_dto_round.setStartLatitude(String.valueOf(endLat));
        trip_dto_round.setStartLongitude(String.valueOf(endLng));
        trip_dto_round.setEndLatitude(String.valueOf(startLat));
        trip_dto_round.setEndLongitude(String.valueOf(startLng));
        trip_dto_round.setEventId(String.valueOf(initId));
        trip_dto_round.setUser_id(user_id);   // set user id here
        long test = adapter.insertTrip(trip_dto_round);
        if (test != -1) {
            addToCalendar(AddTripActivity.this);
            long retVal = adapter.updateEventId(String.valueOf(eventId), String.valueOf(initId));
            if (retVal != -1) {
                trip_dto_round.setEventId(String.valueOf(eventId));
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                if (placeImage_round != null)
                    saveToInternalStorage(placeImage_round, String.valueOf(eventId));

                trip_name.setText("");
                start_point.setText("");
                end_point.setText("");
                trip_notes.setText("");
                trip_date.setText("");
                trip_time.setText("");
                trip_type.setText("");
                Log.i("nela", "habla + mota5alefa");
                startRecycler.setVisibility(View.GONE);
                endRecycler.setVisibility(View.GONE);


                Log.i("Test Calendar", "before");
                Log.i("Test Calendar", "After");
                dialog.dismiss();
                Intent intent = new Intent(AddTripActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


            } else {
                Toast.makeText(this, "Something went wrong with event id !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        } else {
            Toast.makeText(this, "Something went wrong while saving", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save_btn.performClick();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {


                    Toast.makeText(AddTripActivity.this, "Permission denied..", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case 2: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Toast.makeText(AddTripActivity.this, "granted", Toast.LENGTH_SHORT).show();

                } else {


                    Toast.makeText(AddTripActivity.this, "Please accept so you can add trip to calendar", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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



    public boolean isRoundDateValid(){
        boolean retVal = false;
        // get Time in millies for the trip and for now ,then see if it's coming or previous
        long roundTime;
        Calendar begin = Calendar.getInstance();
        begin.set(yearFinal_round, monthFinal_round, dayFinal_round, hourFinal_round, minuteFinal_round);
        roundTime = begin.getTimeInMillis();


        // for now !!
        long tripTime;
        Calendar now = Calendar.getInstance();
        now.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
        tripTime = now.getTimeInMillis();

        if(roundTime > tripTime){
            retVal = true;
        }


        return retVal;
    }

    public void progressDialog(){
        dialog = new ProgressDialog(AddTripActivity.this);
        dialog.setMessage("please wait..");
        dialog.show();
    }

    public void emptyViews(){
        trip_name.setText("");
        start_point.setText("");
        end_point.setText("");
        trip_notes.setText("");
    }
}
