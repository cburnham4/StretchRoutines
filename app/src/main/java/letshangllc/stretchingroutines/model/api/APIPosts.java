package letshangllc.stretchingroutines.model.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import letshangllc.stretchingroutines.model.JavaObjects.Stretch;

import static letshangllc.stretchingroutines.model.Data.Stretches.*;

/**
 * Created by carlburnham on 7/28/17.
 */

public class APIPosts {
    public static Stretch[] stretches = new Stretch[]{upperBackRelease, sideReachStretchLeft, sideReachStretchRight, downWardDog, cowPose, catPose,
            harePose, rightTrianglePose, leftTrianglePose, cobraPose, kneePress, camelPose, boatStrech,
            plank, upwardTablePose, forwardBend, chairPose, fishPose, warriorPoseLeft, warriorPoseRight, warriorPoseLungeLeft, warriorPoseLungeRight,
            sitAndReach, simplePeacock, hamStringStretchLeft, hamStringStretchRight, butterfly};

    private static final String TAG = APIPosts.class.getSimpleName();

    /* Callback to tell the user when the post has been completed */
    public interface FirebaseListener{
        void firebaseSucceeded(boolean success);


    }

    public static void uploadStretches(Context context){
        for(Stretch stretch: stretches){
            uploadImage(context, stretch);
        }
    }
    /*
     * Upload Image to storage
     */
    @SuppressWarnings("VisibleForTests")
    public static void uploadImage(Context context, final Stretch stretch){
        Log.i(TAG, "Upload image");

        Uri uri = getUriToDrawable(context, stretch.getDrawableIndex());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Stretches/"+ stretch.getName() +".png");

        /* Post the image to the database storage */
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "upload success: ");
                Uri uri = taskSnapshot.getDownloadUrl();
                uploadDownloadURL(uri, stretch);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Firebase failed");
                //firebaseListener.firebaseSucceeded(false);
            }
        });
    }

    /*
 * Upload photo downloadUrl to database
 */
    private static void uploadDownloadURL(Uri downloadURL, Stretch stretch){
        Log.i(TAG, "Save download url" + downloadURL.toString());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        stretch.downloadURL = downloadURL.toString();

        /* Point the database to the user's posts */
        DatabaseReference databaseReference = firebaseDatabase.getReference("Stretches");

        /* Create an id for the new post */
        String postId = databaseReference.push().getKey();
        databaseReference.child(stretch.getName()).setValue(stretch);

    }

    /**
     * get uri to drawable or any other resource type if u wish
     * @param context - context
     * @param drawableId - drawable res id
     * @return - uri
     */
    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
//        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                "://" + context.getResources().getResourcePackageName(drawableId)
//                //+ '/' + context.getResources().getResourceTypeName(drawableId)
//                + '/' + context.getResources().getResourceEntryName(drawableId)) ;

        Uri imageUri = Uri.parse("android.resource://letshangllc.stretchingroutines/" + drawableId);
        //context.getResources().getDrawable(drawableId).
        return imageUri;
    }
}
