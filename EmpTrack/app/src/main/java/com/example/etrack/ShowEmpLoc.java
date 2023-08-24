package com.example.etrack;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.etrack.databinding.ActivityShowEmpLocBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowEmpLoc extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityShowEmpLocBinding binding;
    FirebaseAuth mAuth;
    String userID,name;
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShowEmpLocBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid(); // Replace with the actual user ID
        DocumentReference locationRef = db.collection("locations").document(userID);

        // Retrieve the location data as a DocumentSnapshot
        locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the latitude and longitude values from the document
                        double latitude = document.getDouble("latitude");
                        double longitude = document.getDouble("longitude");
                        String name = document.getString("name");

                        // Create a new LatLng object
                        LatLng location = new LatLng(latitude, longitude);

                        // Add a marker to the map and move the camera to that location
                        mMap.addMarker(new MarkerOptions().position(location).title(name));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), employee_dashboard.class);
        startActivity(intent);
        finish();
    }

}