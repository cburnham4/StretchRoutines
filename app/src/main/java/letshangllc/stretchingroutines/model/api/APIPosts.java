package letshangllc.stretchingroutines.model.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import letshangllc.stretchingroutines.model.JavaObjects.Stretch;

import static letshangllc.stretchingroutines.model.Data.Stretches.*;

/**
 * Created by carlburnham on 7/28/17.
 * Utility used to seed Firebase Storage/Database with stretch images.
 */
public class APIPosts {
    public static Stretch[] stretches = new Stretch[]{upperBackRelease, sideReachStretchLeft, sideReachStretchRight, downWardDog, cowPose, catPose,
            harePose, rightTrianglePose, leftTrianglePose, cobraPose, kneePress, camelPose, boatStrech,
            plank, upwardTablePose, forwardBend, chairPose, fishPose, warriorPoseLeft, warriorPoseRight, warriorPoseLungeLeft, warriorPoseLungeRight,
            sitAndReach, simplePeacock, hamStringStretchLeft, hamStringStretchRight, butterfly};

    private static final String TAG = APIPosts.class.getSimpleName();

    public interface FirebaseListener {
        void firebaseSucceeded(boolean success);
    }

    public static void uploadStretches(Context context) {
        for (Stretch stretch : stretches) {
            uploadImage(context, stretch);
        }
    }

    public static void uploadImage(Context context, final Stretch stretch) {
        Log.i(TAG, "Upload image");

        Uri uri = getUriToDrawable(context, stretch.getDrawableIndex());

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child("Stretches/" + stretch.getName() + ".png");

        storageReference.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    Log.i(TAG, "upload success: ");
                    uploadDownloadURL(downloadUri, stretch);
                })
                .addOnFailureListener(e -> Log.i(TAG, "Firebase failed: " + e.getMessage()));
    }

    private static void uploadDownloadURL(Uri downloadURL, Stretch stretch) {
        Log.i(TAG, "Save download url" + downloadURL.toString());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        stretch.downloadURL = downloadURL.toString();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Stretches");
        databaseReference.child(stretch.getName()).setValue(stretch);
    }

    public static Uri getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {
        return Uri.parse("android.resource://letshangllc.stretchingroutines/" + drawableId);
    }
}
