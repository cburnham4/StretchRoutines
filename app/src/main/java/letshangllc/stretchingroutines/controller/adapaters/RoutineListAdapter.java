package letshangllc.stretchingroutines.controller.adapaters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.JavaObjects.RoutineItem;
import letshangllc.stretchingroutines.R;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class RoutineListAdapter extends ArrayAdapter<Routine> {

    private static class ViewHolder {
        CircleImageView routineImg;
        TextView routineName;

    }

    private Context context;

    public RoutineListAdapter(Context context, ArrayList<Routine> items) {
        super(context, R.layout.item_routine, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Routine item = getItem(position);
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

        Glide.with(context).load(item.downloadURL).into(viewHolder.routineImg);

        viewHolder.routineName.setText(item.name);


        // Return the completed view to render on screen
        return convertView;
    }
}
