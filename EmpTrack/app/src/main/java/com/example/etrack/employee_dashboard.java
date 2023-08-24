package com.example.etrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.etrack.ShowEmpLoc;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class employee_dashboard extends AppCompatActivity {

    public static final String TAG = "TAG";
    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;
    Button buttonLocation,buttontask;
    private CollectionReference locationsCollectionRef;
    private FusedLocationProviderClient fusedLocClient;
    private static final int REQUEST_LOCATION = 1;
    FirebaseFirestore fstore;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        buttonLocation = findViewById(R.id.location);
        buttontask = findViewById(R.id.task);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowEmpLoc.class);
                startActivity(intent);
                finish();
            }
        });
        buttontask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fusedLocClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Update location every 5 seconds
        locationRequest.setFastestInterval(3000); // Fastest update interval: 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Upload the location data to Firestore
                    uploadLocationData(location.getLatitude(), location.getLongitude());
                }
            }
        };

        // Check if the app has location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocPermissions();
        } else {
            // Start location updates
            startLocUpdates();
        }
    }

    private void startLocUpdates() {
        // Start location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           requestLocPermissions();
        }
        fusedLocClient.requestLocationUpdates(locationRequest, locationCallback, null);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_service_channel")
                .setContentTitle("Location Tracking")
                .setContentText("Location tracking is in progress")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true);
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

    private void stopLocUpdates() {
        // Stop location updates
        fusedLocClient.removeLocationUpdates(locationCallback);
    }




    private void uploadLocationData(double latitude, double longitude) {
        // Create a data object with location values
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("username");
                    Map<String, Object> data = new HashMap<>();
                    data.put("latitude",latitude);
                    data.put("longitude",longitude);
                    data.put("name",name);

                    db.collection("locations").document(userID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(employee_dashboard.this, "Location uploaded as: " + userID,Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(employee_dashboard.this, "Failed to upload location data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private void requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
    }
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), user_dashboard.class);
        startActivity(intent);
        finish();
    }
}