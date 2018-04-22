package mobile.and.web.iti.navigationdrawer.main.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.AddTripActivity;
import mobile.and.web.iti.navigationdrawer.login.register.Login;

import static mobile.and.web.iti.navigationdrawer.login.register.Login.FACEBOOK_SHAREDPREFERENCES;
import static mobile.and.web.iti.navigationdrawer.login.register.RegisterActivity.EMAIL_SHAREDPREFERENCES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager mgr;
    FragmentTransaction tr;
    Home my_Home;
    List myList;
    TextView myName;
    JSONObject response, profile_pic_data, profile_pic_url;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String user_id;
    String profile;
    String emailShared;
    TextView myEmail;
    ImageView Profile;
    String emailID;
    String tag ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
        }





//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationDialog();
//        }
//        else {
//            checkLocationDialog();
//        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(MainActivity.this,AddTripActivity.class);
                startActivity(in);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        my_Home = new Home();
//        Bundle bundle = new Bundle();
//        bundle.putString("user_id" , user_id);
//        my_Home.setArguments(bundle);
        mgr = getSupportFragmentManager();


        tr = mgr.beginTransaction();
        tr.add(R.id.Frag_Lay, my_Home, "myHome");
        tag="myHome";
        tr.commit();

        myName = navigationView.getHeaderView(0).findViewById(R.id.navName);
        myEmail = navigationView.getHeaderView(0).findViewById(R.id.navEmail);
        Profile = navigationView.getHeaderView(0).findViewById(R.id.imageBar);

        getFacebookSharedPreferences();
        getEmailSharedPreferences();
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String name = intent.getStringExtra("Name");
        String jsondata = intent.getStringExtra("userProfile");
        emailID = intent.getStringExtra("loginId");
//        user_id = intent.getStringExtra("id");
//        Toast.makeText(MainActivity.this, "id = " + user_id, Toast.LENGTH_SHORT).show();


//        Log.i("nela",email);
        if (emailID != null) {
//            getEmailSharedPreferences();
            myEmail.setText(emailID);
            myName.setText("welcome");
            setEmailSharedPreferences();
//            Toast.makeText(MainActivity.this, "user id "+ user_id, Toast.LENGTH_SHORT).show();
        }
        ///////////////////////////////////////////////////facebook///////////////
        else if (jsondata != null) {

            Log.w("Jsondata", jsondata);
            try {
                response = new JSONObject(jsondata);
                myEmail.setText(response.get("email").toString());
                myName.setText(response.get("name").toString());
                profile_pic_data = new JSONObject(response
                        .get("picture").toString());
                profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                Picasso.with(this).load(profile_pic_url.getString("url")).into(Profile);
                setFacebookSharedPreferences();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment myFrag = getSupportFragmentManager().findFragmentByTag("MyFrag");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
//            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
////                getSupportFragmentManager().popBackStack();
//
        }
           else if(tag.equals("myHome")){
                finish();
            }
            else if(tag.equals("myFrag")){
                FragmentTransaction t2 = mgr.beginTransaction();
             t2.replace(R.id.Frag_Lay,my_Home,"myHome");
             t2.commit();
            }

         else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//
//        if (id == R.id.HistoryOnMaps) {
////            mapList = new MapHistoryFragment();
////            FragmentTransaction tr2 = mgr.beginTransaction();
////            tr2.replace(R.id.Frag_Lay, mapList ,"myHistory").addToBackStack("mapHistory");
////            tr2.commit();
////            // Handle the camera action
//        }
        if (id == R.id.tripHestory) {

            myList = new List();
            FragmentTransaction tr1 = mgr.beginTransaction();
            tr1.replace(R.id.Frag_Lay, myList, "myFrag");
            tag="myFrag";
            tr1.commit();

        } else if (id == R.id.Home) {
            FragmentTransaction t = mgr.beginTransaction();
            t.replace(R.id.Frag_Lay, my_Home, "myHome");
            tag="myHome";

            t.commit();

        } else if (id == R.id.SignOut) {
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            setSharedPreferencessNull();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getFacebookSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(FACEBOOK_SHAREDPREFERENCES, MODE_PRIVATE);
        user_id = sharedPreferences.getString("id", "");
        profile = sharedPreferences.getString("profile", "");

        if (!profile.equals("")) {
            try {
                response = new JSONObject(profile);

                myEmail.setText(response.get("email").toString());
                myName.setText(response.get("name").toString());
                profile_pic_data = new JSONObject(response
                        .get("picture").toString());
                profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                Picasso.with(this).load(profile_pic_url.getString("url")).into(Profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public void setFacebookSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences(FACEBOOK_SHAREDPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", user_id);
        editor.putString("profile", response.toString());
        editor.apply();

        try {
            response = new JSONObject(profile);
            myEmail.setText(response.get("email").toString());
            myName.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response
                    .get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url")).into(Profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setEmailSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(EMAIL_SHAREDPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", user_id);
        editor.putString("email", emailShared);
        editor.apply();
        try {
            myEmail.setText(emailShared);
            Profile.setImageResource(R.mipmap.image);
        } catch (Exception e) {

        }

    }

    public void getEmailSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(EMAIL_SHAREDPREFERENCES, MODE_PRIVATE);
        user_id = sharedPreferences.getString("id", "");
        emailShared = sharedPreferences.getString("email", "");


        if (!emailShared.equals("")) {
            myEmail.setText(emailShared);
            Profile.setImageResource(R.mipmap.image);
        }
    }


    public void setSharedPreferencessNull() {
        SharedPreferences sharedPreferences = getSharedPreferences(EMAIL_SHAREDPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString("id", null);
        editer.putString("email", null);
        editer.apply();

        SharedPreferences faceShared = getSharedPreferences(FACEBOOK_SHAREDPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editerFace = faceShared.edit();
        editerFace.putString("id", null);
        editerFace.putString("profile", null);
        editerFace.apply();
    }

//    public void checkLocationDialog(){
//        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            // notify user
//            final AlertDialog.Builder askPermission = new AlertDialog.Builder(this);
//            askPermission.setTitle(R.string.ask_permission_title);
//            askPermission.setCancelable(false);
//            askPermission.setIcon(R.mipmap.alarm);
//            askPermission.setMessage(R.string.ask_permission_messege);
//            askPermission.setPositiveButton(R.string.ask_permission_positive, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                }
//            });
//            askPermission.setNegativeButton(R.string.ask_permission_negative, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    askPermission.setCancelable(false);
//                }
//            });
//            askPermission.create();
//            askPermission.show();
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(this)) {
                askOverlayPermission();
            }

        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{android.Manifest.permission.SYSTEM_ALERT_WINDOW},
//                            3);
                    Toast.makeText(MainActivity.this, "Permission denied..", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }


    public void askOverlayPermission (){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false)
                .setTitle("Ask Permission")
                .setMessage("please enable overlay permission so you can see notes when you start a trip on google maps")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                        startActivity(myIntent);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }


}
