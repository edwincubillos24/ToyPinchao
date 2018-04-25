package com.edwinacubillos.toypinchao;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwinacubillos.toypinchao.model.Montallantas;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMontallantas extends RecyclerView.Adapter<AdapterMontallantas.MontallantasViewHolder>{

    private ArrayList<Montallantas> montallantasList;
    private int resource;
    private Activity activity;

    public AdapterMontallantas(ArrayList<Montallantas> montallantasList, int resource, Activity activity) {
        this.montallantasList = montallantasList;
        this.resource = resource;
        this.activity = activity;
    }

    @Override
    public MontallantasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new MontallantasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MontallantasViewHolder holder, int position) {
        Montallantas montallantas = montallantasList.get(position);
        holder.bindMontallantas(montallantas, activity);
    }

    @Override
    public int getItemCount() {
        return montallantasList.size();
    }

    public class MontallantasViewHolder extends RecyclerView.ViewHolder{

        private TextView tNombre, tValor;
        private ImageView iFoto;

        public MontallantasViewHolder(View itemView) {
            super(itemView);
            tNombre = itemView.findViewById(R.id.tNombre);
            tValor = itemView.findViewById(R.id.tValor);
            iFoto = itemView.findViewById(R.id.iFoto);
        }

        public void bindMontallantas(Montallantas montallantas, Activity activity) {
            tNombre.setText(montallantas.getNombre());
            tValor.setText(montallantas.getValor());
            Picasso.get().load(montallantas.getFoto()).into(iFoto);

        }
    }
}

