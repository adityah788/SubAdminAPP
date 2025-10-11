package com.example.sub_admin_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {

    private Button login;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubAdmins");

    private TextInputEditText etId, etPass;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.btn_login);
        etId = findViewById(R.id.et_EnterName);
        etPass = findViewById(R.id.et_EnterPass);

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance().getReference("SubAdmins");

        // Auto-login if already logged in
        if(sp.getBoolean("isLoggedIn", false)){
            startActivity(new Intent(loginActivity.this, MainActivity.class));
            finish();
            return;
        }

        login.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if(id.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Please enter ID & Password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase lookup
            ref.child(id).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult().exists()){
                    String storedPass = task.getResult().child("pass").getValue(String.class);

                    if(pass.equals(storedPass)){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                        String name = task.getResult().child("name").getValue(String.class);
                        String phone = task.getResult().child("phone").getValue(String.class);
                        String profilePic = task.getResult().child("profilePic").getValue(String.class);

                        // Save login session
                        sp.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("id", id)
                                .putString("name", name)
                                .putString("phone", phone)
                                .putString("profilePic", profilePic)
                                .apply();

                        startActivity(new Intent(loginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "SubAdmin not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}