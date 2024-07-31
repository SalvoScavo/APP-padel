package com.example.padel.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.padel.Obj.Campo;
import com.example.padel.Obj.Partita;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {


    String orari[] ={"9:00","10:30","12:00","13:30","15:00","16:30","18:00","19:30","21:00","22:30"};

    String id;

    int nPrenotati;
    DatabaseReference ref ;

    private FirebaseAuth mAuth;
    CalendarView cal;

    private Button[] buttons_time;

    private  ArrayList<String> invitatiS2;
    private String invitatoS1;

    int day,month,year;

    TableLayout table;
    ImageButton choose;

    String dataSelezionata;

    String creatore;

    String toSub;
    String nomeCampo;
    TextView date_txt;
    ArrayList<Partita> partite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);
        creatore="";
        nomeCampo="";
        toSub="";
        mAuth = FirebaseAuth.getInstance();
        buttons_time = new Button[10];
        table = findViewById(R.id.table);
        choose = findViewById(R.id.choose);
        date_txt = findViewById(R.id.date_txt);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        day+=1;
        id = getIntent().getStringExtra("idCampo");

        Log.d("IDCAMPO",id);
        nPrenotati = getIntent().getIntExtra("nPrenotati", 1);
        invitatiS2 = getIntent().getStringArrayListExtra("invitatiS2");
        invitatoS1 = getIntent().getStringExtra("invitatoS1");

        ref = FirebaseDatabase.getInstance().getReference("Utenti").child(mAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                        User u = snapshot.getValue(User.class);

                        creatore = u.getNome() +" "+ u.getCognome();
                        Log.d("Creatore",creatore);

                }else
                {
                    creatore = "err";
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //prendo il nome del campo
        ref = FirebaseDatabase.getInstance().getReference("Campi").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                        Campo u = snapshot.getValue(Campo.class);

                        nomeCampo = u.getNome();

                }else
                {
                    nomeCampo="ERR";
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        partite = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite");
        for(int i=0; i<10; i++)
        {
            buttons_time[i] = findViewById(getResources().getIdentifier("btn_time"+i, "id", getPackageName()));
            final String ora = orari[i];
            buttons_time[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        showConfirmationDialog(ora);

                    ref.getDatabase().getReference("Prenotazioni").child("Partite");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())

                                for(DataSnapshot ds: snapshot.getChildren())
                                {
                                    if(ds.child("idCampo").getValue().equals(id) && ds.child("giorno").getValue().equals(dataSelezionata) && ds.child("oraInizio").getValue().equals(ora))
                                    {
                                        toSub=ds.getKey();
                                        Log.d("TOSUB",""+toSub);
                                    }
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }



        getPartite();


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                year = selectedYear;
                                month = selectedMonth;
                                day = selectedDay;

                                // Formatta la data nel formato dd/MM/yyyy
                                dataSelezionata= String.format("%02d/%02d/%d", day, month + 1, year);
                                date_txt.setText("Hai scelto:"+dataSelezionata);
                                table.setVisibility(View.VISIBLE);

                                for(Button b : buttons_time)
                                {
                                    Log.d("AA","RIPRISTINO PULSANTI");
                                    //partite.clear();
                                    b.setEnabled(true);
                                    b.setTextColor(ContextCompat.getColor(BookingActivity.this,R.color.colorPrimary2));
                                }


                                ArrayList<Partita> p_filtrate  = filtraPartite(dataSelezionata);


                              // Toast.makeText(BookingActivity.this, "Trovate "+p_filtrate.size() + "partite", Toast.LENGTH_SHORT).show();


                                findBookingTime(p_filtrate);



                                //Toast.makeText(BookingActivity.this,"partite trovate : " +partite.size(),Toast.LENGTH_SHORT).show();
                            }
                        }, year, month, day);


                Calendar minDate = Calendar.getInstance();
                minDate.setTimeInMillis(System.currentTimeMillis());
                minDate.add(Calendar.DAY_OF_MONTH,1);

                date.getDatePicker().setMinDate(minDate.getTimeInMillis());

                date.show();




            }
        });


    }




    private void showConfirmationDialog(String orario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conferma");
        builder.setMessage("Vuoi prenotare per "+ nPrenotati + " persone alle ore "+orario +"?");

        // Aggiungi il pulsante "Conferma"
        builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Azione da eseguire se l'utente conferma


                //prendo il nome utente




                int i = 0;

                String n[] = new String[3];
                n[i] =invitatoS1;
                n[1] ="VUOTO";
                n[2] ="VUOTO";
                i=1;
                Log.d("AA","INVITATI S2"+ invitatiS2.size());
                for(String id: invitatiS2)
                {
                    n[i] = id;
                    i++;
                    Map<String,String> not = new HashMap<>();
                    not.put("idDestinatario",id);
                    not.put("messaggio","Sei stato invitato ad una partita giorno "+dataSelezionata + " alle ore " +orario + " da "+creatore+" presso il campo " +nomeCampo);

                    ref.getDatabase().getReference("Notifiche").push().setValue(not);
                }

                Partita p = new Partita(dataSelezionata,id,mAuth.getCurrentUser().getUid(),orario,n[0],n[1],n[2]);


                if(toSub.equals(""))
                    ref.push().setValue(p);
                else
                    ref.getDatabase().getReference("Prenotazioni").child("Partite").child(toSub).setValue(p);



                Intent intent = new Intent(BookingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

               // Log.d("CREAZIONE t","Creazione partita "+ p.toString());


            }
        });

        // Aggiungi il pulsante "Annulla"
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Azione da eseguire se l'utente annulla
                dialog.dismiss();
            }
        });

        // Mostra il dialogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private ArrayList<Partita> filtraPartite(String data)
    {
        ArrayList<Partita> partiteFiltrate  = new ArrayList<>();

        for(Partita p : partite)
        {
            if(p.getGiorno().equals(data))
                partiteFiltrate.add(p);
        }

        return  partiteFiltrate;
    }


    private void getPartite()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("giorno", ds.child("giorno").getValue().toString());

                        if (ds.child("idCampo").getValue().equals(id)) {
                            Partita p = ds.getValue(Partita.class);
                            Log.d("partita", p.toString());
                            partite.add(p);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void findBookingTime(ArrayList<Partita> Pfiltrate)
    {
        Log.d("DIM","totale partite "+ Pfiltrate.size());
        for(Partita p : Pfiltrate)
        {
            Log.d("TURNI", "sto controllando i turni, vogliono prenotare " + nPrenotati);
           ArrayList<String>  invitati = p.prendiInviti();
            Log.d("INVITATI",""+invitati.size());
            if(invitati.size() + 1  >nPrenotati)
            {
                int index = getIndexOf(p.getOraInizio());
                Log.d("PRENOTAZIONI","Part = "+ p.toString() +" invitati " + invitati.size()) ;
                Log.d("INDICE", ""+index);
                if(index != -1)
                {
                    buttons_time[index].setEnabled(false);
                    buttons_time[index].setTextColor(ContextCompat.getColor(this,R.color.colorNonDisponibile));//getResources().getColor(R.color.colorNonDisponibile));
                    //buttons_time[index].setText("ND");
                }

            }



        }
    }




    private int getIndexOf(String ora)
    {
        for(int i=0;i<10;i++)
        {
            if(orari[i].equals(ora))
                return  i;
        }
        return -1;
    }


    public void gestisciOra(View v)
    {
        Log.d("partita", "Elenco partite;");
        for(Partita p : partite)
            Log.d("partita", p.toString());
    }

    private String getFormattedDate(int year, int month, int dayOfMonth)
    {
        String date= "";
        if(dayOfMonth <10)
            date = "0" + dayOfMonth + "/";
        else
            date = dayOfMonth + "/";


        if(month < 10)
            date+= "0" + month + "/" + year;
        else
            date+= month + "/" + year;


        return date;
    }
}