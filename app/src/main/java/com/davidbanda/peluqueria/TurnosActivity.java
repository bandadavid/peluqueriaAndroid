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

public class TurnosActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;
    String[] listaSplit;
    ListView lstTurnos;
    ArrayList<String> listaTurnos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        lstTurnos=findViewById(R.id.lstTurnos);
        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
        seleccionTurnosClick();
    }

    public void seleccionTurnosClick(){

        lstTurnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finish();
                Intent ventanaEditarTurnos = new Intent(getApplicationContext(), FormularioEditarTurnosActivity.class);
                listaSplit = listaTurnos.get(position).split("\\|");
                ventanaEditarTurnos.putExtra("codigo", listaSplit[0].replace(" ", ""));
                ventanaEditarTurnos.putExtra("apellido", listaSplit[1].replace(" ", ""));
                ventanaEditarTurnos.putExtra("nombre", listaSplit[2].replace(" ", ""));
                ventanaEditarTurnos.putExtra("celular", listaSplit[3].replace(" ", ""));
                ventanaEditarTurnos.putExtra("estado", listaSplit[4].replace(" ", ""));
                ventanaEditarTurnos.putExtra("servicio", listaSplit[5].replace(" ", ""));
                startActivity(ventanaEditarTurnos);
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaSplit[0].replace(" ", ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarTurnos(View view){
        listaTurnos.clear();
        Call llamadaHTTP=peticionesWeb.consultarTurnos();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");

                            for(JsonElement turnosTemporal:listadoObtenido){
                                //String fechaInicio = turnosTemporal.getAsJsonObject().get("fecha_hora_inicio_res").toString();
                                String codigo = turnosTemporal.getAsJsonObject().get("codigo_res").toString();
                                String apellido = turnosTemporal.getAsJsonObject().get("apellido_res").toString();
                                String nombre = turnosTemporal.getAsJsonObject().get("nombre_res").toString();
                                String celular = turnosTemporal.getAsJsonObject().get("celular_res").toString();
                                String estado = turnosTemporal.getAsJsonObject().get("estado_res").toString();
                                String servicio = turnosTemporal.getAsJsonObject().get("fk_codigo_ser").toString();
                                listaTurnos.add(codigo.replace("\"","") + " | " + apellido.replace("\"","") + " | " + nombre.replace("\"","") + " | " + celular.replace("\"","") + " | " + estado.replace("\"","") + " | " + servicio.replace("\"",""));
                            }
                            ArrayAdapter<String> adaptadorTurnos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaTurnos);
                            lstTurnos.setAdapter(adaptadorTurnos);
                        } else {
                            /*Toast.makeText(getApplicationContext(), "No se encontraron Turnos",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    } else {
                        /*Toast.makeText(getApplicationContext(), "Error al traer los DATOS",
                                Toast.LENGTH_LONG).show();*/
                    }
                } catch (Exception ex){
                    /*Toast.makeText(getApplicationContext(), "Error en la App Web -> " +ex.toString(),
                            Toast.LENGTH_LONG).show();*/
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
        Intent ventanIngreso = new Intent(getApplicationContext(), FormularioTurnosActivity.class);
        startActivity(ventanIngreso); //Solicitando que se abra el Menu
    }

}