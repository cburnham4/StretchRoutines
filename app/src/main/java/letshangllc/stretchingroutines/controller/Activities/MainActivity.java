package letshangllc.stretchingroutines.controller.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.Data.Routines;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.model.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.controller.adapaters.RoutineListAdapter;
import letshangllc.stretchingroutines.model.api.APIRequests;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private ArrayList<Routine> routines;
    private RoutineListAdapter routineListAdapter;
    private ProgressDialog progressDialog;

    /* TODO: Add in between time */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        getRoutines();

        listView = (ListView) findViewById(R.id.lv_routines);
        registerForContextMenu(listView);
        this.runAds();

    }

    public void getRoutines(){
        progressDialog = ProgressDialog.show(this, "", "Loading Routines...", true);
        APIRequests.getRoutines(new APIRequests.RoutineListener() {
            @Override
            public void success(ArrayList<Routine> routines) {
                MainActivity.this.routines = routines;

                setupRoutineList();
            }
        });
    }

    private void setupRoutineList(){
        routineListAdapter = new RoutineListAdapter(this, routines);

        listView.setAdapter(routineListAdapter);
        //addRoutinesFromDB();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Log.i(TAG, "Item Clicked");
                Intent intent = new Intent(MainActivity.this, StretchActivity.class);

                Routine item = routineListAdapter.getItem(position);

                intent.putExtra(getString(R.string.routine_index_intent), item);
                startActivity(intent);
            }
        });

        try{
            progressDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /* ADS */
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


    private void runAds(){
        adsHelper =  new AdsHelper(this.getWindow().getDecorView(), getResources().getString(R.string.admob_id_routines), this);
        adsHelper.setUpAds();
        handler.post(runnable);
    }




}
