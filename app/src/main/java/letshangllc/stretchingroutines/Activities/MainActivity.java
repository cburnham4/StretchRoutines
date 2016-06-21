package letshangllc.stretchingroutines.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.Data.Routines;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.RoutineListAdapter;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private ArrayList<RoutineItem> routineItems;
    private RoutineListAdapter routineListAdapter;

    private AdsHelper adsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv_routines);

        Routines routines = new Routines();
        routineItems = routines.getRoutines();

        routineListAdapter = new RoutineListAdapter(this, routineItems);

        listView.setAdapter(routineListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Intent intent = new Intent(MainActivity.this, StretchActivity.class);

                intent.putExtra(getString(R.string.routine_index_intent), position);
                startActivity(intent);
            }
        });

        adsHelper =  new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_id_routines), this);

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

    public void createRoutineOnClick(View view){
        Intent intent = new Intent(this, CreateRoutineActivity.class);
        startActivity(intent);
    }

}
