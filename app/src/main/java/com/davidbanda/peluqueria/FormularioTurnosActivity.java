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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class FormularioTurnosActivity extends AppCompatActivity implements Validator.ValidationListener {
    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    Spinner spServicios;

    Validator validator;

    Button btnFechaInicio, btnHoraInicio, btnAgregar;

    ArrayList<String> listaServicios = new ArrayList<>();

    private int dia,mes,ano,hora,minutos;


    @NotEmpty (message = "Debes llenar el campo")
    EditText txtApellido, txtNombre, txtCelular, txtEstado;

    @NotEmpty (message = "Debes llenar el campo")
    EditText txtFechaInicio, txtHoraInicio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_turnos);

        spServicios = (Spinner) findViewById(R.id.spServicios);

        btnFechaInicio = (Button) findViewById(R.id.btnFechaInicio);
        btnHoraInicio = (Button) findViewById(R.id.btnHoraInicio);

        txtFechaInicio = (EditText) findViewById(R.id.txtFechaInicio);
        txtHoraInicio = (EditText) findViewById(R.id.txtHoraInicio);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCelular = (EditText) findViewById(R.id.txtCelular);
        txtEstado = (EditText) findViewById(R.id.txtEstado);

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

        validator = new Validator(this);
        validator.setValidationListener(this);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
        llenarSpinnerServicios();

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    public void mostrarDatePickeFecha(View view){
        if(view == btnFechaInicio){
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
            datePickerDialog.updateDate(dia, mes, ano);
            datePickerDialog.show();
        }

        if(view == btnHoraInicio){
            final Calendar c = Calendar.getInstance();

            /*hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);*/



            TimePickerDialog timePickerDialog = new TimePickerDialog(FormularioTurnosActivity.this,
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
    }


    public void llenarSpinnerServicios(){
        listaServicios.clear();
        Call llamadaHTTP=peticionesWeb.consultarServi();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");

                            for(JsonElement servicioTemporal:listadoObtenido){
                                String codigo = servicioTemporal.getAsJsonObject().get("codigo_ser").toString();
                                //String nombre = servicioTemporal.getAsJsonObject().get("nombre_ser").toString();
                                /*String descripcion = servicioTemporal.getAsJsonObject().get("descripcion_ser").toString();
                                String precio = servicioTemporal.getAsJsonObject().get("precio_ser").toString();
                                String tiempo = servicioTemporal.getAsJsonObject().get("tiempo_ser").toString();
                                String foto = servicioTemporal.getAsJsonObject().get("foto_ser").toString();*/
                                listaServicios.add(codigo.replace("\"",""));
                            }
                            ArrayAdapter<String> adaptadorServicios = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaServicios);
                            spServicios.setAdapter(adaptadorServicios);
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

    public void botonCancelar(View vista){
        Intent ventanaTurnos = new Intent(getApplicationContext(), TurnosActivity.class);
        startActivity(ventanaTurnos);
        finish();
    }

    public void abrirTurnos(){
        Intent ventanaTurnos = new Intent(getApplicationContext(), TurnosActivity.class);
        startActivity(ventanaTurnos);
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        String fechaIngresada = txtFechaInicio.getText().toString() + " " + txtHoraInicio.getText().toString();
        String apellidoIngresado = txtApellido.getText().toString();
        String nombreIngresado = txtNombre.getText().toString();
        String celularIngresado = txtCelular.getText().toString();
        String servicioIngresado = spServicios.getSelectedItem().toString();

        Call llamadaHTTP=peticionesWeb.guardarTurno(fechaIngresada, apellidoIngresado, nombreIngresado, celularIngresado, servicioIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioTurnosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Enviando datos...");
        progressDialog.setTitle("Procesando Turno");
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
                            abrirTurnos();
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