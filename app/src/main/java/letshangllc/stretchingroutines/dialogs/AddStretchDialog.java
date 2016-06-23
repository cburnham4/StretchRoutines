package letshangllc.stretchingroutines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import letshangllc.stretchingroutines.R;

/**
 * Created by cvburnha on 6/21/2016.
 */
public class AddStretchDialog extends DialogFragment {
    /* Callback for when user presses Add */
    private  Listener mListener;

    public interface Listener {
        void onDialogPositiveClick(String name, int duration, String description);
    }

    public void setCallback(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_add_stretch, null);

        final EditText etDuration = (EditText) view.findViewById(R.id.etStretchDuration);
        final EditText etDescription = (EditText) view.findViewById(R.id.etStretchDescription);
        final EditText etName = (EditText) view.findViewById(R.id.etStretchName);


        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int duration = Integer.parseInt(etDuration.getText().toString());
                        String description = etDescription.getText().toString();
                        String name = etName.getText().toString();
                        mListener.onDialogPositiveClick(name, duration, description);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddStretchDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


}