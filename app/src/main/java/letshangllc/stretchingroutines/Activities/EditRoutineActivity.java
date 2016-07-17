package letshangllc.stretchingroutines.Activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.adapaters.StretchesAdapter;
import letshangllc.stretchingroutines.dialogs.AddStretchDialog;
import letshangllc.stretchingroutines.dialogs.EditStretchDialog;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;
import letshangllc.stretchingroutines.helpers.StoreRoutineInBackground;
import letshangllc.stretchingroutines.helpers.StoringRoutineComplete;
import letshangllc.stretchingroutines.helpers.UpdateRoutineInBackground;

public class EditRoutineActivity extends AppCompatActivity {
    private static final String TAG = EditRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* ListView for the stretches */
    private ListView lvStretches;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;

    /* Routine Name EditText */
    private EditText etRoutineName;

    /* Progress Dialog */
    private ProgressDialog progressDialog;

    /* Routine Id */
    private int routineId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        this.setupToolbar();

        Intent intent = getIntent();
        routineId = intent.getIntExtra(getString(R.string.routine_index_intent), 0);

        stretchesDBHelper = new StretchesDBHelper(this);

        this.getStretches();
        this.setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Edit Routine");

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDiscard();
            }
        });
    }

    public void setupViews() {
        lvStretches = (ListView) findViewById(R.id.lvStretches);

        registerForContextMenu(lvStretches);

        stretchesAdapter = new StretchesAdapter(this, stretches);

        lvStretches.setAdapter(stretchesAdapter);

        etRoutineName = (EditText) findViewById(R.id.etRoutineName);
    }

    public void getStretches(){
        stretches = new ArrayList<>();
        SQLiteDatabase db = stretchesDBHelper.getReadableDatabase();
         /* Query the db to get the muscle data */

        String sql = "SELECT *" +
                " FROM " + DBTableConstants.STRETCH_TABLE_NAME +
                " INNER JOIN " + DBTableConstants.ROUTINE_STRETCH_TABLE +
                " ON " + DBTableConstants.STRETCH_TABLE_NAME + "." + DBTableConstants.STRETCH_ID +
                " = " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.STRETCH_ID +
                " WHERE " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.ROUTINE_ID +
                " = " + routineId;


        Cursor c = db.rawQuery(sql, null);



        Log.i(TAG, "Query count: "+ c.getCount());

        c.moveToFirst();
        while(!c.isAfterLast()){
            int duration = c.getInt(c.getColumnIndex(DBTableConstants.STRETCH_DURATION));
            String instruction = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_INSTRUCTION));
            byte[] bytes = c.getBlob(c.getColumnIndex(DBTableConstants.STRETCH_IMAGE));
            String name = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_NAME));
            int id = c.getInt(c.getColumnIndex(DBTableConstants.STRETCH_ID));

            if(bytes == null){
                stretches.add(new Stretch(name, instruction, duration, null, id));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                stretches.add(new Stretch(name, instruction,duration, bitmap, id));
            }

            c.moveToNext();
        }

        c.close();
        db.close();
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

        Log.i(TAG, "Storing data");
        int routineId = this.updateRoutine(routineName);
        Log.i(TAG, "Stored routine");

        new UpdateRoutineInBackground(stretches, routineId, stretchesDBHelper, this, new StoringRoutineComplete() {
            @Override
            public void onDataStored() {
                Log.i(TAG, "Data stored");
                finish();
            }
        }).execute();

    }

    public int updateRoutine(String routineName){
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBTableConstants.ROUTINE_NAME, routineName);

        db.update(DBTableConstants.ROUTINE_TABLE_NAME, cv,
                DBTableConstants.ROUTINE_ID +" = "+ routineId, null);

        Log.i(TAG, "Routine ID: " + routineId);
        db.close();

        return routineId;
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

    private void confirmDiscard(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Routine?");

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Finish the exercise upon discarding */
                finish();
            }
        }).setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            case R.id.edit:
                editStretch(stretches.get((int) info.id));
                return true;
            case R.id.delete:
                stretches.remove((int) info.id);
                stretchesAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void editStretch(final Stretch stretch){
        EditStretchDialog editStretchDialog  = new EditStretchDialog();
        editStretchDialog.setCurrentStretch(stretch);

        editStretchDialog.setCallback(new EditStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description, Bitmap bitmap) {
                stretch.setName(name);
                stretch.setDuration(duration);
                stretch.setInstructions(description);
                stretch.bitmap = bitmap;
                stretchesAdapter.notifyDataSetChanged();
            }
        });
        editStretchDialog.show(getSupportFragmentManager(), TAG);
    }




}
