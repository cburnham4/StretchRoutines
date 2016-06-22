package letshangllc.stretchingroutines.adapaters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import letshangllc.stretchingroutines.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;

/**
 * Created by cvburnha on 6/21/2016.
 */
public class StretchesAdapter extends ArrayAdapter<Stretch> {

    private static class ViewHolder {
        CircleImageView routineImg;
        TextView routineName;
    }

    public StretchesAdapter(Context context, ArrayList<Stretch> stretches){
        super(context, R.something, stretches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Stretch item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_routine, parent, false);
            viewHolder.routineImg = (CircleImageView) convertView.findViewById(R.id.img_routine);
            viewHolder.routineName = (TextView) convertView.findViewById(R.id.tv_routine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object

        viewHolder.routineImg.setImageDrawable(ContextCompat.getDrawable(getContext(), item.getRoutineIconIndex()));
        viewHolder.routineName.setText(item.getName());


        // Return the completed view to render on screen
        return convertView;
    }



}
