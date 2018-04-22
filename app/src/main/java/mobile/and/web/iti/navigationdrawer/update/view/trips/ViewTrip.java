package mobile.and.web.iti.navigationdrawer.update.view.trips;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.database.DatabaseAdapter;

public class ViewTrip extends AppCompatActivity {
    TextView trName ;
    TextView from ;
    TextView to ;
    TextView date ;
    TextView time ;
    ImageView img ;
    String id ;
    ListView myList ;
    ArrayAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        trName = findViewById(R.id.edtTripName);
        from = findViewById(R.id.From);
        to = findViewById(R.id.To);
        date = findViewById(R.id.edtTripTime);
        time = findViewById(R.id.edtTripDate);
        img = findViewById(R.id.ViewImage);
        myList = findViewById(R.id.myList);





        Intent intent = getIntent();
        trName.setText(intent.getStringExtra("name"));
        from.setText(intent.getStringExtra("from"));
        to.setText(intent.getStringExtra("to"));
        date.setText(intent.getStringExtra("date"));
        time.setText(intent.getStringExtra("time"));
        if(getIntent().hasExtra("byteArray")) {
            if(getIntent().getByteArrayExtra("byteArray") != null) {
                Bitmap _bitmap = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
                img.setImageBitmap(_bitmap);
            }else{
                img.setImageResource(R.mipmap.image);
            }
                id = intent.getStringExtra("id");
                DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());

                String[] myNotes = db.getNotesById(id);

                if(myNotes != null)
                    adapter = new ArrayAdapter(getApplicationContext(), R.layout.notelist, R.id.chkdText, myNotes);
                else
                    adapter = new ArrayAdapter(getApplicationContext(), R.layout.notelist, R.id.chkdText, new ArrayList());

                myList.setAdapter(adapter);




        }



    }
}
