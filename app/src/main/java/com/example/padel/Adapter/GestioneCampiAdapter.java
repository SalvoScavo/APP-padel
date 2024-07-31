package com.example.padel.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.padel.Obj.Campo;
import com.example.padel.Obj.Partita;
import com.example.padel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestioneCampiAdapter  extends RecyclerView.Adapter<GestioneCampiAdapter.ViewHolder> {


        private ArrayList<Campo> listaCampo;
        private Context context;

        private DatabaseReference ref;

        public GestioneCampiAdapter(ArrayList<Campo> listaCampo, Context context) {
            this.listaCampo = listaCampo;
            this.context = context;
        }

        @NonNull
        @Override
        public GestioneCampiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.gestione_campi_row,parent,false);
            return new GestioneCampiAdapter.ViewHolder(view);
        }


        public void onBindViewHolder(@NonNull GestioneCampiAdapter.ViewHolder holder, int position) {
            Glide.with(context).load(listaCampo.get(position).getUrlFoto()).into(holder.foto);
            holder.nomecampo.setText(listaCampo.get(position).getNome());
            holder.tipologia.setText(listaCampo.get(position).getTipologia());

            if(listaCampo.get(position).getLezioni())
                holder.lezioni.setText("LEZIONI:SI");
            else
                holder.lezioni.setText("LEZIONI:NO");


            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "DA IMPLEMENTARE "+ listaCampo.get(position).getNome(), Toast.LENGTH_SHORT).show();
                    if(!listaCampo.get(position).getLezioni())
                    { //CI SONO PARTITE E DEVONO DIVENTARE LEZIONI
                        Log.d("ID", "id campo " +listaCampo.get(position).getId());

                        ref = FirebaseDatabase.getInstance().getReference("Campi").child(listaCampo.get(position).getId());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    Campo c = snapshot.getValue(Campo.class);
                                    c.setLezioni(true);
                                    Map<String, Object>  map  = new HashMap<>();
                                    map.put("lezioni",true);
                                    ref = FirebaseDatabase.getInstance().getReference("Campi").child(listaCampo.get(position).getId());
                                    ref.setValue(c);
                                    //ref.updateChildren(map);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        ref=FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        for(DataSnapshot ds:snapshot.getChildren())
                                        {

                                            Partita p = ds.getValue(Partita.class);
                                            if(p.getIdCampo().equals(listaCampo.get(position).getId()))
                                            {
                                                ref=FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Partite").child(ds.getKey());
                                               ref.removeValue();
                                            }
                                        }

                                    }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }else
                    {
                        ref = FirebaseDatabase.getInstance().getReference("Campi").child(listaCampo.get(position).getId());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    Campo c = snapshot.getValue(Campo.class);
                                    c.setLezioni(false);

                                    ref = FirebaseDatabase.getInstance().getReference("Campi").child(listaCampo.get(position).getId());
                                    ref.setValue(c);
                                    //ref.updateChildren(map);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        ref=FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Lezioni");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    for(DataSnapshot ds:snapshot.getChildren())
                                    {

                                        Partita p = ds.getValue(Partita.class);
                                        if(p.getIdCampo().equals(listaCampo.get(position).getId()))
                                        {
                                            ref=FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Lezioni").child(ds.getKey());
                                            ref.removeValue();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return listaCampo.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView foto;
            private TextView nomecampo,tipologia,lezioni;
            private ImageView btn;

            public ViewHolder(View itemView) {
                super(itemView);
                foto = (ImageView) itemView.findViewById(R.id.fotocampoG);
                nomecampo = (TextView) itemView.findViewById(R.id.GestnomeCampo_txt);
                tipologia = (TextView) itemView.findViewById(R.id.Gesttipologia_txt);
                lezioni = itemView.findViewById(R.id.GestLezioni_txt);
                btn = itemView.findViewById(R.id.btnCambia);
            }
        }
    }

