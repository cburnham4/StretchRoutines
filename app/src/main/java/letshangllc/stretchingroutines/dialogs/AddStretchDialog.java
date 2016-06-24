package letshangllc.stretchingroutines.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import letshangllc.stretchingroutines.R;

/**
 * Created by cvburnha on 6/21/2016.
 */
public class AddStretchDialog extends DialogFragment {
    private static  final String TAG = AddStretchDialog.class.getSimpleName();
    /* Callback for when user presses Add */
    private  Listener mListener;
    private static final int SELECT_PICTURE = 1;
    private Bitmap bitmap;
    private TextView tvPhotoUploaded;

    public interface Listener {
        void onDialogPositiveClick(String name, int duration, String description,Bitmap bitmap);
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
        tvPhotoUploaded = (TextView) view.findViewById(R.id.tvPhotoUploaded);
        Button btnUpload = (Button) view.findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });


        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String durationString = etDuration.getText().toString();
                        String description = etDescription.getText().toString();
                        String name = etName.getText().toString();
                        if(!(durationString.isEmpty() || description.isEmpty() || name.isEmpty())){
                            int duration = Integer.parseInt(durationString);
                            mListener.onDialogPositiveClick(name, duration, description, bitmap);
                        }else{
                            Toast.makeText(getContext(), "One or more fields left blank", Toast.LENGTH_SHORT).show();

                        }



                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddStretchDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if(data == null ){
                    Log.e(TAG, "Data Null");
                }
                bitmap = null;
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = selectedImageUri.getPath();
                    Log.i(TAG, "PATH: " + selectedImagePath);
                    Bitmap bitmap1 = (Bitmap) MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    double width = bitmap1.getWidth()* 1.0;
                    double height = bitmap1.getHeight() * 1.0;
                    if(width>1024 || height>1024){
                        if(width>height){
                            double scale = 1024.0/height;
                            bitmap = Bitmap.createScaledBitmap(bitmap1,(int) (scale*width),
                                    (int)  (height*scale), true);
                        }else{
                            double scale = 1024.0/width;
                            bitmap = Bitmap.createScaledBitmap(bitmap1,(int) (scale*width),
                                    (int)  (height*scale), true);
                        }

                    }

                    tvPhotoUploaded.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}