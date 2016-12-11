package letshangllc.stretchingroutines.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.Data.Routines;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.adapaters.RoutineListAdapter;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private ArrayList<RoutineItem> routineItems;
    private RoutineListAdapter routineListAdapter;

    /* TODO: Add in between time */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv_routines);
        registerForContextMenu(listView);
        //this.runAds();

    }

//    public void addRoutinesFromDB(){
//        StretchesDBHelper stretchesDBHelper = new StretchesDBHelper(this);
//        SQLiteDatabase sqLiteDatabase = stretchesDBHelper.getReadableDatabase();
//
//        String[] projection = {DBTableConstants.ROUTINE_NAME, DBTableConstants.ROUTINE_IMAGE,
//            DBTableConstants.ROUTINE_ID
//        };
//
//        Cursor c = sqLiteDatabase.query(DBTableConstants.ROUTINE_TABLE_NAME, projection, null,
//                null, null, null, null);
//        c.moveToFirst();
//
//        while(!c.isAfterLast()){
//            String routineName = c.getString(0);
//            Log.i(TAG, routineName);
//            byte[] bytes = c.getBlob(1);
//
//            int id = c.getInt(2);
//            if(bytes == null){
//                routineItems.add(new RoutineItem(id,R.drawable.silhouette, routineName));
//            }else{
//                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
//                routineItems.add(new RoutineItem(id, bitmap, routineName));
//            }
//            c.moveToNext();
//        }
//        routineListAdapter.notifyDataSetChanged();;
//        c.close();
//        sqLiteDatabase.close();
//    }



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
        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Routines routines = new Routines();
        routineItems = routines.getRoutines();

        routineListAdapter = new RoutineListAdapter(this, routineItems);

        listView.setAdapter(routineListAdapter);
        //addRoutinesFromDB();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Log.i(TAG, "Item Clicked");
                Intent intent = new Intent(MainActivity.this, StretchActivity.class);

                RoutineItem item = routineListAdapter.getItem(position);

                intent.putExtra(getString(R.string.routine_index_intent), item.id);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_edit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            default:
                return super.onContextItemSelected(item);
        }
    }

//



}
