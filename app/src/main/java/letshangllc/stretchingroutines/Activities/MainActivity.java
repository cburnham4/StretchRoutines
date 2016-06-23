package letshangllc.stretchingroutines.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.Routines;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.adapaters.RoutineListAdapter;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;

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

        final Routines routines = new Routines();
        routineItems = routines.getRoutines();

        routineListAdapter = new RoutineListAdapter(this, routineItems);

        listView.setAdapter(routineListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Intent intent = new Intent(MainActivity.this, StretchActivity.class);

                RoutineItem item = routineListAdapter.getItem(position);

                intent.putExtra(getString(R.string.routine_index_intent), item.id);
                startActivity(intent);
            }
        });

        addRoutinesFromDB();

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

    public void addRoutinesFromDB(){
        StretchesDBHelper stretchesDBHelper = new StretchesDBHelper(this);
        SQLiteDatabase sqLiteDatabase = stretchesDBHelper.getReadableDatabase();

        String[] projection = {DBTableConstants.ROUTINE_NAME, DBTableConstants.ROUTINE_IMAGE,
            DBTableConstants.ROUTINE_ID
        };

        Cursor c = sqLiteDatabase.query(DBTableConstants.ROUTINE_TABLE_NAME, projection, null,
                null, null, null, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String routineName = c.getString(0);
            Log.i(TAG, routineName);
            byte[] bytes = c.getBlob(1);

            int id = c.getInt(2);
            if(bytes == null){
                routineItems.add(new RoutineItem(id,R.drawable.silhouette, routineName));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                routineItems.add(new RoutineItem(id, bitmap, routineName));
            }
            c.moveToNext();
        }
        routineListAdapter.notifyDataSetChanged();;
        c.close();
        sqLiteDatabase.close();
    }

    public void createRoutineOnClick(View view){
        Intent intent = new Intent(this, CreateRoutineActivity.class);
        startActivity(intent);
    }

}
