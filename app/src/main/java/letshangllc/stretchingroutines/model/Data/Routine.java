package letshangllc.stretchingroutines.model.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import letshangllc.stretchingroutines.model.JavaObjects.Stretch;

/**
 * Created by carlburnham on 7/29/17.
 */

public class Routine implements Parcelable{
    public ArrayList<String> Stretches;
    public String downloadURL;
    public String name;

    public Routine(){

    }

    protected Routine(Parcel in) {
        Stretches = in.createStringArrayList();
        downloadURL = in.readString();
        name = in.readString();
    }

    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(Stretches);
        dest.writeString(downloadURL);
        dest.writeString(name);
    }
}
