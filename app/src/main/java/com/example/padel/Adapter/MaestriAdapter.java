package com.example.padel.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Activity.BookingLessonActivity;
import com.example.padel.Activity.GestioneMaestriActivity;
import com.example.padel.Obj.Maestro;
import com.example.padel.R;

import java.util.ArrayList;

public class MaestriAdapter extends RecyclerView.Adapter<MaestriAdapter.ViewHolder>{

    private ArrayList<Maestro> listaMaestri;
    private Context context;
    private Activity activity;

    private boolean admin;


    public MaestriAdapter(ArrayList<Maestro> l, Context c,Activity a)
    {
        this.listaMaestri = l;
        this.context = c;
        this.activity =a;
        admin = activity.getIntent().getBooleanExtra("admin",false);
        Log.d("ADMIN"," = "+admin);

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.maestri_row,parent,false);
        return new ViewHolder(view);
    }


    public  void onBindViewHolder(MaestriAdapter.ViewHolder holder, int position)
    {
            holder.nome.setText(""+listaMaestri.get(position).getNome() + " " + listaMaestri.get(position).getCognome());

            holder.seleziona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(admin)
                    {
                        Intent i = new Intent(context, GestioneMaestriActivity.class);
                        i.putExtra("idMaestro", listaMaestri.get(position).idMaestro());
                        context.startActivity(i);

                    }else
                    {
                        Intent i = new Intent(context, BookingLessonActivity.class);
                        i.putExtra("idCampo", activity.getIntent().getStringExtra("id"));
                        i.putExtra("idMaestro", listaMaestri.get(position).idMaestro());
                        context.startActivity(i);
                    }
                }
            });

    }


    public int getItemCount() {
        return listaMaestri.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nome;
        private Button seleziona;

        public ViewHolder(View itemView)
        {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeCognomeMastro);
            seleziona = itemView.findViewById(R.id.btnSelezionaMaestro);
        }
    }
}
