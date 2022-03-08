package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioEditarDisponibilidadActivity extends AppCompatActivity {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    EditText txtFecha, txtHoraI, txtHoraF, txtCod;

    Button btnFechaHoraInicio, btnHoraInicio, btnHoraFin;

    private int dia,mes,ano,hora,minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_editar_disponibilidad);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);

        txtCod = (EditText) findViewById(R.id.txtCod);
        txtFecha = (EditText) findViewById(R.id.txtFecha);
        txtHoraI = (EditText) findViewById(R.id.txtHoraI);
        txtHoraF = (EditText) findViewById(R.id.txtHoraF);

        btnFechaHoraInicio = (Button) findViewById(R.id.btnFechaHoraInicio);
        btnHoraInicio = (Button) findViewById(R.id.btnHoraInicio);
        btnHoraFin = (Button) findViewById(R.id.btnHoraFin);

        String codigo = getIntent().getStringExtra("codigo");
        String fecha = getIntent().getStringExtra("fecha");
        String horaInicio = getIntent().getStringExtra("horaInicio");
        String horaFin = getIntent().getStringExtra("horaFin");

        txtCod.setText(codigo);
        txtFecha.setText(fecha);
        txtHoraI.setText(horaInicio);
        txtHoraF.setText(horaFin);
    }

    public void mostrarDatePickeFecha(View view){
        if(view == btnFechaHoraInicio){
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if(month+1 < 10 && dayOfMonth < 10){
                        txtFecha.setText(year+"-"+"0"+(month+1)+"-"+"0"+dayOfMonth);
                    } else {
                        txtFecha.setText(year+(month+1)+dayOfMonth);
                    }
                }
            }, dia, mes, ano);
            datePickerDialog.show();
        }
        if(view == btnHoraInicio){
            final Calendar c = Calendar.getInstance();

            /*hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);*/



            TimePickerDialog timePickerDialog = new TimePickerDialog(FormularioEditarDisponibilidadActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hora = hourOfDay;
                    minutos = minute;

                    //Inicializamos

                    String time = hora + ":" + minutos;

                    SimpleDateFormat f24horas = new SimpleDateFormat(
                            "HH:mm"
                    );
                    Date date = null;
                    try {
                        date = f24horas.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    txtHoraI.setText(f24horas.format(date));


                }
            }, 12, 0, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(hora, minutos);
            timePickerDialog.show();
        }
        if(view == btnHoraFin){
            final Calendar c = Calendar.getInstance();
            /*hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);*/

            TimePickerDialog timePickerDialog = new TimePickerDialog(FormularioEditarDisponibilidadActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hora = hourOfDay;
                    minutos = minute;

                    //Inicializamos

                    String time = hora + ":" + minutos;

                    SimpleDateFormat f24horas = new SimpleDateFormat(
                            "HH:mm"
                    );
                    Date date = null;
                    try {
                        date = f24horas.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    txtHoraF.setText(f24horas.format(date));


                }
            }, 12, 0, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(hora, minutos);
            timePickerDialog.show();
        }
    }

    public void editarDisponibilidad(View view){
        String codigoIngresado = txtCod.getText().toString();
        String fechaIngresada = txtFecha.getText().toString();
        String horaIIngresada = txtHoraI.getText().toString();
        String horaFFin = txtHoraF.getText().toString();

        Call llamadaHTTP=peticionesWeb.editarDisponibilidad(fechaIngresada, horaIIngresada, horaFFin, codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarDisponibilidadActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Editando datos...");
        progressDialog.setTitle("Procesando Disponibilidad");
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
                            abrirDisponibilidades();
                            Toast.makeText(getApplicationContext(), "Disponibilidad editada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se ingreso la disponibilidad",
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

    public void eliminarDisponibilidad(View view){
        String codigoIngresado = txtCod.getText().toString();

        Call llamadaHTTP=peticionesWeb.eliminarDisponibilidad(codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarDisponibilidadActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Eliminando datos...");
        progressDialog.setTitle("Procesando Disponibilidad");
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
                            abrirDisponibilidades();
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

    public void botonCancelar(View vista){
        finish();
        Intent ventanaDisponibilidad = new Intent(getApplicationContext(), DisponibilidadActivity.class);
        startActivity(ventanaDisponibilidad);

    }

    public void abrirDisponibilidades(){
        finish();
        Intent ventanaDisponibilidad = new Intent(getApplicationContext(), DisponibilidadActivity.class);
        startActivity(ventanaDisponibilidad);

    }
}