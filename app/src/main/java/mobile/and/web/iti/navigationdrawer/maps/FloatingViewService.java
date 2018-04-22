package mobile.and.web.iti.navigationdrawer.maps;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;

public class FloatingViewService extends Service  {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private  View mFloatingCircle;
    private String event_id;
    ArrayAdapter adapter;
     View expandedView;
     View collapsedView;

    public FloatingViewService() {

    }




    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        mFloatingCircle = LayoutInflater.from(this).inflate(R.layout.circle , null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-right corner
//        params.x = 590;
//        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

/////////////////////////////////////////////cirlcle////////////////////
        final WindowManager.LayoutParams circleParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        circleParams.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-right corner
        circleParams.x = 300;
        circleParams.y = 852;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingCircle, circleParams);


        //The root element of the collapsed view layout
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
       expandedView = mFloatingView.findViewById(R.id.expanded_container);
        //the root element of circle view
        final View circleView = mFloatingCircle.findViewById(R.id.circle_container);

        circleView.setVisibility(View.GONE);


//        //Set the close button
//        ImageView closeButtonCollapsed =  mFloatingView.findViewById(R.id.close_btn);
//        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //close the service and remove the from from the window
//                stopSelf();
//            }
//        });


        ImageView notes = mFloatingView.findViewById(R.id.collapsed_iv);
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);

            }
        });

        ///////////////hna hashel el text da w ha7ot el list///////////////////


//       Log.i("nela",event_id+"");
//        TextView genNote = mFloatingView.findViewById(R.id.Notes);
//        genNote.setText(event_id);
//        genNote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                expandedView.setVisibility(View.GONE);
//
//
//            }
//        });

        mFloatingView.findViewById(R.id.collapse_view
        ).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        Log.i("nela",initialX+"awl x");
                        Log.i("nela",initialY+"awl y");

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.i("nela",initialTouchX+"awl ma msk el  x");
                        Log.i("nela",initialTouchY+"awl ma msk y");

                        return true;

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() + initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.

//                                collapsedView.setVisibility(View.GONE);
//                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;


                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                         params.x = initialX + (int) (event.getRawX() );
//                         params.y = initialY + (int) (event.getRawY() );
                        Log.i("nela",params.x+"a5r 7aga x");
                        Log.i("nela",params.y+"aa5r 7aga y");
                        //Update the layout with new X & Y coordinate

                        mWindowManager.updateViewLayout(mFloatingView, params);

                        /////////if the floating point reach a specific position in the screen it will disappear //////////
                        if(params.x > 24 && params.y > 660 ){

                            circleView.setVisibility(View.VISIBLE);

                            if( ( params.x > 220 && params.x < 400) && (params.y > 780 && params.y <852)  ){

                                circleView.setVisibility(View.GONE);
                                stopSelf();


                            }

                        }
                        else{
                            circleView.setVisibility(View.GONE);


                        }


                        return true;
                }
                return false;
            }
        });





    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        event_id = (String) intent.getExtras().get("id");
//        Log.i("nela",event_id+"");
//
//        Log.i("nela",event_id+"");

        DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());

        String [] myNotes = db.getNotesById(event_id);

        if(myNotes != null)
         adapter = new ArrayAdapter(getApplicationContext(), R.layout.notelist, R.id.chkdText , myNotes);
        else
            adapter = new ArrayAdapter(getApplicationContext(), R.layout.notelist, R.id.chkdText , new ArrayList());

        ListView genNote = mFloatingView.findViewById(R.id.Notes);

        genNote.setAdapter(adapter);

        genNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                expandedView.setVisibility(View.GONE);
                collapsedView.setVisibility(View.VISIBLE);


                Log.i("nela","das 3leha");

                return false;
            }
        });



//
//        genNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });



        return super.onStartCommand(intent, flags, startId);
    }










    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);

    }

//    public void getNote(String event_id){
//        DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
//        String [] myNotes = db.getNotesById(event_id);
//        for(String note : myNotes){
//            Log.i("Notessssssssss" , note+"");
//        }
//    }



}
