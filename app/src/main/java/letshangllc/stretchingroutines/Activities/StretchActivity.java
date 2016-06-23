package letshangllc.stretchingroutines.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.RoutineStretches;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;

public class StretchActivity extends AppCompatActivity {
    private TextView tv_stretchName;
    private TextView tv_instructions;
    private ImageView img_stretch;
    private TextView tv_timer;
    private int currentStretch;

    private AdsHelper adsHelper;

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
            default:
                stretches = new ArrayList<>();
        }

        getStretches(routineId);


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
        if(currentStretch == stretches.size()){
            finish();
            return;
        }
        Stretch stretch = stretches.get(currentStretch);
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        if(stretch.bitmap != null){
            img_stretch.setImageBitmap(stretch.bitmap);
        }else if(stretch.getDrawableIndex() == 0){
            img_stretch.setVisibility(View.INVISIBLE);
        }else{
            img_stretch.setImageDrawable(ContextCompat.getDrawable(this, stretch.getDrawableIndex()));
        }


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

    public void getStretches(int routineId){
        StretchesDBHelper stretchesDBHelper = new StretchesDBHelper(this);
        SQLiteDatabase db = stretchesDBHelper.getReadableDatabase();
         /* Query the db to get the muscle data */

        String sql = "SELECT " + DBTableConstants.STRETCH_NAME +", " + DBTableConstants.STRETCH_DURATION +
                ", " + DBTableConstants.STRETCH_INSTRUCTION + ",  " + DBTableConstants.STRETCH_IMAGE +
                " FROM " + DBTableConstants.STRETCH_TABLE_NAME +
                " INNER JOIN " + DBTableConstants.ROUTINE_STRETCH_TABLE +
                " ON " + DBTableConstants.STRETCH_TABLE_NAME + "." + DBTableConstants.STRETCH_ID +
                " = " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.STRETCH_ID +
                " WHERE " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.ROUTINE_ID +
                " = " + routineId;


        Cursor c = db.rawQuery(sql, null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            String name = c.getString(0);
            int duration = c.getInt(1);
            String instruction = c.getString(2);
            byte[] bytes = c.getBlob(3);
            if(bytes == null){
                stretches.add(new Stretch(name, instruction, duration));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                stretches.add(new Stretch(name, instruction, bitmap, duration));
            }

            c.moveToNext();
        }

        c.close();
        db.close();
    }
}
