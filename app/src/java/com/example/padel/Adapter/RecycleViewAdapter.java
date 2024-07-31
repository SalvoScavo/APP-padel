package com.example.padel.Adapter;





import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.padel.Activity.MaestriActivity;
import com.example.padel.Activity.SquadreActivity;
import com.example.padel.Obj.Campo;
import com.example.padel.R;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {


    private ArrayList<Campo> listaCampo;
    private Context context;

    public RecycleViewAdapter(ArrayList<Campo> listaCampo, Context context) {
        this.listaCampo = listaCampo;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.campo_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(listaCampo.get(position).getUrlFoto()).into(holder.foto);
        holder.nomecampo.setText(listaCampo.get(position).getNome());
        holder.tipologia.setText(listaCampo.get(position).getTipologia());

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "DA IMPLEMENTARE "+ listaCampo.get(position).getNome(), Toast.LENGTH_SHORT).show();


                if(!listaCampo.get(position).getLezioni()) {
                    Intent intent = new Intent(context, SquadreActivity.class);

                    intent.putExtra("id", listaCampo.get(holder.getAdapterPosition()).getId());
                    context.startActivity(intent);
                }else
                {
                    Intent intent = new Intent(context, MaestriActivity.class);

                    intent.putExtra("id", listaCampo.get(holder.getAdapterPosition()).getId());
                    context.startActivity(intent);
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
        private TextView nomecampo,tipologia;

        private ImageView btn;

        public ViewHolder(View itemView) {
            super(itemView);
            foto = (ImageView) itemView.findViewById(R.id.fotocampo);
            nomecampo = (TextView) itemView.findViewById(R.id.nomeCampo_txt);
            tipologia = (TextView) itemView.findViewById(R.id.tipologia_txt);
            btn = itemView.findViewById(R.id.btnPren);
        }
    }
}
