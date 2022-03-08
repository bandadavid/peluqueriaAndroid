package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

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

public class PaisesActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    ListView lstPaises;
    String[] listaSplit;
    ArrayList<String> listaPaises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paises);

        lstPaises = (ListView) findViewById(R.id.lstPaises);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
        seleccionPaisesClick();
    }


    public void seleccionPaisesClick(){
        lstPaises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaUsuarios.get(position), Toast.LENGTH_SHORT).show();
                //finish();// Cerrando ventana actual
                //Objeto para manipular la actividad Menu
                finish();
                Intent ventanaEditarPais = new Intent(getApplicationContext(), FormularioPaisActivity.class);
                listaSplit = listaPaises.get(position).split("\\|");
                ventanaEditarPais.putExtra("codigo", listaSplit[0].replace(" ", ""));
                ventanaEditarPais.putExtra("nombre", listaSplit[1].replace(" ", ""));
                ventanaEditarPais.putExtra("continente", listaSplit[2].replace(" ", ""));
                startActivity(ventanaEditarPais);
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaSplit[0].replace(" ", ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarPaises(View view){
        listaPaises.clear();
        Call llamadaHTTP=peticionesWeb.consultarPaises();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");

                            for(JsonElement paisTemporal:listadoObtenido){
                                String codigo = paisTemporal.getAsJsonObject().get("id_bd").toString();
                                String nombre = paisTemporal.getAsJsonObject().get("nombre_bd").toString();
                                String continente = paisTemporal.getAsJsonObject().get("continente_bd").toString();

                                listaPaises.add(codigo.replace("\"", "") + " | " + nombre.replace("\"", "")
                                        + " | " +  continente.replace("\"", ""));
                            }
                            ArrayAdapter<String> adaptadorPaises = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaPaises);
                            lstPaises.setAdapter(adaptadorPaises);
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

    public void abrirFormularioPaises(View view){
        finish();// Cerrando ventana actual
        //Objeto para manipular la actividad Menu
        Intent ventanIngreso = new Intent(getApplicationContext(), FormularioPaisActivity.class);
        startActivity(ventanIngreso); //Solicitando que se abra el Menu
    }
}