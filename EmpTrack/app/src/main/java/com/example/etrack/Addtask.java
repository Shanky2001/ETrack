package com.example.etrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.etrack.Admin.admin_dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Addtask extends AppCompatActivity {

    private EditText nameEditText, task1EditText, task2EditText, task3EditText;
    private Button submitButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        nameEditText = findViewById(R.id.name);
        task1EditText = findViewById(R.id.task1);
        task2EditText = findViewById(R.id.task2);
        task3EditText = findViewById(R.id.task3);

        submitButton = findViewById(R.id.submit);

        db = FirebaseFirestore.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the task data from the EditText fields
                String name = nameEditText.getText().toString();
                String task1 = task1EditText.getText().toString();
                String task2 = task2EditText.getText().toString();
                String task3 = task3EditText.getText().toString();

                // Create a Map object to store the task data
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("name", name);
                taskData.put("task1", task1);
                taskData.put("task2", task2);
                taskData.put("task3", task3);

                // Add the task data to Firestore
                db.collection("tasks")
                        .add(taskData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Addtask.this, "Task added to Firestore!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Addtask.this, "Error adding task to Firestore!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), admin_dashboard.class);
        startActivity(intent);
        finish();
    }
}