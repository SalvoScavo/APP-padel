package com.example.padel.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.padel.Obj.Maestro;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GestioneMaestriActivity extends AppCompatActivity {

    private String[] giorni = {"Lunedi","Martedi","Mercoledi","Giovedi","Venerdi","Sabato","Domenica"};

    private Spinner[] spinners;

    private DatabaseReference ref;

    private TextView nome;
    private Button salva;

    private  String id,nomecognome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestione_maestri);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id=getIntent().getStringExtra("idMaestro");

        Log.d("id",""+id);
        nomecognome = "";
        ref = FirebaseDatabase.getInstance().getReference("Maestri").child(id);
        nome = findViewById(R.id.txtNomeMaestro);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Maestro m = snapshot.getValue(Maestro.class);
                    nomecognome = m.getNome() + " "+ m.getCognome();
                    nome.setText(nomecognome);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        spinners = new Spinner[7];
        String[] options ={"Si","No"};
        ArrayAdapter<String>  adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,options);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);


        for(int i = 0; i<7;i++)
        {
            spinners[i] = findViewById(getResources().getIdentifier("spinner_"+i, "id", getPackageName()));
            spinners[i].setAdapter(adapter);
        }

        salva = findViewById(R.id.btnSave);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref = FirebaseDatabase.getInstance().getReference("Maestri").child(id);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            Maestro m = snapshot.getValue(Maestro.class);
                            Map<String,Boolean> giorniLavoro = new HashMap<>();
                            for(int i = 0 ; i < 7; i++)
                            {
                                if(spinners[i].getSelectedItem().toString().equals("Si"))
                                    giorniLavoro.put(giorni[i],true);
                                else
                                    giorniLavoro.put(giorni[i],false);
                            }
                            Log.d("CLICK","ciao");
                            m.setGiorniLavoro(giorniLavoro);
                            ref = FirebaseDatabase.getInstance().getReference("Maestri").child(id);
                            ref.setValue(m);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}