package com.example.padel.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassificaAdapter extends RecyclerView.Adapter<ClassificaAdapter.ViewHolder> {
    private ArrayList<User> listaUtenti;
    private Context context;
    private Activity activity;

    private FirebaseAuth auth = FirebaseAuth.getInstance();


    public ClassificaAdapter(ArrayList<User> listaUser, Context context,Activity activity) {
        this.listaUtenti = listaUser;
        this.context = context;
        this.activity = activity;
    }

    public ClassificaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.classifica_row,parent,false);
        return new ClassificaAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ClassificaAdapter.ViewHolder holder, int position) {

        if(listaUtenti.get(position).getUserID().equals(auth.getCurrentUser().getUid())) {
            holder.nomecognome.setText(listaUtenti.get(position).getNome() + " " + listaUtenti.get(position).getCognome() + " (TU)");
        holder.btnSegnala.setVisibility(View.GONE);
        holder.btnCompl.setVisibility(View.GONE);
        }
        else
            holder.nomecognome.setText(listaUtenti.get(position).getNome() + " " + listaUtenti.get(position).getCognome());

        holder.ranking.setText("Ranking: " + listaUtenti.get(position).rankToString() + "("+listaUtenti.get(position).getRanking()+")");
        //Glide.with(context).load(listaUtenti.get(position).getPic()).into(holder.foto);
        if(listaUtenti.get(position).getPic() != null){
            Glide.with(context).load(listaUtenti.get(position).getPic()).into(holder.foto);

        }
        holder.btnSegnala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View v1 = LayoutInflater.from(context).inflate(R.layout.segnala_layout,null);
                TextInputEditText txt = v1.findViewById(R.id.dialogBANTXT);
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                        .setTitle("Segnala")
                        .setView(v1)
                        .setPositiveButton("INVIA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String,Object> segn = new HashMap<>();
                                segn.put("idSegnalato",listaUtenti.get(position).getUserID());
                                segn.put("idSegnalatore",auth.getCurrentUser().getUid());
                                segn.put("motivo",txt.getText().toString());

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Segnalazioni");
                                ref.push().setValue(segn);
                                dialog.dismiss();

                                Map<String,Object> notifica = new HashMap<>();
                                notifica.put("idDestinatario",listaUtenti.get(position).getUserID());
                                notifica.put("messaggio","Sei stato segnalato per: "+txt.getText().toString());
                                ref= FirebaseDatabase.getInstance().getReference("Notifiche");
                                ref.push().setValue(notifica);



                            }
                        }).setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }
        });

        holder.btnCompl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View v2 = LayoutInflater.from(context).inflate(R.layout.complimenti_layout,null);

                RatingBar rat = v2.findViewById(R.id.rate);
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                        .setTitle("Valuta utente")
                        .setView(v2)
                        .setPositiveButton("INVIA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,""+rat.getRating(),Toast.LENGTH_SHORT).show();


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Utenti").child(listaUtenti.get(position).getUserID());
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists())
                                        {
                                            User u = snapshot.getValue(User.class);
                                            u.calcolaReputazione(rat.getRating());

                                            ref.setValue(u);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                               // ref.push().setValue(segn);
                                dialog.dismiss();




                            }
                        }).setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }
        });


    }
    public int getItemCount() {
        return listaUtenti.size();
    }

        public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nomecognome,ranking;

        private ImageButton btnSegnala,btnCompl;

        ImageView foto;


        public ViewHolder(View itemView) {
            super(itemView);
            nomecognome = (TextView) itemView.findViewById(R.id.nc_txt);
            ranking = (TextView) itemView.findViewById(R.id.r_txt);
            btnSegnala =  itemView.findViewById(R.id.btn_segnala);
            btnCompl = itemView.findViewById(R.id.btn_comp);
            foto = (ImageView) itemView.findViewById(R.id.fotocampoG);
        }
    }
}
