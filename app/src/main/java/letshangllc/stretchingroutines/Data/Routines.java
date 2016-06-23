package letshangllc.stretchingroutines.Data;

import android.content.Context;

import java.util.ArrayList;

import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.JavaObjects.RoutineItem;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class Routines {
    Context context;

    static RoutineItem backStretch = new RoutineItem(99999, R.drawable.silhouette, "Back Stretch");
    static RoutineItem morningStretch =  new RoutineItem(99998, R.drawable.wake_up, "Morning Stretch");
    static RoutineItem fullBodyStretch = new RoutineItem(99997, R.drawable.full_body_stretch, "Full Body Stretch");



    public Routines(){
        routineItems = new ArrayList<>();
        routineItems.add(backStretch);
        routineItems.add(morningStretch);
        routineItems.add(fullBodyStretch);
    }
    private static ArrayList<RoutineItem> routineItems;

    public static ArrayList<RoutineItem> getRoutines(){
        return routineItems;
    }
}
