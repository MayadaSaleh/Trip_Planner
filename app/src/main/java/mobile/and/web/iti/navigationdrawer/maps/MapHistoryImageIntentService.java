package mobile.and.web.iti.navigationdrawer.maps;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapHistoryImageIntentService extends IntentService {
    ByteArrayOutputStream bytearrayoutputstream;
    FileOutputStream fileoutputstream;
    File file;

    public MapHistoryImageIntentService() {
        super("MyIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bitmap b = null;
            URL url;
            HttpURLConnection con;
            InputStream is;
            bytearrayoutputstream = new ByteArrayOutputStream();


            try {
                Log.i("url","intent");
                String res = intent.getStringExtra("SentURL");
                String durUnit=intent.getStringExtra("durationUnit");
                String distnUnit=intent.getStringExtra("distanceUnit");
                Double v=intent.getDoubleExtra("velocity",0);
                Double dr=intent.getDoubleExtra("duration",0);
                Double dst=intent.getDoubleExtra("distance",0);
                Log.i("a","5");

                url = new URL(res);
                con = (HttpURLConnection) url.openConnection();
                is = con.getInputStream();
                b = BitmapFactory.decodeStream(is);
                b.compress(Bitmap.CompressFormat.PNG, 60, bytearrayoutputstream);

                file = new File(Environment.getExternalStorageDirectory(), "MapHistoryImg.png");
                try {
                    //file.createNewFile();
                    Log.i("url","intent2");
                    fileoutputstream = new FileOutputStream(file);
                    fileoutputstream.write(bytearrayoutputstream.toByteArray());
                    fileoutputstream.close();
                    Log.i("url","intent3");
                    Intent intent2 = new Intent(this, ShowingMapHistoryImg.class);
                    intent2.setAction("com.example.SendBroadCast");
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra("message", file.getAbsolutePath());
                    intent2.putExtra("vvv",v);
                    intent2.putExtra("drr",dr);
                    intent2.putExtra("dstn",dst);
                    intent2.putExtra("durUnite",durUnit);
                    intent2.putExtra("dstnUnite",distnUnit);
                    Log.i("a","6");

                    // Log.i("url","intent4");
                    startActivity(intent2);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(con, "", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
