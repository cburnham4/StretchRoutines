package letshangllc.stretchingroutines.controller.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.controller.adapaters.RoutineListAdapter;
import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.api.APIRequests;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private ArrayList<Routine> routines;
    private RoutineListAdapter routineListAdapter;
    private ProgressDialog progressDialog;

    private AdsHelper adsHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (adsHelper != null) {
                adsHelper.refreshAd();
            }
            handler.postDelayed(this, getResources().getInteger(R.integer.ad_refresh_rate));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySystemBarInsets();

        FirebaseApp.initializeApp(this);

        getRoutines();

        listView = findViewById(R.id.lv_routines);
        registerForContextMenu(listView);
        this.runAds();
    }

    private void applySystemBarInsets() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View content = ((ViewGroup) v).getChildAt(0);
            if (content != null) {
                content.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            }
            return windowInsets;
        });
    }

    public void getRoutines() {
        progressDialog = ProgressDialog.show(this, "", "Loading Routines...", true);
        APIRequests.getRoutines(routines -> {
            MainActivity.this.routines = routines;
            setupRoutineList();
        });
    }

    private void setupRoutineList() {
        routineListAdapter = new RoutineListAdapter(this, routines);

        listView.setAdapter(routineListAdapter);

        listView.setOnItemClickListener((AdapterView<?> a, View v, int position, long id) -> {
            Log.i(TAG, "Item Clicked");
            Intent intent = new Intent(MainActivity.this, StretchActivity.class);
            Routine item = routineListAdapter.getItem(position);
            intent.putExtra(getString(R.string.routine_index_intent), item);
            startActivity(intent);
        });

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Bound from activity_main.xml (button currently gone). */
    public void createRoutineOnClick(View view) {
        // Custom routine creation is not wired in this release.
    }

    private void runAds() {
        adsHelper = new AdsHelper(
                getWindow().getDecorView(),
                getString(R.string.admob_id_routines),
                this
        );
        adsHelper.setUpAds();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adsHelper != null) {
            adsHelper.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }
}
