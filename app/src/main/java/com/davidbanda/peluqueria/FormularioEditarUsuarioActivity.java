package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioEditarUsuarioActivity extends AppCompatActivity implements Validator.ValidationListener {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    Validator validator;

    @NotEmpty(message = "Debes llenar el campo")
    EditText txtUsuario, txtApellido, txtNombre, txtCodigo;

    Spinner spnEstados;

    Button btnAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_editar_usuario);
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCodigo = (EditText) findViewById(R.id.txtCodigo);

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

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

        validator = new Validator(this);
        validator.setValidationListener(this);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    public void botonCancelar(View vista){
        Intent ventanaUsuarios = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(ventanaUsuarios);
        finish();
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

    @Override
    public void onValidationSucceeded() {
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

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}