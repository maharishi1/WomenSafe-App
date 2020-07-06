package com.kazim.womensafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterScreen extends AppCompatActivity {
    private EditText mname,mpassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        mname = findViewById(R.id.name);
        mpassword = findViewById(R.id.password);
        Button registerbtn = findViewById(R.id.registerbtn);
        auth = FirebaseAuth.getInstance();
        Button loginbtn = findViewById(R.id.loginbtn);
        progressBar = findViewById(R.id.progressBar);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mname.getText().toString();
                String password = mpassword.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

                if (TextUtils.isEmpty(name)) {
//                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    mname.setError("Enter Email");
                    return;
                }

                if(!(name.matches(emailPattern))){
//                    Toast.makeText(getApplicationContext(), "Invalid email address!", Toast.LENGTH_SHORT).show();
                    mname.setError("Enter Valid Email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    mpassword.setError("Enter Password");
                    return;
                }

                if (password.length() < 6) {
//                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    mpassword.setError("Password too short");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(name, password)
                        .addOnCompleteListener(RegisterScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                //Toast.makeText(MainActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    String yourString = Objects.requireNonNull(task.getException()).toString();
                                    String target = "Exception:";
                                    String error = yourString.substring(yourString.indexOf(target) + target.length() + 1, yourString.length());

                                    Toast.makeText(RegisterScreen.this, "Error: " + error,
                                            Toast.LENGTH_SHORT).show();
                                } else {
//                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                                    finish();
//
//                                    Toast.makeText(RegisterScreen.this, "Created User", Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        // User is signed in
                                        // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                                        // the user will receive another verification email.
                                        sendVerificationEmail();
                                    }


                                }
                            }
                        });
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterScreen.this, LoginScreen.class);
                RegisterScreen.this.startActivity(myIntent);
                finish();
            }
        });

    }

    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(RegisterScreen.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
