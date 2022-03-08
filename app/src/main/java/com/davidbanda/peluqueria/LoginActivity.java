package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    Validator validator;

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    @NotEmpty(message = "Campo necesario")
    EditText txtUsuario, txtPassword;

    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        btnIngresar = (Button) findViewById(R.id.btnIngresar);

        validator = new Validator(this);
        validator.setValidationListener(this);

        //Definiendo el objeto shared preferences
        SharedPreferences usuariosIngresados=getSharedPreferences("datos", MODE_PRIVATE);
        //Verificando si alguien esta conectado
        String usuarioContenido=usuariosIngresados.getString("usuario", "");

        if(!usuarioContenido.equals("")){
            //Si email esta vacio significa que algien esta conectado
            abrirMenu();
        } else {
            //En el caso que nadie este conectado
        }

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    public void abrirMenu(){
        finish();// Cerrando ventana actual
        //Objeto para manipular la actividad Menu
        Intent ventanaMenu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(ventanaMenu); //Solicitando que se abra el Menu
    }

    @Override
    public void onValidationSucceeded() {
        String usuarioIngresado = txtUsuario.getText().toString();
        String passwordIngresado = txtPassword.getText().toString();
        Call llamadaHTTP=peticionesWeb.consultarUsuarios();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");
                            for(JsonElement usuarioTemporal:listadoObtenido){

                                String usuario = usuarioTemporal.getAsJsonObject().get("usuario_usu").toString();
                                String password = usuarioTemporal.getAsJsonObject().get("password_usu").toString();

                                if (usuarioIngresado.equals(usuario.replace("\"","")) || passwordIngresado.equals(password.replace("\"",""))){
                                    Toast.makeText(getApplicationContext(), "Usuario encontrado abriendo app",
                                            Toast.LENGTH_LONG).show();
                                    SharedPreferences usuariosIngresados = getSharedPreferences("datos", MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = usuariosIngresados.edit(); //editor para escribir valores
                                    editor1.putString("usuario", String.valueOf(usuariosIngresados)); //guardando el valor del email
                                    editor1.commit(); //guardando la informacion
                                    abrirMenu();
                                } else{
                                    /*Toast.makeText(getApplicationContext(), "Usuario no  encontrado",
                                            Toast.LENGTH_LONG).show();*/
                                }

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No se encontraron Usuarios",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al traer los DATOS",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error en la App Web -> " +ex.toString(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION (IP)",
                        Toast.LENGTH_LONG).show();
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