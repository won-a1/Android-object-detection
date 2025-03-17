package com.example.ayush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class Userregister extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private static final int REQUEST_LOCATION = 1;
    private EditText Name, Email,Phone, Password;
    private Button SignUp;
    private TextView AlreadyRegistered;
    private ImageView UserLogo;
    private RadioButton GenderRadioButton;
    private RadioGroup radioGroup;
    LocationManager locationManager;
    public double latitude, longitude;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private String mLatitudeLabel;
    private String mLongitudeLabel;

    String UName , UEmail , UPhone  , UPassword , CountryCode="+91",JustPhone;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);

        getSupportActionBar().setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        setUpUI();


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                UEmail= Email.getText().toString().trim();
                UPassword= Password.getText().toString().trim();
                JustPhone= Phone.getText().toString().trim();
                UPhone= CountryCode.concat(JustPhone);
                UName = Name.getText().toString();




                if(!UName.isEmpty() && !UEmail.isEmpty()  && !JustPhone.isEmpty() && !UPassword.isEmpty()) {


                    mAuth.createUserWithEmailAndPassword(UEmail, UPassword).
                            addOnCompleteListener(Userregister.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        if (!UName.isEmpty() && !UEmail.isEmpty() && !UPhone.isEmpty() && !UPassword.isEmpty()) {

                                            if (!checkPermissions()) {
                                                requestPermissions();
                                            } else {
                                                getLastLocation();
                                            }
                                            Log.d(TAG, "createUserWithEmail:success");

                                            finish();
                                            startActivity(new Intent(Userregister.this, dashboard.class));
                                            Toast.makeText(Userregister.this, "Registration Successful",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            String error = e.getMessage().toString();

                                            Toast.makeText(Userregister.this, error, Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                }
                            });

                }
                else
                {
                    Snackbar snackbar;
                    snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "         Please  Enter  all  the  Details ", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();

                }

            }
        });




    }
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            latitude=mLastLocation.getLatitude();
                            longitude=mLastLocation.getLongitude();

                            sendUserData();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }
    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.myCoordinatorLayout);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(Userregister.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    private void sendUserData()
    {
        //Getting Instance of Databases
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        //Creating reference for USERS collection
        DocumentReference documentReference = db.collection("Users").document(mAuth.getUid());

        //Setting up the Collection of Data
        Map<String, Object> user = new HashMap<>();

        user.put("Name", UName);
        user.put("Email", UEmail);
        user.put("Mobile", UPhone);
        user.put("Password", UPassword);
        user.put("longitude", String.valueOf(latitude));
        user.put("Latitude", String.valueOf(longitude));
        //Adding Crop in our Database
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + mAuth.getUid());


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                    }
                });

    }

    private void setUpUI()
    {
        Email = (EditText) findViewById(R.id.iEmail);
        Password = (EditText) findViewById(R.id.iPassword);
        Phone = (EditText) findViewById(R.id.iMobile);
        Name = (EditText) findViewById(R.id.iName);
        SignUp = (Button) findViewById(R.id.iSignIn);

    }
}