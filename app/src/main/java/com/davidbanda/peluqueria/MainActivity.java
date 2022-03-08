package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetwork(getApplicationContext())){

            Toast.makeText(getApplicationContext(), "Sin Conexión a internet", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Conectado a internet", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void abrirUsuarios(View view){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
    }

    public void abrirDisponibilidad(View view){
        Intent ventanaDisponibilidad = new Intent(getApplicationContext(), DisponibilidadActivity.class);
        startActivity(ventanaDisponibilidad);
    }

    public void abrirTurnos(View view){
        Intent ventanaTurnos = new Intent(getApplicationContext(), TurnosActivity.class);
        startActivity(ventanaTurnos);
    }

    public void abrirServicios(View view){
        Intent ventanaServicios = new Intent(getApplicationContext(), ServiciosActivity.class);
        startActivity(ventanaServicios);
    }
}