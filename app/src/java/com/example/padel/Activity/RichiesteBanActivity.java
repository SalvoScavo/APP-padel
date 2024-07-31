package com.example.padel.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Adapter.SegnalazioniAdapter;
import com.example.padel.Obj.Segnalazione;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RichiesteBanActivity extends AppCompatActivity {

    DatabaseReference ref;

    SegnalazioniAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Segnalazione> segnalazioni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_richieste_ban);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        segnalazioni = new ArrayList<>();
        recyclerView = findViewById(R.id.rcSegnalazioni);
        adapter = new SegnalazioniAdapter(segnalazioni,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        ref = FirebaseDatabase.getInstance().getReference("Segnalazioni");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                segnalazioni.clear();
                if(snapshot.exists())
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        Segnalazione s = ds.getValue(Segnalazione.class);
                        s.aggiungiId(ds.getKey());
                        segnalazioni.add(s);
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