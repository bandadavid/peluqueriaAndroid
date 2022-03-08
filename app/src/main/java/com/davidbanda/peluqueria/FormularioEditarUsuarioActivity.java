package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioEditarUsuarioActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    EditText txtUsuario, txtApellido, txtNombre, txtCodigo;
    Spinner spnEstados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_editar_usuario);
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCodigo = (EditText) findViewById(R.id.txtCodigo);

        spnEstados = (Spinner) findViewById(R.id.spnEstados);

        String[] estados = {"ACTIVO", "INACTIVO"};

        spnEstados.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, estados));
        String codigo = getIntent().getStringExtra("codigo");
        String usuario = getIntent().getStringExtra("usuario");
        String apellido = getIntent().getStringExtra("apellido");
        String nombre = getIntent().getStringExtra("nombre");
        txtCodigo.setText(codigo);
        txtUsuario.setText(usuario);
        txtApellido.setText(apellido);
        txtNombre.setText(nombre);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
    }

    public void botonCancelar(View vista){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
        finish();
    }

    public void editarUsuarios(View view){
        String usuarioIngresado = txtUsuario.getText().toString();
        String estadoIngresado = spnEstados.getSelectedItem().toString();
        String apellidoIngresado = txtApellido.getText().toString();
        String nombreIngresado = txtNombre.getText().toString();
        String codigoIngresado = txtCodigo.getText().toString();

        Call llamadaHTTP=peticionesWeb.editarUsuarios(usuarioIngresado, estadoIngresado, apellidoIngresado, nombreIngresado, codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarUsuarioActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Editando datos...");
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
                            Toast.makeText(getApplicationContext(), "Usuario editado correctamente", Toast.LENGTH_SHORT).show();
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

    public void eliminarUsuarios(View view){
        String codigoIngresado = txtCodigo.getText().toString();

        Call llamadaHTTP=peticionesWeb.eliminarUsuarios(codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarUsuarioActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Eliminando datos...");
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
                            Toast.makeText(getApplicationContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
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

    public void abrirUsuarios(){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
        finish();
    }
}