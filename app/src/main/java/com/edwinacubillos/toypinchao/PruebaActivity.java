package com.edwinacubillos.toypinchao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwinacubillos.toypinchao.model.Usuarios;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class PruebaActivity extends AppCompatActivity {

    private EditText eNombre, eTelefono, eCorreo;
    private ListView listView;
    private ImageView iFoto;
    private Bitmap bitmap;

    String urlFoto="No ha cargado Foto";

    // private ArrayList<String> nombresList;
    private ArrayList<Usuarios> usuariosList;
    private ArrayAdapter arrayAdapter;
    private String url = "https://firebasestorage.googleapis.com/v0/b/toypinchao.appspot.com/o/flash.jpg?alt=media&token=e57bd226-4a44-4ecb-bbf2-092f17da6029";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) { //No hay usuario logueado
            Intent i = new Intent(PruebaActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        eNombre = findViewById(R.id.eNombre);
        eTelefono = findViewById(R.id.eTelefono);
        eCorreo = findViewById(R.id.eCorreo);
        listView = findViewById(R.id.listView);
        iFoto = findViewById(R.id.iFoto);

        Picasso.get().load(url).into(iFoto);

        //   nombresList = new ArrayList();
        usuariosList = new ArrayList<>();

      /*  arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,nombresList);
         listView.setAdapter(arrayAdapter);*/

        final UsuarioAdapter usuarioAdapter = new UsuarioAdapter(this, usuariosList);
        listView.setAdapter(usuarioAdapter);

        FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(); //toypinchao

        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //    nombresList.clear();
                usuariosList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Usuarios usuarios = snapshot.getValue(Usuarios.class);
                        //            nombresList.add(usuarios.getNombre());
                        usuariosList.add(usuarios);
                    }
                }
                usuarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String uid = usuariosList.get(position).getId();
                //         nombresList.remove(position);
                usuariosList.remove(position);
                databaseReference.child("usuarios").child(uid).removeValue();
                return false;
            }
        });

    }

    public void guardarClicked(View view) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageReference.child("usuarios").child(databaseReference.push().getKey()).
                putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urlFoto = taskSnapshot.getDownloadUrl().toString();
            }
        });

        Usuarios usuarios = new Usuarios(databaseReference.push().getKey(),
                eNombre.getText().toString(),
                eTelefono.getText().toString(),
                eCorreo.getText().toString(),
                urlFoto);

        databaseReference.child("usuarios").child(usuarios.getId()).setValue(usuarios);
    }

    public void onClickedFoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.
                INTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(this,"Error cargando imagen", Toast.LENGTH_SHORT).show();
            } else{
                Uri imagen = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(imagen);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bitmap = BitmapFactory.decodeStream(is);
                    iFoto.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class UsuarioAdapter extends ArrayAdapter<Usuarios> {

        public UsuarioAdapter(@NonNull Context context, ArrayList<Usuarios> data) {
            super(context, R.layout.list_item, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item, null);

            Usuarios usuarios = getItem(position);

            TextView nombre = item.findViewById(R.id.tNombre);
            nombre.setText(usuarios.getNombre());

            TextView correo = item.findViewById(R.id.tCorreo);
            correo.setText(usuarios.getCorreo());

            TextView telefono = item.findViewById(R.id.tTelefono);
            telefono.setText(usuarios.getTelefono());

            return item;
        }
    }
}
















