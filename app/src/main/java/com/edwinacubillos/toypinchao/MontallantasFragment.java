package com.edwinacubillos.toypinchao;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwinacubillos.toypinchao.model.Montallantas;
import com.edwinacubillos.toypinchao.model.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MontallantasFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterMontallantas;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Montallantas> montallantasList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    public MontallantasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView  = inflater.inflate(R.layout.fragment_montallantas, container, false);

        recyclerView = itemView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        montallantasList = new ArrayList<>();

        adapterMontallantas = new AdapterMontallantas(montallantasList, R.layout.cardview_detalle,
                getActivity());

        recyclerView.setAdapter(adapterMontallantas);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(); //toypinchao

        databaseReference.child("montallantas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //    nombresList.clear();
                montallantasList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Montallantas montallantas = snapshot.getValue(Montallantas.class);
                        //            nombresList.add(uarios.getNombre());
                        montallantasList.add(montallantas);
                    }
                }
                adapterMontallantas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return itemView;
    }

}
