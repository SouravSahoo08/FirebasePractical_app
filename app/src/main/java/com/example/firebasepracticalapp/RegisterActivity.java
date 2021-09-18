package com.example.firebasepracticalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailText);
        password = findViewById(R.id.pwdTxt);
        Button redg = findViewById(R.id.redgButton);
        auth = FirebaseAuth.getInstance();

        redg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                String pwdTxt = password.getText().toString();

                if (TextUtils.isEmpty(emailTxt) || TextUtils.isEmpty(pwdTxt))
                    Toast.makeText(RegisterActivity.this, "Credentials Empty", Toast.LENGTH_SHORT).show();
                else if (pwdTxt.length() < 6)
                    Toast.makeText(RegisterActivity.this, "password must be of 6 characters", Toast.LENGTH_SHORT).show();
                else
                    registerUser(emailTxt, pwdTxt);
            }
        });
    }

    private void registerUser(String email, String pwd) {

        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        });
    }
}