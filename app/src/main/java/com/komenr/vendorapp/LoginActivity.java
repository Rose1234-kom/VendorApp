package com.komenr.vendorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText emailTf, passwordTf;
    private Button loginBtn;
    private TextView signupTxtView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        emailTf = findViewById(R.id.login_email);
        passwordTf = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signupTxtView = findViewById(R.id.signup_txt_view);

        mAuth = FirebaseAuth.getInstance();
        mDbUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        // Event listeners
        signupTxtView.setOnClickListener(view -> {
            Intent signupIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(signupIntent);
        });

        loginBtn.setOnClickListener(view -> {
            showToast("PROCESSING...");
            final String email = Helper.trimText(emailTf);
            final String password = Helper.trimText(passwordTf);

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showToast("Complete all fields!");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    checkUserExistance();
                } else {
                    showToast("Couldn't login, user NOT found!");
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void checkUserExistance() {
        final String userId = mAuth.getCurrentUser().getUid();
        mDbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userId)) {
                    Intent mainPageIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainPageIntent);
                } else {
                    showToast("User not registered!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error: " + error.toString());
            }
        });
    }

    private void showToast(CharSequence text) {
        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}