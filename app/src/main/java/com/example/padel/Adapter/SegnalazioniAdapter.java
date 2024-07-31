package com.example.padel.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Obj.Segnalazione;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SegnalazioniAdapter extends RecyclerView.Adapter<SegnalazioniAdapter.ViewHolder> {

    private ArrayList<Segnalazione> segnalazioni;
    private Context context;

    private DatabaseReference ref;

    String nomeSegnalato;
    String nomeSegnalatore;
    private int position;

    public SegnalazioniAdapter(ArrayList<Segnalazione> segnalazioni, Context context) {
        this.segnalazioni = segnalazioni;
        this.context = context;
        ref = FirebaseDatabase.getInstance().getReference("Utenti");
    }

    public SegnalazioniAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.richiesta_row,parent,false);
        return new SegnalazioniAdapter.ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull SegnalazioniAdapter.ViewHolder holder, int position)
    {
        this.position = position;
        nomeSegnalato="";
         nomeSegnalatore="";



        ref = FirebaseDatabase.getInstance().getReference("Utenti");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists())
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        User u = ds.getValue(User.class);
                        Log.d("CERCOB",segnalazioni.get(position).getIdSegnalato());
                        Log.d("CERCOB",segnalazioni.get(position).getIdSegnalatore());
                        if(u.getUserID().equals(segnalazioni.get(position).getIdSegnalato()))
                        {
                            Log.d("nome","nome segnalato ="+u.getNome());
                            nomeSegnalato = u.getNome();
                        }

                        if(u.getUserID().equals(segnalazioni.get(position).getIdSegnalatore()))
                        {
                            nomeSegnalatore = u.getNome();
                        }
                    }
                    holder.segnalazione.setText(nomeSegnalatore+" ha segnalato "+nomeSegnalato +" per il seguente motivo: "+ segnalazioni.get(position).getMotivo());

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("Utenti").child(segnalazioni.get(position).getIdSegnalato());

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if(snapshot.exists())
                         {
                             User u = snapshot.getValue(User.class);
                             u.setStato("BANNATO");
                             u.setReputazione(u.getReputazione()-2.5);
                             ref.getDatabase().getReference("Utenti").child(u.getUserID()).setValue(u);

                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ref = FirebaseDatabase.getInstance().getReference("Segnalazioni").child(segnalazioni.get(position).prendiId());
                ref.removeValue();
                segnalazioni.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.sanziona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("Utenti").child(segnalazioni.get(position).getIdSegnalato());

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            User u = snapshot.getValue(User.class);
                            u.setReputazione(u.getReputazione()-0.5);
                            ref.getDatabase().getReference("Utenti").child(u.getUserID()).setValue(u);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ref = FirebaseDatabase.getInstance().getReference("Segnalazioni").child(segnalazioni.get(position).prendiId());
                ref.removeValue();
                segnalazioni.remove(position);
                notifyItemRemoved(position);

            }
        });

        holder.ignora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("Segnalazioni").child(segnalazioni.get(position).prendiId());
                ref.removeValue();

                segnalazioni.remove(position);
                notifyItemRemoved(position);
            }});
    }



    public int getItemCount() {return segnalazioni.size();}

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView segnalazione;
        private Button ban,sanziona,ignora;

        public ViewHolder(View itemView) {
            super(itemView);
            segnalazione = (TextView) itemView.findViewById(R.id.richiesta_txt);
            ban = (Button) itemView.findViewById(R.id.ban_rich);
            sanziona = (Button) itemView.findViewById(R.id.sanziona_rich);
            ignora = (Button) itemView.findViewById(R.id.ignoraRich);
        }

    }

}
