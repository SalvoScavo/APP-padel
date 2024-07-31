package com.example.padel.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.R;
import com.example.padel.Adapter.RecycleViewInvitaUtenti;
import com.example.padel.Obj.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InvitiActivity extends AppCompatActivity {

    private int retRes;

    private RecycleViewInvitaUtenti adapter;

    private RecyclerView recyclerView;

    private ArrayList<User> listaUtenti;

    private ArrayList<String> idInvitati;
    private DatabaseReference ref ;

    RadioGroup order;

    private FirebaseAuth mAuth;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviti);

        recyclerView =findViewById(R.id.recVi);
        listaUtenti= new ArrayList<>();

        idInvitati = getIntent().getStringArrayListExtra("invitati");
        adapter = new RecycleViewInvitaUtenti(listaUtenti,this,this);

        order = findViewById(R.id.rgroup);
        ref = FirebaseDatabase.getInstance().getReference("Utenti");

        mAuth = FirebaseAuth.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUtenti.clear();

                if(snapshot.exists())
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        User u= ds.getValue(User.class);
                        if(!u.getUserID().equals(mAuth.getCurrentUser().getUid()) && !u.getStato().equalsIgnoreCase("BANNATO") && !idInvitati.contains(u.getUserID()))
                            listaUtenti.add(u);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        order.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbRANK)
                {
                    //ordina per rank
                    Query q = ref.orderByChild("ranking");
                    q.addValueEventListener(new ValueEventListener() {

                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listaUtenti.clear();
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    User u = ds.getValue(User.class);
                                    if (!u.getUserID().equals(mAuth.getCurrentUser().getUid()) && !u.getStato().equalsIgnoreCase("BANNATO") && !idInvitati.contains(u.getUserID()))
                                        listaUtenti.add(u);
                                }
                                adapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                }else if (checkedId == R.id.rbCognome)
                {
                    //ordina per cognome
                    Query q = ref.orderByChild("cognome");
                    q.addValueEventListener(new ValueEventListener() {

                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listaUtenti.clear();
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    User u = ds.getValue(User.class);
                                    if (!u.getUserID().equals(mAuth.getCurrentUser().getUid()) && !u.getStato().equalsIgnoreCase("BANNATO") && !idInvitati.contains(u.getUserID()))
                                        listaUtenti.add(u);
                                }
                                adapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                }
            }
        });



      //  Toast.makeText(this,"val "+retRes,Toast.LENGTH_SHORT).show();




    }

    public void svuota(View view)

    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("nome", "VUOTO");
        int retRes= getIntent().getIntExtra("button",0);
        resultIntent.putExtra("idBtn", retRes);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    public void chiudi(View view)
    {
         // Chiudi l'activity

    }
}