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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioDisponibilidadActivity extends AppCompatActivity implements Validator.ValidationListener {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    Validator validator;

    @NotEmpty (message = "Debes llenar el campo")
    EditText txtHoraFin;

    @NotEmpty (message = "Debes llenar el campo")
    EditText txtFechaInicio, txtHoraInicio;

    Button btnFecha, btnHoraInicio, btnHoraFin, btnAgregar;
    private int dia,mes,ano,hora,minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_disponibilidad);

        txtFechaInicio = (EditText) findViewById(R.id.txtFechaInicio);
        txtHoraInicio = (EditText) findViewById(R.id.txtHoraInicio);
        txtHoraFin = (EditText) findViewById(R.id.txtHoraFin);

        btnFecha = (Button) findViewById(R.id.btnFechaHoraInicio);
        btnHoraInicio = (Button) findViewById(R.id.btnHoraInicio);
        btnHoraFin = (Button) findViewById(R.id.btnHoraFin);

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

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

    public void mostrarDatePickeFecha(View view){
        if(view == btnFecha){
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if(month+1 < 10 && dayOfMonth < 10){
                        txtFechaInicio.setText(year+"-"+"0"+(month+1)+"-"+"0"+dayOfMonth);
                    } else {
                        txtFechaInicio.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                    }
                }
            }, dia, mes, ano);
            datePickerDialog.show();
        }
        if(view == btnHoraInicio){
            final Calendar c = Calendar.getInstance();

            /*hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);*/
            TimePickerDialog timePickerDialog = new TimePickerDialog(FormularioDisponibilidadActivity.this,
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
                    txtHoraInicio.setText(f24horas.format(date));


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

            TimePickerDialog timePickerDialog = new TimePickerDialog(FormularioDisponibilidadActivity.this,
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
                    txtHoraFin.setText(f24horas.format(date));


                }
            }, 12, 0, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(hora, minutos);
            timePickerDialog.show();
        }
    }

    public void botonCancelar(View vista){
        Intent ventanaDisponibilidad = new Intent(getApplicationContext(), DisponibilidadActivity.class);
        startActivity(ventanaDisponibilidad);
        finish();
    }

    public void abrirDisponibilidades(){
        Intent ventanaDisponibilidad = new Intent(getApplicationContext(), DisponibilidadActivity.class);
        startActivity(ventanaDisponibilidad);
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        String fechai = txtFechaInicio.getText().toString();
        String horai = txtHoraInicio.getText().toString();
        String horaf = txtHoraFin.getText().toString();

        Call llamadaHTTP=peticionesWeb.guardarDisponibilidad(fechai, horai, horaf);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioDisponibilidadActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Enviando datos...");
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
                            Toast.makeText(getApplicationContext(), "Usuario ingresado correctamente", Toast.LENGTH_LONG).show();
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