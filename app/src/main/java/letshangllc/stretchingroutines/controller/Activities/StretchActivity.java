package letshangllc.stretchingroutines.controller.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.model.api.APIRequests;

public class StretchActivity extends AppCompatActivity {
    private static final String TAG = StretchActivity.class.getSimpleName();

    /* Views */
    private TextView tv_stretchName, tv_instructions, tvStretchCount, tv_timer;
    private ImageView img_stretch;
    private ProgressDialog progressDialog;

    private CountDownTimer countDownTimer;
    private int currentStretchIndex;
    private boolean isPaused;

    /* Alarm */
    private Ringtone r;

    private ArrayList<Stretch> stretches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        /* Load stretches */
        Intent intent = getIntent();
        Routine routine = intent.getParcelableExtra(getString(R.string.routine_index_intent));
        loadStretches(routine);

        this.findViews();
        currentStretchIndex = 0;




        this.runAds();
    }

    private void loadStretches(Routine routine){
        progressDialog = ProgressDialog.show(this, "Loading Stretches", "Please wait...", true);
        APIRequests.getStretches(routine, new APIRequests.StretchListener() {
            @Override
            public void success(ArrayList<Stretch> stretches) {
                StretchActivity.this.stretches = stretches;
                progressDialog.dismiss();
                startStretches();
            }

        });
    }

    private void findViews(){
        tv_stretchName = (TextView) findViewById(R.id.tv_stretchName);
        tv_instructions = (TextView) findViewById(R.id.tv_instructions);
        img_stretch = (ImageView) findViewById(R.id.img_stretchPhoto);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tvStretchCount = (TextView) findViewById(R.id.tvStretchCount);
    }

    private void startStretches(){
        if(currentStretchIndex >= stretches.size()){
            finish();
            return;
        }
        isPaused = false;
        Stretch stretch = stretches.get(currentStretchIndex);
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        String stretchCount = (currentStretchIndex+1) + " / " +stretches.size();
        tvStretchCount.setText(stretchCount);

        Glide.with(this).load(stretch.downloadURL).into(img_stretch);

        //tv_timer.setText(stretch.getDuration() +"");
        startSwitchCountdown(stretch.time*1000);
    }

    /* TODO: Countdown to next exercise */
    public void nextExerciseOnClick(View view){
        countDownTimer.cancel();
        currentStretchIndex++;
        startStretches();
    }

    private void startSwitchCountdown(final int stretchDuration){
        countDownTimer = new CountDownTimer(3000, 250) {

            public void onTick(long millisUntilFinished) {
                int time = (int) Math.round(Math.ceil(millisUntilFinished * 0.001f));
                Log.i(TAG, String.format(Locale.getDefault(), "%d" , time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d" ,time));
                StretchActivity.this.millisUntilFinished = millisUntilFinished;
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
                StretchActivity.this.millisUntilFinished = millisUntilFinished;
            }

            public void onFinish() {
                playAlarm();
                currentStretchIndex++;
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

    private long millisUntilFinished = 0;
    public void pauseExercise(View view){
        Log.i(TAG, " Pause");
        if(isPaused){
            countDownTimer.start();
            ((Button) view).setText("PAUSE");

        }else{
            if(countDownTimer!=null){
                ((Button) view).setText("RESUME");
                countDownTimer.cancel();

            }

        }
        isPaused = !isPaused;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }

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
