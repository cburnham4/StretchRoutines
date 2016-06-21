package letshangllc.stretchingroutines.Activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.Data.RoutineStretches;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;

public class StretchActivity extends AppCompatActivity {
    private TextView tv_stretchName;
    private TextView tv_instructions;
    private ImageView img_stretch;
    private TextView tv_timer;
    private int currentStretch;

    private AdsHelper adsHelper;

    private Stretch[] stretches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        Intent intent = getIntent();
        int routineIndex = intent.getIntExtra(getString(R.string.routine_index_intent), 0);

        /* todo Add routine index */
        switch (routineIndex){
            case 0:
                stretches =RoutineStretches.backStretches;
                break;
            case 1:
                stretches = RoutineStretches.morningStretches;
                break;
            case 2:
                stretches = RoutineStretches.fullBodyStretches;
                break;
        }


        this.findViews();
        currentStretch = 0;


        startStretches();

        adsHelper =  new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_id_stretches), this);

        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);
    }

    private void findViews(){
        tv_stretchName = (TextView) findViewById(R.id.tv_stretchName);
        tv_instructions = (TextView) findViewById(R.id.tv_instructions);
        img_stretch = (ImageView) findViewById(R.id.img_stretchPhoto);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
    }
    private void startStretches(){
        if(currentStretch == stretches.length){
            finish();
            return;
        }
        Stretch stretch = stretches[currentStretch];
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        img_stretch.setImageDrawable(ContextCompat.getDrawable(this, stretch.getDrawableIndex()));
        //tv_timer.setText(stretch.getTime() +"");
        startCountdown(stretch.getTime()*1000);
    }

    private void startCountdown(int timer){
        new CountDownTimer(timer, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
               tv_timer.setText("Done!");
                currentStretch++;
                startStretches();
            }
        }.start();
    }
}
