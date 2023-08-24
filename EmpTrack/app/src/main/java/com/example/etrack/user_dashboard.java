package com.example.etrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.etrack.Admin.admin_dashboard;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class user_dashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button button, buttonAdmin,buttonEmployee;
    TextView textView;
    private FirebaseFirestore db;
    String user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setIcon(R.drawable.logo_modified);
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        buttonAdmin = findViewById(R.id.admin);
        buttonEmployee = findViewById(R.id.employee);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();

            }
        });

        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(user.getUid());
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists() && documentSnapshot.getBoolean("isAdmin")) {
                                Intent intent = new Intent(getApplicationContext(), admin_dashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "You are not authorized to access this page.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                /*Intent intent = new Intent(getApplicationContext(), admin_dashboard.class);
                startActivity(intent);
                finish();*/

            }
        });

        buttonEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), employee_dashboard.class);
                startActivity(intent);
                finish();
            }
        });


    }
}