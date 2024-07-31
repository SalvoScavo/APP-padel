package com.example.padel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.padel.Obj.Partita;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SquadreActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
   private String idCampo;

   private boolean isModifying,partitaFinita;

   private String idPartita;
    private int nPrenotati;

    EditText p1,p2;

    private Partita p;

    private  User creatore,giocatore2S1,giocatore1S2,giocatore2S2;

    private String idInvS1;
    private ArrayList<String> idInvS2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squadre);
        nPrenotati=0;
        idCampo = getIntent().getStringExtra("id");
        idPartita = getIntent().getStringExtra("idPartita");


        if(idPartita != null)
            isModifying = true;
        else
            isModifying = false;

        partitaFinita=false;



        idInvS2 = new ArrayList<>();
        idInvS1 = "VUOTO";

            activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = result.getData();
                                if (data != null) {
                                    String risultato = data.getStringExtra("nome");

                                    risultato += " " + data.getIntExtra("idBtn", -1);

                                    String nome = data.getStringExtra("nome");


                                    if (data.getIntExtra("idBtn", -1) != -1) {
                                        Button b = findViewById(data.getIntExtra("idBtn", -1));
                                        if (!nome.equals("VUOTO")) {
                                            if (b.getId() == R.id.btn_addS1P2) {
                                                idInvS1 = data.getStringExtra("invitato");
                                            } else {
                                                idInvS2.add(data.getStringExtra("invitato"));
                                            }


                                            b.setText(nome);
                                            nPrenotati++;
                                        } else {

                                            b.setText("+");
                                            nPrenotati--;
                                        }
                                    }


                                    // Usa i dati come necessario
                                  //  Toast.makeText(SquadreActivity.this, "Risultato ricevuto: " + risultato, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });


        if(isModifying)
        {//STA MODIFICANDO
             p1 = findViewById(R.id.pSquadra1);
            p2 = findViewById(R.id.pSquadra2);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite").child(idPartita);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        p = snapshot.getValue(Partita.class);
                        Date d;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            d = sdf.parse(p.getGiorno());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        if(d.before(new Date()))
                        {
                            //parita finitaù
                            partitaFinita = true;
                            Button b = findViewById(R.id.btn_addS1P2);
                            b.setVisibility(View.GONE);
                            Button b2 = findViewById(R.id.btn_addS2P2);
                            b2.setVisibility(View.GONE);
                            Button b3 = findViewById(R.id.btnTU);
                            b3.setVisibility(View.GONE);
                            Button b4 = findViewById(R.id.btn_addS2P1);
                            b4.setVisibility(View.GONE);

                            p1.setVisibility(View.VISIBLE);
                            p2.setVisibility(View.VISIBLE);


                        }else
                        {
                            partitaFinita=false;
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

           // Toast.makeText(this,"MODIFICA", Toast.LENGTH_SHORT).show();

        }
    }


    public void avanti(View v)
    {
        if(!isModifying) {
            Intent i = new Intent(this, BookingActivity.class);
            Log.d("idCampo", idCampo);
            i.putExtra("idCampo", idCampo);
            i.putExtra("nPrenotati", nPrenotati + 1);

            i.putStringArrayListExtra("invitatiS2", idInvS2);
            i.putExtra("invitatoS1", idInvS1);
            startActivity(i);
        }else {
            if (partitaFinita) {
                int punteggioS1 = Integer.parseInt(p1.getText().toString());
                int punteggioS2 = Integer.parseInt(p2.getText().toString());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite").child(idPartita);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            p = snapshot.getValue(Partita.class);


                            Log.d("punteggioS1", punteggioS1 + "");
                            Log.d("punteggioS2", punteggioS2 + "");
                            p.setPunteggio1(punteggioS1);
                            p.setPunteggio2(punteggioS2);
                            ref.setValue(p);



                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Utenti");
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.exists())
                                    {
                                        for(DataSnapshot s : snapshot.getChildren())
                                        {
                                            User u = s.getValue(User.class);
                                            if(u.getUserID().equals(p.getIdCreatore()))
                                                creatore = u;

                                            if(u.getUserID().equals(p.getId1S1()))
                                                giocatore2S1 = u;

                                            if(u.getUserID().equals(p.getId1S2()))
                                                giocatore1S2 = u;

                                            if(u.getUserID().equals(p.getId2S2()))
                                                giocatore2S2 = u;

                                        }

                                        if(creatore!=null && giocatore2S1!=null && giocatore1S2!=null && giocatore2S2!=null)
                                        {
                                            Log.d("NONULL","tutti sono stati trovato");
                                            if(punteggioS1==punteggioS2)
                                            {
                                                creatore.setRanking(creatore.getRanking()+5);
                                                giocatore2S1.setRanking(giocatore2S1.getRanking()+5);
                                                giocatore1S2.setRanking(giocatore1S2.getRanking()+5);
                                                giocatore2S2.setRanking(giocatore2S2.getRanking()+5);

                                            } else if(punteggioS1>punteggioS2)
                                            {
                                                creatore.setRanking(creatore.getRanking()+10);
                                                giocatore2S1.setRanking(giocatore2S1.getRanking()+10);
                                                giocatore1S2.setRanking(giocatore1S2.getRanking()-10);
                                                giocatore2S2.setRanking(giocatore2S2.getRanking()-10);

                                            }else if(punteggioS1<punteggioS2)
                                            {
                                                creatore.setRanking(creatore.getRanking()-10);
                                                giocatore2S1.setRanking(giocatore2S1.getRanking()-10);
                                                giocatore1S2.setRanking(giocatore1S2.getRanking()+10);
                                                giocatore2S2.setRanking(giocatore2S2.getRanking()+10);

                                            }

                                            ref2.child(creatore.getUserID()).setValue(creatore);
                                            ref2.child(giocatore2S1.getUserID()).setValue(giocatore2S1);
                                            ref2.child(giocatore1S2.getUserID()).setValue(giocatore1S2);
                                            ref2.child(giocatore2S2.getUserID()).setValue(giocatore2S2);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            Log.d("NONULL","PROBLEMSS");

                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            } else {
            //    Toast.makeText(this, "DA FARE", Toast.LENGTH_SHORT).show();
                //fare la query per aggiornare la tabella
                int i = 0;
                String n[] = new String[3];
                n[i] = idInvS1;
                n[1] = "VUOTO";
                n[2] = "VUOTO";
                i = 1;

                for (String id : idInvS2) {
                    n[i] = id;
                    i++;
                }

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite").child(idPartita);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            p = snapshot.getValue(Partita.class);
                            p.setId1S1(n[0]);
                            p.setId1S2(n[1]);
                            p.setId2S2(n[2]);
                            ref.setValue(p);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }
    }


    public void invita(View view)
    {

        //Toast.makeText(this,"CLick "+ view.getId(), Toast.LENGTH_SHORT).show();


        Intent i = new Intent(this, InvitiActivity.class);
        //passare invitati

        ArrayList<String> topass = new ArrayList<>();
        topass.add(idInvS1);
        topass.addAll(idInvS2);
        i.putStringArrayListExtra("invitati", topass);
        i.putExtra("button", view.getId());
        activityResultLauncher.launch(i);

    }


    public void indietro(View v)
    {
        finish();
    }
}