package com.example.etrack.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.etrack.Addtask;
import com.example.etrack.ShowEmpLoc;
import com.example.etrack.R;
import com.example.etrack.Read;
import com.example.etrack.user_dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class admin_dashboard extends AppCompatActivity {

    Button buttonLocation,buttontask;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        mAuth = FirebaseAuth.getInstance();
        buttonLocation = findViewById(R.id.location);
        buttontask = findViewById(R.id.task);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Read.class);
                startActivity(intent);
                finish();
            }
        });

        buttontask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Addtask.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), user_dashboard.class);
        startActivity(intent);
        finish();
    }
}