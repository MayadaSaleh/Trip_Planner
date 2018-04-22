package mobile.and.web.iti.navigationdrawer.maps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.and.web.iti.navigationdrawer.R;

public class ShowingMapHistoryImg extends Activity {
    ImageView imggg;
    TextView v;
    TextView distnc;
    TextView durat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_map_history_img);

        Intent inttt=getIntent();
        String url3=inttt.getStringExtra("message");

        Log.i("a","7");

        Double durt=inttt.getDoubleExtra("drr",0);
        String stringdouble1= Double.toString(durt);
        Double vel=inttt.getDoubleExtra("vvv",0);
        String stringdouble2= Double.toString(vel);
        Double dstnc=inttt.getDoubleExtra("dstn",0);
        String stringdouble3= Double.toString(dstnc);

        String durunit=inttt.getStringExtra("durUnite");
        String distnUnit=inttt.getStringExtra("dstnUnite");
        Log.i("a","8");

        Log.i("url","showing");

        v=findViewById(R.id.velc);
        distnc=findViewById(R.id.dstn);
        durat=findViewById(R.id.durat);
        Log.i("a","9");

v.setText("Velocity"+stringdouble2 +distnUnit+"/"+durunit);
distnc.setText("Trip Distance"+stringdouble3 +distnUnit);
durat.setText("Trip duration"+stringdouble1+durunit);

        Log.i("a","10");

        Bitmap myBitmap = BitmapFactory.decodeFile(url3);
        imggg=findViewById(R.id.maphistimg);
        imggg.setImageBitmap(myBitmap);


    }
}
