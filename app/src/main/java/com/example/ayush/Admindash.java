package com.example.ayush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Admindash extends AppCompatActivity {

    //Creating instances
    private RecyclerView RecHAdmindashView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    FirebaseAuth mAuth;
    String Farmer_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindash);
        getSupportActionBar().setTitle("Your History");

        db = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        RecHAdmindashView = (RecyclerView) findViewById(R.id.iRecyclerAdmindash);
        RecHAdmindashView.setLayoutManager(new LinearLayoutManager(this));

        //Getting the Farmers USER_ID



        //Query for FARMER'S own CROPS
        Query query=db.collection("Users");

        FirestoreRecyclerOptions<UserModel> options= new FirestoreRecyclerOptions.Builder<UserModel>().
                setQuery(query,UserModel.class).build();

        adapter= new FirestoreRecyclerAdapter<UserModel, Admindash.HistoryViewHolder>(options) {
            @NonNull
            @Override
            public Admindash.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowuser,parent,false);

                return new Admindash.HistoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final Admindash.HistoryViewHolder holder, int position, @NonNull UserModel model) {

                holder.C_Name.setText("Name:  "+model.getName());
                holder.S_Price.setText("Email  "+model.getEmail());
                holder.C_Qty.setText("Mobile:  "+model.getMobile());
                final String Longi=model.getLongitude();
                final String Latti=model.getLatitude();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        final String DocId = snapshot.getId();

                        Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("latitude", Longi);
                        intent.putExtra("longitude", Latti);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                        startActivity(intent);
                    }
                });

            }
        };

        RecHAdmindashView.setHasFixedSize(true);
        RecHAdmindashView.setLayoutManager(new LinearLayoutManager(this));
        RecHAdmindashView.setAdapter(adapter);

    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder{

        private TextView C_Name;
        private TextView S_Price;
        private TextView C_Qty;
        private TextView Buyer_Name;
        public HistoryViewHolder(@NonNull final View itemView) {
            super(itemView);

            C_Name=itemView.findViewById(R.id.iCName);
            S_Price=itemView.findViewById(R.id.iSellingPrice);
            C_Qty=itemView.findViewById(R.id.iCQty);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}
