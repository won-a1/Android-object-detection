package com.example.ayush;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class dashboard extends AppCompatActivity implements View.OnClickListener{
    private CardView card3,card2,card1,card4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        card3=(CardView)findViewById(R.id.card1234);
        card2=(CardView)findViewById(R.id.card12345);
        card4=(CardView)findViewById(R.id.card123456);
        card1=(CardView)findViewById(R.id.card1234567);

        card3.setOnClickListener(this);
        card2.setOnClickListener(this);
        card4.setOnClickListener(this);
        card1.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        final FirebaseAuth mAuth;
        mAuth= FirebaseAuth.getInstance();
        switch(v.getId())
        {
            case R.id.card1234:
                Intent ii=new Intent(dashboard.this,MainActivity.class);
                startActivity(ii);
                break;


            case R.id.card12345:
                Intent iz=new Intent(dashboard.this,MainActivity2.class);
                startActivity(iz);
                break;


            case R.id.card123456:
                mAuth.signOut();
                finish();
                startActivity(new Intent(dashboard.this,Userlogin.class));
                break;

            case R.id.card1234567:
                startActivity(new Intent(dashboard.this,MainActivity3.class));
                break;

        }

    }
}

