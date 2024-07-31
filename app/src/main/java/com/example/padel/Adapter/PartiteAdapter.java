package com.example.padel.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Activity.SquadreActivity;
import com.example.padel.Obj.Partita;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PartiteAdapter extends  RecyclerView.Adapter<PartiteAdapter.ViewHolder> {

    private ArrayList<Partita> listPartite;

    private Context context;
    private Activity activity;

    DatabaseReference reference ;

    public PartiteAdapter(ArrayList<Partita> listPartite, Context context, Activity activity) {
        this.listPartite = listPartite;
        this.context = context;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
    }


    public PartiteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.match_row, null);
        return new PartiteAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(PartiteAdapter.ViewHolder holder, int position) {

            String data = listPartite.get(position).getGiorno()+ "-"+listPartite.get(position).getOraInizio()+ " "+ listPartite.get(position).getIdCampo();
            holder.dataCampo.setText(data);
        //
        //
        holder.pS1.setText(String.valueOf(listPartite.get(position).getPunteggio1()));
         holder.pS2.setText(String.valueOf(listPartite.get(position).getPunteggio2()));


            if(!listPartite.get(position).getId1S1().equals("VUOTO")) {

                Log.d("CERCO", "1S1: "+listPartite.get(position).getId1S1());
                reference = FirebaseDatabase.getInstance().getReference("Utenti").child(listPartite.get(position).getId1S1());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {

                                Log.d("TEST"," AA " + listPartite.get(position).getId1S1());
                                User u = snapshot.getValue(User.class);
                                Log.d("CERCO", "1S1 NOME: "+u.getNome());
//                               if(u.getUserID().equals(listPartite.get(position).getId1S1()))
                                    holder.invs1p2.setText(u.getNome() + " "+ u.getCognome());


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }else
                holder.invs1p2.setText("-");

        if(!listPartite.get(position).getId1S2().equals("VUOTO")) {

            Log.d("CERCO", "1S2: "+listPartite.get(position).getId1S2());

            reference = FirebaseDatabase.getInstance().getReference("Utenti").child(listPartite.get(position).getId1S2());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {


                            User u = snapshot.getValue(User.class);

                            Log.d("CERCO", "1S2 NOME: " + u.getNome());

                                holder.invs2p1.setText(u.getNome() + " " + u.getCognome());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else
            holder.invs2p1.setText("-");

        if(!listPartite.get(position).getId2S2().equals("VUOTO")) {
            Log.d("CERCO", "2S2: "+listPartite.get(position).getId2S2());

            reference = FirebaseDatabase.getInstance().getReference("Utenti").child(listPartite.get(position).getId2S2());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {

                            User u = snapshot.getValue(User.class);
                            Log.d("CERCO", "2S2 NOME: " + u.getNome());
                            holder.invs2p2.setText(u.getNome() + " " + u.getCognome());


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else
            holder.invs2p2.setText("-");

        holder.btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SquadreActivity.class);
                intent.putExtra("idPartita",listPartite.get(position).IdPartita());
                activity.startActivity(intent);
            }
        });


        holder.btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: "+listPartite.get(position).IdPartita());
                //NOtifica annullamento

                String mess = "La partita in data " + listPartite.get(position).getGiorno() + " alle ore "+ listPartite.get(position).getOraInizio() + " è stata annullata";
                Map<String,Object> notAnnulla1 = new HashMap<>();
                if(!listPartite.get(position).getId1S1().equals("VUOTO")) {
                    notAnnulla1.put("idDestinatario", listPartite.get(position).getId1S1());
                    notAnnulla1.put("messaggio", mess);
                }

                Map<String,Object> notAnnulla2 = new HashMap<>();
                if(!listPartite.get(position).getId1S2().equals("VUOTO")) {
                    notAnnulla2.put("idDestinatario", listPartite.get(position).getId1S2());
                    notAnnulla2.put("messaggio", mess);
                }

                Map<String,Object> notAnnulla3 = new HashMap<>();
                if(!listPartite.get(position).getId2S2().equals("VUOTO")) {
                    notAnnulla3.put("idDestinatario", listPartite.get(position).getId2S2());
                    notAnnulla3.put("messaggio", mess);
                }


                reference = FirebaseDatabase.getInstance().getReference("Notifiche");
                reference.push().setValue(notAnnulla1);
                reference.push().setValue(notAnnulla1);
                reference.push().setValue(notAnnulla3);


                reference= FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite").child(listPartite.get(position).IdPartita());
                reference.removeValue();
            }
        });


    }

    public int getItemCount() {
        return listPartite.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView dataCampo,invs1p2,invs2p1,invs2p2,pS1,pS2;

        private Button btnModifica,btnElimina;

        public ViewHolder(View itemView) {
            super(itemView);
            dataCampo= (TextView) itemView.findViewById(R.id.txt_dataCampo);
            invs1p2 = (TextView) itemView.findViewById(R.id.txt_inv2);
            invs2p1 = (TextView) itemView.findViewById(R.id.txtinv_3);
            invs2p2 = (TextView) itemView.findViewById(R.id.txt_inv4);
            Log.d("TAG", "ViewHolder: " + R.id.punteggioS1_txt);
            Log.d("TAG", "ViewHolder: " + R.id.punteggioS2_txt);
             pS1 =  itemView.findViewById(R.id.punteggioS1_txt);
            pS2 = itemView.findViewById(R.id.punteggioS2_txt);

            btnModifica = (Button) itemView.findViewById(R.id.bModifica);
            btnElimina = (Button) itemView.findViewById(R.id.bDisdici);
        }
    }
}
