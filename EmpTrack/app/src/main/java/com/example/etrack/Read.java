package com.example.etrack;


import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.etrack.databinding.ActivityReadBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.etrack.databinding.ActivityReadBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Read extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private ActivityReadBinding binding;
    private static final String TAG = "TAG";
    private List<Marker> markers = new ArrayList<>();
    Bitmap bitmap;
    private Timer timer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

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
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bit);
        BitmapDescriptor custom = BitmapDescriptorFactory.fromBitmap(bitmap);


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Call your method that fetches new data from Firestore and updates the markers

                updateMarkers();
            }
        }, 0, 10000);

    }

    private void updateMarkers(){
        BitmapDescriptor custom = BitmapDescriptorFactory.fromBitmap(bitmap);
        db.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Convert data to LatLng
                        List<LatLng> locations = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double latitude = document.getDouble("latitude");
                            double longitude = document.getDouble("longitude");
                            String name = document.getString("name");
                            LatLng location = new LatLng(latitude, longitude);
                            locations.add(location);
                            names.add(name);
                        }

                        for (Marker marker : markers) {
                            marker.remove();
                        }
                        markers.clear();

                        // Add markers to the map
                        for (int i = 0; i < locations.size(); i++) {
                            LatLng location = locations.get(i);
                            String name = names.get(i);
                            Marker marker= mMap.addMarker(new MarkerOptions().position(location).title(name).icon(custom));
                            markers.add(marker);
                            Toast.makeText(Read.this, "Location updated",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), user_dashboard.class);
        startActivity(intent);
        finish();
    }


}