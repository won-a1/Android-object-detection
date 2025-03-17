package com.example.ayush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Userlogin extends AppCompatActivity {
    private EditText Email, Password;
    private Button SignIn;
    private Button SignInWithPhone;
    private TextView SignUp , ForgotPassword;
    private ProgressDialog progress;

    private static final String TAG = "EmailPassword";


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_userlogin);

        setUpUI();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();

        if (user != null)
        {
            startActivity(new Intent(Userlogin.this, dashboard.class));
            finish();

        }

        progress = new ProgressDialog(this);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String UserName = Email.getText().toString().trim();
                String Pass = Password.getText().toString().trim();

                if (!UserName.isEmpty() && !Pass.isEmpty())
                {
                    progress.setTitle("Signing in");
                    progress.setMessage("Santanu is Logging you in");
                    progress.show();
                    if (UserName.equals("admin") && Pass.equals("admin")) {
                        startActivity(new Intent(Userlogin.this, Admindash.class));
                        Toast.makeText(Userlogin.this, "Login Successful",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    }

                    mAuth.signInWithEmailAndPassword(UserName, Pass).
                            addOnCompleteListener(Userlogin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {

                                    if (task.isSuccessful()) {
                                        progress.dismiss();
                                        Log.d(TAG, "createUserWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();

                                            startActivity(new Intent(Userlogin.this, dashboard.class));
                                            Toast.makeText(Userlogin.this, "Login Successful",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();


                                    } else {
                                        progress.dismiss();
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            String err = e.getMessage().toString();
                                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                                        }

                                    }

                                }
                            });
                }

                else if(UserName.isEmpty())
                {
                    Email.requestFocus();
                    Email.setError("Email can't be empty");
                    Snackbar snackbar;
                    snackbar = Snackbar.make(findViewById(R.id.mainCoordinatedLayout), "         Please  Enter Your Email  and  Password ", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();
                }
                else{
                    Password.requestFocus();
                    Password.setError("Password can't be empty");
                    Snackbar snackbar;
                    snackbar = Snackbar.make(findViewById(R.id.mainCoordinatedLayout), "         Please  Enter Your Email  and  Password ", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Userlogin.this,Userregister.class));
            }
        });


    }



    private void setUpUI()
    {

        Email = (EditText) findViewById(R.id.iEmail);
        Password = (EditText) findViewById(R.id.iPassword);
        SignIn = (Button) findViewById(R.id.iSignIn);
        SignUp = (TextView) findViewById(R.id.iSignUp);
        ForgotPassword = (TextView) findViewById(R.id.iForgotPassword);

    }


}
