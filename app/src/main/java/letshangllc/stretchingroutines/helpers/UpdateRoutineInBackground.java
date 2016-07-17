package letshangllc.stretchingroutines.helpers;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.JavaObjects.Stretch;

/**
 * Created by Carl on 7/16/2016.
 */
public class UpdateRoutineInBackground extends AsyncTask<Void, Void, Void> {
    private static final String TAG = UpdateRoutineInBackground.class.getSimpleName();

    private ArrayList<Stretch> stretches;
    private int routineId;
    private StretchesDBHelper stretchesDBHelper;
    private Context context;
    private StoringRoutineComplete callback;

    private ProgressDialog dialog;

    public UpdateRoutineInBackground(ArrayList<Stretch> stretches, int routineId,
                                     StretchesDBHelper stretchesDBHelper, Context context,
                                     StoringRoutineComplete callback) {
        this.stretches = stretches;
        this.routineId = routineId;
        this.stretchesDBHelper = stretchesDBHelper;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Data");
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        callback.onDataStored();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        /* Add each stretch to the routine */
        for (Stretch stretch : stretches) {
            byte[] bytes = null;
            if (stretch.bitmap != null) {
                bytes = DbBitmapUtility.getBytes(stretch.bitmap);
            }
            SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();
            /* Insert stretch into db */
            ContentValues cv = new ContentValues();
            cv.put(DBTableConstants.STRETCH_NAME, stretch.getName());
            cv.put(DBTableConstants.STRETCH_IMAGE, bytes);
            cv.put(DBTableConstants.STRETCH_DURATION, stretch.getDuration());
            cv.put(DBTableConstants.STRETCH_INSTRUCTION, stretch.getInstructions());
            db.insertWithOnConflict(DBTableConstants.STRETCH_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            db.update(DBTableConstants.STRETCH_TABLE_NAME, cv,
                    DBTableConstants.STRETCH_ID + " = " + stretch.id, null);
        }
        Log.i(TAG, "Stored stretches");
        return null;
    }
}
