package letshangllc.stretchingroutines.JavaObjects;

import android.graphics.drawable.Drawable;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class RoutineItem {
    int routineIconIndex;
    String name;
    int id;

    public RoutineItem(int id, int routineIconIndex, String name) {
        this.id = id;
        this.routineIconIndex = routineIconIndex;
        this.name = name;
    }

    public int getRoutineIconIndex() {

        return routineIconIndex;
    }

    public void setRoutineIconIndex(int routineIconIndex) {
        this.routineIconIndex = routineIconIndex;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
