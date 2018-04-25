package com.edwinacubillos.toypinchao;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.edwinacubillos.toypinchao.model.Usuarios;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText eCorreo, eContrasena;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        eCorreo = findViewById(R.id.eCorreo);
        eContrasena = findViewById(R.id.eContrasena);
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login Facebook: ","OK");
                signInFacebook(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d("Login Facebook: ","Cancelado por Usuario");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Login Facebook: ","Error");
            }
        });


        getHashes();
        inicializar();
    }

    private void signInFacebook(AccessToken accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.
                getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            goMainActivity();
                        } else {
                                Log.d("Facebook Error:",task.getException().toString());

                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void getHashes() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.edwinacubillos.toypinchao",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    goMainActivity();
                    Log.d("FirebaseUser","Usuario Logeado: "+firebaseUser.getEmail());
                } else {
                    Log.d("FirebaseUser","No hay usuario logeado");

                }

            }
        };
    }

    public void iniciarSesionClicked(View view) {
        iniciarsesion(eCorreo.getText().toString(), eContrasena.getText().toString());
    }

    private void iniciarsesion(String correo, String contrasena) {
        firebaseAuth.signInWithEmailAndPassword(correo, contrasena).
                addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            goMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesion",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void goMainActivity() {
        crearUsuario();
        Intent i =  new Intent(LoginActivity.this, PruebaActivity.class);
        startActivity(i);
        finish();
    }

    private void crearUsuario() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //toypinchao

        databaseReference.child("usuarios").child(firebaseUser.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Log.d("Existe: ","SI");
                        } else{
                            Log.d("Existe: ","NO");
                            Usuarios usuarios = new Usuarios (firebaseUser.getUid(),
                                    firebaseUser.getDisplayName(),
                                    firebaseUser.getPhoneNumber(),
                                    firebaseUser.getEmail());
                            databaseReference.child("usuarios").child(firebaseUser.getUid()).setValue(usuarios);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void crearCuentaClicked(View view) {
        crearCuenta(eCorreo.getText().toString(), eContrasena.getText().toString());
    }

    private void crearCuenta(String correo, String contrasena) {
        firebaseAuth.createUserWithEmailAndPassword(correo, contrasena).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(LoginActivity.this, "Cuenta Creada",
                                   Toast.LENGTH_SHORT).show();
                       } else {
                           Toast.makeText(LoginActivity.this, "Error al crear"+task.getException(),
                                   Toast.LENGTH_SHORT).show();
                       }
                    }
    });
    }


}
