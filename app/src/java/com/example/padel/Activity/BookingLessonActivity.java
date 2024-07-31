package com.example.padel.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.padel.Obj.Lezione;
import com.example.padel.Obj.Maestro;
import com.example.padel.Obj.Partita;
import com.example.padel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingLessonActivity extends AppCompatActivity {

    private String orari[] ={"9:00","10:30","12:00","13:30","15:00","16:30","18:00","19:30","21:00","22:30"};

    private String id, idMaestro;


    private DatabaseReference ref ;

    private FirebaseAuth mAuth;
    private CalendarView cal;

    private Button[] buttons_time;

    private ArrayList<String> invitatiS2;
    private String invitatoS1;

    int day,month,year;

    private ArrayList<Lezione> lezioni;

    private  TableLayout table;
   private ImageButton choose;

   private TextView date_txt;

    ArrayList<String> giorniMaestro;
    private String dataSelezionata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        buttons_time = new Button[10];
        table = findViewById(R.id.tableLesson);
        choose = findViewById(R.id.chooseLesson);
        date_txt = findViewById(R.id.dateLesson_txt);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        day+=1;
        id = getIntent().getStringExtra("idCampo");
        idMaestro=getIntent().getStringExtra("idMaestro");
        lezioni=new ArrayList<>();
        giorniMaestro = new ArrayList<>();

        ref =FirebaseDatabase.getInstance().getReference("Maestri").child(idMaestro);

        //GIORNI CHE LAVORA IL MAESTRO
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Maestro m = snapshot.getValue(Maestro.class);
                        giorniMaestro = m.giorniLavorativi();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(int i=0; i<10; i++)
        {
            buttons_time[i] = findViewById(getResources().getIdentifier("btnl_time"+i, "id", getPackageName()));
            final String ora = orari[i];
            buttons_time[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog(ora);

                }});
        }

        getLezioni();

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(BookingLessonActivity.this,
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

                                //prendo il giorno della settimana
                                String giornoSettimana = getDay(year,month,day);

                                for(Button b : buttons_time)
                                {
                                    Log.d("AA","RIPRISTINO PULSANTI");
                                    //partite.clear();
                                    b.setEnabled(true);
                                    b.setTextColor(ContextCompat.getColor(BookingLessonActivity.this,R.color.colorPrimary2));
                                }


                                ArrayList<Lezione> l_filtrate  = filtraLezioni(dataSelezionata);


                                // Toast.makeText(BookingActivity.this, "Trovate "+p_filtrate.size() + "partite", Toast.LENGTH_SHORT).show();


                                findBookingTime(l_filtrate,getDay(year,month,day));



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

    private String getDay(int year,int month,int day)
    {
        Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        switch (c.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.SUNDAY:
                return "Domenica";

            case Calendar.MONDAY:
                return "Lunedi";

            case Calendar.TUESDAY:
                return "Martedi";

            case Calendar.WEDNESDAY:
                return "Mercoledi";

            case Calendar.THURSDAY:
                return "Giovedi";

            case Calendar.SATURDAY:
                return "Sabato";

        }
        return "ERR";
    }


    private void findBookingTime(ArrayList<Lezione> Lfiltrate,String giorno)
    {
        Log.d("GIORNLEZIONE"," = "+giorno);
        for(String g: giorniMaestro)
            Log.d("GIORNIMAESTRO", g);

        Log.d("VERIFICA",""+giorniMaestro.contains(giorno));

        if(!giorniMaestro.contains(giorno))
        {//il maestro  non può lavorare

            for(Button b: buttons_time)
            {
                b.setEnabled(false);
                b.setTextColor(ContextCompat.getColor(this,R.color.colorNonDisponibile));
            }

        }else
        {
          for(Lezione l : lezioni)
          {
              int index = getIndexOf(l.getOraInizio());
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

    private ArrayList<Lezione> filtraLezioni(String data)
    {
        ArrayList<Lezione> lezioniFiltrate  = new ArrayList<>();

        for(Lezione l : lezioni)
        {
            if(l.getGiorno().equals(data))
                lezioniFiltrate.add(l);
        }

        return  lezioniFiltrate;
    }

    private void  getLezioni()
    {

        ref= FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Lezioni");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("giorno", ds.child("giorno").getValue().toString());

                        if (ds.child("idCampo").getValue().equals(id)) {
                            Lezione p = ds.getValue(Lezione.class);
                            Log.d("lezione", p.toString());
                            lezioni.add(p);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showConfirmationDialog(String orario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conferma");
        builder.setMessage("Vuoi prenotare la lezione alle ore " + orario + "?");
        builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Lezione l = new Lezione(dataSelezionata,id,idMaestro,orario,mAuth.getCurrentUser().getUid());
                ref=FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Lezioni");
                ref.push().setValue(l);
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}