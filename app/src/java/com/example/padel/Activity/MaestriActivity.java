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

import com.example.padel.Adapter.MaestriAdapter;
import com.example.padel.Obj.Maestro;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MaestriActivity extends AppCompatActivity {

    private ArrayList<Maestro> maestri;
    private RecyclerView rv;
    private MaestriAdapter adapter;

    DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maestri);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        maestri = new ArrayList<>();
        adapter = new MaestriAdapter(maestri,this,this);

        rv = findViewById(R.id.rvMaestri);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);


        ref= FirebaseDatabase.getInstance().getReference("Maestri");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maestri.clear();
                if(snapshot.exists())
                {
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        Maestro m = ds.getValue(Maestro.class);
                        m.addId(ds.getKey());
                        maestri.add(m);
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