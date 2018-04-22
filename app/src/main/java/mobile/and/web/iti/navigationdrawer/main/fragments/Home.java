package mobile.and.web.iti.navigationdrawer.main.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;
import mobile.and.web.iti.navigationdrawer.main.fragments.adapters.MyAdapter;

import static android.content.Context.MODE_PRIVATE;
import static mobile.and.web.iti.navigationdrawer.add.trip.AddTripActivity.DIRECTORY_NAME;


public class Home extends Fragment {
    View myView ;
    RecyclerView myRecycler ;
    RecyclerView.Adapter myAdapter ;
    RecyclerView.LayoutManager myLayoutMgr;
    private FirebaseAuth mAuth;
    private String user_id ="";
    private FirebaseUser user;
    private ArrayList<Trip_DTO> tripDtos;
    private String profile;

    public Home(){

    }

//    public Home(String userId) {
//
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
        }
//        Toast.makeText(getContext(), "From Home user id = " + user_id, Toast.LENGTH_SHORT).show();
        myView=inflater.inflate(R.layout.fragment_home, container, false);
        myRecycler = myView.findViewById(R.id.my_recycler);
        myLayoutMgr = new GridLayoutManager(getContext(),2);
        myRecycler.setLayoutManager(myLayoutMgr);
        myRecycler.setItemAnimator(new DefaultItemAnimator());
        myRecycler.addItemDecoration(new SpacesItemDecoration(15));

//        Cake c1 = new Cake(R.mipmap.one,"cheese cake");
//        Cake c2 = new Cake(R.mipmap.three,"strawbry cake");
//        Cake c3 = new Cake(R.mipmap.two,"banana cake");
//        Cake c4 = new Cake(R.mipmap.five,"chocolate cake");
//        Cake c5 = new Cake(R.mipmap.four,"tart cake");
//        Cake c6 = new Cake(R.mipmap.one,"cheese cake");
//        Cake c7 = new Cake(R.mipmap.two,"strawbry cake");
//        Cake c8 = new Cake(R.mipmap.three,"banana cake");
//        Cake c9 = new Cake(R.mipmap.one,"chocolate cake");
//        Cake c10 = new Cake(R.mipmap.three,"tart cake");
//        Cake c11 = new Cake(R.mipmap.four,"cheese cake");
//        Cake c12 = new Cake(R.mipmap.five,"strawbry cake");
//        Cake c13 = new Cake(R.mipmap.two,"banana cake");
//        Cake c14 = new Cake(R.mipmap.one,"chocolate cake");
//        Cake c15 = new Cake(R.mipmap.two,"tart cake");
//        Cake[] myCakeArr = {c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15};

//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        if (user != null) {
//            user_id = user.getUid();
//        }
//        if(user_id != null) {
            tripDtos = getTrips();

        if(tripDtos.size()>0) {
            ArrayList<Trip_DTO> tripsWithImage = setAllImagesFromStorage(tripDtos);

            for (int i = 0; i < tripsWithImage.size(); i++) {
                Log.i("atran", tripsWithImage.get(i).getEventId() + "");
            }

            myAdapter = new MyAdapter(getContext(), tripsWithImage);

            myRecycler.setAdapter(myAdapter);
        }

//        Button btnAdd = myView.findViewById(R.id.addTrip);
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.i("nela","d5l gwa el button");
//
//                Intent in = new Intent(getContext(),AddTripActivity.class);
//                startActivity(in);
//
//
//            }
//        });

//        }else{
//            Toast.makeText(getContext(), "user id = null", Toast.LENGTH_SHORT).show();
//        }


        return myView;
    }

    public ArrayList<Trip_DTO> getTrips(){
        ArrayList<Trip_DTO> trips = null;
        DatabaseAdapter adapter = new DatabaseAdapter(getContext());
        trips = adapter.retrieveUpcomingTrips(user_id);
//        Toast.makeText(getContext(), "trips size = "+ trips.size() + " , id = "+user_id, Toast.LENGTH_SHORT).show();
        if(trips.size()>0) {
            if (trips.get(0).getEventId() != null) {
                Log.i("nela elmota5alefa", trips.get(0).getEventId());
            } else {
                Log.i("nela elmota5alefa", "kan fe w 5eles");
            }
            return trips;
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




}
