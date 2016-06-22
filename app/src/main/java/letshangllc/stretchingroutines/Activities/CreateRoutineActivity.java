package letshangllc.stretchingroutines.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Locale;

import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.dialogs.AddStretchDialog;

public class CreateRoutineActivity extends AppCompatActivity {
    private static final String TAG = CreateRoutineActivity.class.getSimpleName();

    /* ListView for the stretches */
    private ListView lvStretches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);
        setupViews();
    }

    public void setupViews(){
        lvStretches = (ListView) findViewById(R.id.lvStretches);


    }

    public void addStretchOnClick(View view){
        AddStretchDialog addStretchDialog = new AddStretchDialog();

        addStretchDialog.setCallback(new AddStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description) {

            }
        });
    }


}
