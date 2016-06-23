package letshangllc.stretchingroutines.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.adapaters.StretchesAdapter;
import letshangllc.stretchingroutines.dialogs.AddStretchDialog;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;

public class CreateRoutineActivity extends AppCompatActivity {
    private static final String TAG = CreateRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* ListView for the stretches */
    private ListView lvStretches;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;

    /* Routine Name EditText */
    private EditText etRoutineName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        this.setupToolbar();

        stretches = new ArrayList<>();

        stretchesDBHelper = new StretchesDBHelper(this);
        this.setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Create Routine");

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupViews() {
        lvStretches = (ListView) findViewById(R.id.lvStretches);

        stretchesAdapter = new StretchesAdapter(this, stretches);

        lvStretches.setAdapter(stretchesAdapter);

        etRoutineName = (EditText) findViewById(R.id.etRoutineName);
    }

    public void addStretchOnClick(View view) {
        AddStretchDialog addStretchDialog = new AddStretchDialog();

        addStretchDialog.setCallback(new AddStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description, Bitmap bitmap) {
                Stretch stretch = new Stretch(name, description, bitmap, duration);
                stretches.add(stretch);
                stretchesAdapter.notifyDataSetChanged();
            }
        });

        addStretchDialog.show(getSupportFragmentManager(), TAG);
    }

    /* todo store routine image */
    public void storeRoutineData(){
        String routineName = etRoutineName.getText().toString();
        if(routineName.isEmpty()){
            Toast.makeText(this, "Routine name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        int routineId = this.storeRoutine(routineName);

        addStretchesToDatabase(routineId);
        finish();
    }

    public int storeRoutine(String routineName){
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBTableConstants.ROUTINE_NAME, routineName);

        db.insert(DBTableConstants.ROUTINE_TABLE_NAME, null, cv);

        db.close();

        return this.getRoutineId();
    }

    public int getRoutineId(){
        SQLiteDatabase db = stretchesDBHelper.getReadableDatabase();

        String sql = "SELECT MAX("+ DBTableConstants.ROUTINE_ID +") " +
                "FROM " + DBTableConstants.ROUTINE_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);

        c.moveToFirst();

        int id = c.getInt(0);

        c.close();
        db.close();

        return id;
    }

    public void addStretchesToDatabase(int routineId) {
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        /* Add each stretch to the routine */
        for (Stretch stretch : stretches) {
            byte[] bytes = null;
            if (stretch.bitmap != null) {
                bytes = DbBitmapUtility.getBytes(stretch.bitmap);
            }

            /* Insert stretch into db */
            ContentValues cv = new ContentValues();
            cv.put(DBTableConstants.STRETCH_NAME, stretch.getName());
            cv.put(DBTableConstants.STRETCH_IMAGE, bytes);
            cv.put(DBTableConstants.STRETCH_DURATION, stretch.getTime());
            cv.put(DBTableConstants.STRETCH_INSTRUCTION, stretch.getInstructions());
            db.insert(DBTableConstants.STRETCH_TABLE_NAME, null, cv);

            int stretchId = getStretchId();

            /* Insert routine Id and stretch ID into db */
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBTableConstants.ROUTINE_ID, routineId);
            contentValues.put(DBTableConstants.STRETCH_ID, stretchId);
            db.insert(DBTableConstants.ROUTINE_STRETCH_TABLE, null, contentValues);
        }
        db.close();
    }

    public int getStretchId(){
        SQLiteDatabase db = stretchesDBHelper.getReadableDatabase();

        String sql = "SELECT MAX("+ DBTableConstants.STRETCH_ID +") " +
                "FROM " + DBTableConstants.STRETCH_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);

        c.moveToFirst();

        int id = c.getInt(0);

        c.close();
        db.close();

        return id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_create_routine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.complete:
                storeRoutineData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* todo confirmation cancel alert box */


}
