package letshangllc.stretchingroutines.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.model.Data.RoutineStretches;
import letshangllc.stretchingroutines.model.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;

public class StretchActivity extends AppCompatActivity {
    private static final String TAG = StretchActivity.class.getSimpleName();

    private TextView tv_stretchName;
    private TextView tv_instructions;
    private ImageView img_stretch;
    private TextView tv_timer;
    private int currentStretch;

    private CountDownTimer countDownTimer;


    /* Alarm */
    private Ringtone r;

    private ArrayList<Stretch> stretches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        Intent intent = getIntent();
        int routineId = intent.getIntExtra(getString(R.string.routine_index_intent), 0);

        /* todo Add routine index */
        switch (routineId){
            case 99999:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.backStretches));
                break;
            case 99998:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.morningStretches));
                break;
            case 99997:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.fullBodyStretches));
                break;
            case 99996:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.legStretches));
                break;
            case 99995:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.beforeBedStretches));
                break;
            default:
                stretches = new ArrayList<>();
        }

        this.findViews();
        currentStretch = 0;


        startStretches();

        this.runAds();
    }

    private void findViews(){
        tv_stretchName = (TextView) findViewById(R.id.tv_stretchName);
        tv_instructions = (TextView) findViewById(R.id.tv_instructions);
        img_stretch = (ImageView) findViewById(R.id.img_stretchPhoto);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
    }

    private void startStretches(){
        if(currentStretch >= stretches.size()){
            finish();
            return;
        }
        Stretch stretch = stretches.get(currentStretch);
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        if(stretch.bitmap != null){
            img_stretch.setVisibility(View.VISIBLE);
            img_stretch.setImageBitmap(stretch.bitmap);
        }else if(stretch.getDrawableIndex() == 0){
            img_stretch.setVisibility(View.INVISIBLE);
        }else{
            img_stretch.setVisibility(View.VISIBLE);
            img_stretch.setImageDrawable(ContextCompat.getDrawable(this, stretch.getDrawableIndex()));
        }


        //tv_timer.setText(stretch.getDuration() +"");
        startSwitchCountdown(stretch.getDuration()*1000);
    }

    /* TODO: Countdown to next exercise */
    public void nextExerciseOnClick(View view){
        countDownTimer.cancel();
        currentStretch++;
        startStretches();
    }

    private void startSwitchCountdown(final int stretchDuration){
        countDownTimer = new CountDownTimer(5000, 250) {

            public void onTick(long millisUntilFinished) {
                int time = (int) Math.round(Math.ceil(millisUntilFinished * 0.001f));
                Log.i(TAG, String.format(Locale.getDefault(), "%d" , time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d" ,time));
            }

            public void onFinish() {
                startStretchCountdown(stretchDuration);
            }
        };
        countDownTimer.start();
    }

    private void startStretchCountdown(int stretchDuration){
        countDownTimer = new CountDownTimer(stretchDuration, 250) {

            public void onTick(long millisUntilFinished) {
                int time = Math.round(millisUntilFinished * 0.001f);
                Log.i(TAG, String.format(Locale.getDefault(), "%d" ,time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d" ,time));
            }

            public void onFinish() {
                playAlarm();
                currentStretch++;
                startStretches();

            }
        };
        countDownTimer.start();
    }

//

    private void playAlarm(){
        /* Get the alarm tone */
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // If notication is null the use ringtone
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            /* Play the alarm */
            r = RingtoneManager.getRingtone(this, alert);
            //r.setStreamType(AudioManager.STREAM_ALARM);
            r.play();
        }catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    r.stop();
                } catch (Exception e) {
                    r.stop();
                }

            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        finish();
    }


    private AdsHelper adsHelper;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            adsHelper.refreshAd();
      /* and here comes the "trick" */
            handler.postDelayed(this, getResources().getInteger(R.integer.ad_refresh_rate));
        }
    };


    public void runAds(){
        adsHelper =  new AdsHelper(this.getWindow().getDecorView(), getResources().getString(R.string.admob_id_stretches), this);
        adsHelper.setUpAds();
        handler.postDelayed(runnable, 100);
    }


}
