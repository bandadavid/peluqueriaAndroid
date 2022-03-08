package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class UsuariosActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    ListView lstUsuarios;
    String[] listaSplit;
    ArrayList<String> listaUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        lstUsuarios=findViewById(R.id.lstUsuarios);
        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
        seleccionUsuarioClick();

    }

    public void seleccionUsuarioClick(){

        lstUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaUsuarios.get(position), Toast.LENGTH_SHORT).show();
                //finish();// Cerrando ventana actual
                //Objeto para manipular la actividad Menu
                finish();
                Intent ventanaEditarUsuario = new Intent(getApplicationContext(), FormularioEditarUsuarioActivity.class);
                listaSplit = listaUsuarios.get(position).split("\\|");
                ventanaEditarUsuario.putExtra("codigo", listaSplit[0].replace(" ", ""));
                ventanaEditarUsuario.putExtra("usuario", listaSplit[3].replace(" ", ""));
                ventanaEditarUsuario.putExtra("apellido", listaSplit[2].replace(" ", ""));
                ventanaEditarUsuario.putExtra("nombre", listaSplit[1].replace(" ", ""));
                startActivity(ventanaEditarUsuario);
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaSplit[0].replace(" ", ""), Toast.LENGTH_SHORT).show();
            }
        });




    }

    public void consultarUsuarios(View view){
        listaUsuarios.clear();
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
                                String codigo = usuarioTemporal.getAsJsonObject().get("codigo_usu").toString();
                                String nombre = usuarioTemporal.getAsJsonObject().get("nombre_usu").toString();
                                String apellido = usuarioTemporal.getAsJsonObject().get("apellido_usu").toString();
                                String usuario = usuarioTemporal.getAsJsonObject().get("usuario_usu").toString();
                                String perfil = usuarioTemporal.getAsJsonObject().get("perfil_usu").toString();
                                //String password = usuarioTemporal.getAsJsonObject().get("password_usu").toString();

                                listaUsuarios.add(codigo.replace("\"", "") + " | " + nombre.replace("\"", "")
                                        + " | " +  apellido.replace("\"", "") + " | " +  usuario.replace("\"", "") + " | " +  perfil.replace("\"", ""));
                            }
                            ArrayAdapter<String> adaptadorUsuarios = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaUsuarios);
                            lstUsuarios.setAdapter(adaptadorUsuarios);
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

    public void abrirFormularioIngreso(View view){
        finish();// Cerrando ventana actual
        //Objeto para manipular la actividad Menu
        Intent ventanIngreso = new Intent(getApplicationContext(), FormularioUsuariosActivity.class);
        startActivity(ventanIngreso); //Solicitando que se abra el Menu
    }
}