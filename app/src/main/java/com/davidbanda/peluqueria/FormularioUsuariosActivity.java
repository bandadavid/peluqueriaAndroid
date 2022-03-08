package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioUsuariosActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    EditText txtUsuario, txtPassword, txtEstado, txtPerfil, txtApellido, txtNombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuarios);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtEstado = (EditText) findViewById(R.id.txtEstado);
        txtPerfil = (EditText) findViewById(R.id.txtPerfil);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtNombre = (EditText) findViewById(R.id.txtNombre);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
    }

    public void guardarUsuarios(View view){
        String usuarioingresado = txtUsuario.getText().toString();
        String passwordingresado = txtPassword.getText().toString();
        String estadoingresado = txtEstado.getText().toString();
        String perfilingresado = txtPerfil.getText().toString();
        String apellidoingresado = txtApellido.getText().toString();
        String nombreingresado = txtNombre.getText().toString();

        Call llamadaHTTP=peticionesWeb.guardarUsuarios(usuarioingresado, passwordingresado, estadoingresado, perfilingresado, apellidoingresado, nombreingresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioUsuariosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Enviando datos...");
        progressDialog.setTitle("Procesando Usuario");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            abrirUsuarios();
                            Toast.makeText(getApplicationContext(), "Usuario ingresado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se ingreso el usuario",
                                    Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al traer los DATOS",
                                Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                    }
                } catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error en la App Web -> " +ex.toString(),
                            Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION (IP)",
                        Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
            }
        });
    }

    public void botonCancelar(View vista){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
        finish();
    }

    public void abrirUsuarios(){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
        finish();
    }
}