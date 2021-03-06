package letshangllc.stretchingroutines.model.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.model.JavaObjects.Stretch;

/**
 * Created by carlburnham on 7/29/17.
 */

public class APIRequests {
    private static final String TAG = APIRequests.class.getSimpleName();

    public interface RoutineListener{
        void success(ArrayList<Routine> routines);
    }

    public interface StretchListener{
        void success(ArrayList<Stretch> routines);
    }

    public static void getRoutines(final RoutineListener routineListener){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Routines");

        final ArrayList<RoutineItem> routineItems = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Routine> routines = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String name = dataSnapshot1.getKey();
                    Routine routine = (Routine) dataSnapshot1.getValue(Routine.class);
                    routine.name = name;
                    Log.i(TAG, name);
                    routines.add(routine);
                }
                routineListener.success(routines);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    public static void getStretches(final Routine routine, final StretchListener stretchListener){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Stretches");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Stretch> stretches = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String name = dataSnapshot1.getKey();
                    Stretch stretch = (Stretch) dataSnapshot1.getValue(Stretch.class);

                    Log.i(TAG, name);
                    if(routine.Stretches.contains(stretch.getName())) {
                        stretches.add(stretch);
                    }
                }
                stretchListener.success(stretches);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }
}
