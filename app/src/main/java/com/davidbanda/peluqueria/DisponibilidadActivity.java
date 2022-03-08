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

public class DisponibilidadActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;
    String[] listaSplit;
    ListView lstDisponibilidad;
    ArrayList<String> listaDisponibilidad = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibilidad);

        lstDisponibilidad=findViewById(R.id.lstDisponibilidad);
        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
        seleccionDisponibilidadClick();
    }

    public void seleccionDisponibilidadClick(){

        lstDisponibilidad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finish();
                Intent ventanaEditarDisponibilidad = new Intent(getApplicationContext(), FormularioEditarDisponibilidadActivity.class);
                listaSplit = listaDisponibilidad.get(position).split("\\|");
                ventanaEditarDisponibilidad.putExtra("codigo", listaSplit[0].replace(" ", ""));
                ventanaEditarDisponibilidad.putExtra("fecha", listaSplit[1].replace(" ", ""));
                ventanaEditarDisponibilidad.putExtra("horaInicio", listaSplit[2].replace(" ", ""));
                ventanaEditarDisponibilidad.putExtra("horaFin", listaSplit[3].replace(" ", ""));
                startActivity(ventanaEditarDisponibilidad);
                //Toast.makeText(getApplicationContext(), "Seleccionaste el elemento: " + listaSplit[0].replace(" ", ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDisponibilidades(View view){
        listaDisponibilidad.clear();
        Call llamadaHTTP=peticionesWeb.consultarDisponibilidad();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");

                            for(JsonElement disponibilidadTemporal:listadoObtenido){
                                String codigo = disponibilidadTemporal.getAsJsonObject().get("codigo_dis").toString();
                                String fecha = disponibilidadTemporal.getAsJsonObject().get("fecha_dis").toString();
                                String horainicio = disponibilidadTemporal.getAsJsonObject().get("hora_inicio_dis").toString();
                                String horafin = disponibilidadTemporal.getAsJsonObject().get("hora_fin_dis").toString();

                                listaDisponibilidad.add(codigo.replace("\"","") + " | " + fecha.replace("\"","") + " | " + horainicio.replace("\"","") + " | " + horafin.replace("\"",""));
                            }
                            ArrayAdapter<String> adaptadorDisponibilidad = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaDisponibilidad);
                            lstDisponibilidad.setAdapter(adaptadorDisponibilidad);
                        } else {
                            Toast.makeText(getApplicationContext(), "No se encontraron Disponibilidades",
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
        Intent ventanIngreso = new Intent(getApplicationContext(), FormularioDisponibilidadActivity.class);
        startActivity(ventanIngreso); //Solicitando que se abra el Menu
    }
}