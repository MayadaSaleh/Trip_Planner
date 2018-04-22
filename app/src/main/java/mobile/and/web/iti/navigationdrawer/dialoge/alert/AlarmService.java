package mobile.and.web.iti.navigationdrawer.dialoge.alert;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import mobile.and.web.iti.navigationdrawer.R;


public class AlarmService extends Service {

    private MediaPlayer player;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.   vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            vibrator.vibrate(2000);
        }
        vibrator.vibrate(2000);
        player = MediaPlayer.create(this, R.raw.alarmtone);
        player.setLooping(true);
        player.start();
        // start sticky means service will be explicity started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
