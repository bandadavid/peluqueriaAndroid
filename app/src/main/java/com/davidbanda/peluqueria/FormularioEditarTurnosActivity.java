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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
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

public class FormularioEditarTurnosActivity extends AppCompatActivity implements Validator.ValidationListener {

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    @NotEmpty(message = "Debes llenar el campo")
    EditText txtApellido, txtNombre, txtCelular, txtCodigo;

    Validator validator;

    Spinner spnEstados, spServicios;

    ArrayList<String> listaServicios = new ArrayList<>();

    Button btnAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_editar_turnos);

        txtCodigo = (EditText) findViewById(R.id.txtCodigo);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCelular = (EditText) findViewById(R.id.txtCelular);

        spServicios = (Spinner) findViewById(R.id.spServicios);

        spnEstados = (Spinner) findViewById(R.id.spnEstados);

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

        String[] estados = {"ACTIVO", "INACTIVO"};

        spnEstados.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, estados));

        String codigo = getIntent().getStringExtra("codigo");
        String apellido = getIntent().getStringExtra("apellido");
        String nombre = getIntent().getStringExtra("nombre");
        String celular = getIntent().getStringExtra("celular");


        txtCodigo.setText(codigo);
        txtApellido.setText(apellido);
        txtNombre.setText(nombre);
        txtCelular.setText(celular);

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


    public void eliminarTurno(View view){
        String codigoIngresado = txtCodigo.getText().toString();

        Call llamadaHTTP=peticionesWeb.eliminarReserva(codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarTurnosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Eliminando datos...");
        progressDialog.setTitle("Procesando Turnos");
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
                            Toast.makeText(getApplicationContext(), "Turno eliminado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se elimino el turno",
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
        String codigoIngresado = txtCodigo.getText().toString();
        String apellidoIngresado = txtApellido.getText().toString();
        String nombreIngresado = txtNombre.getText().toString();
        String celularIngresado = txtCelular.getText().toString();
        String estadoIngresado = spnEstados.getSelectedItem().toString();
        String servicioIngresado = spServicios.getSelectedItem().toString();

        Call llamadaHTTP=peticionesWeb.editarReserva(apellidoIngresado, nombreIngresado, celularIngresado, estadoIngresado, servicioIngresado, codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarTurnosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Editando datos...");
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
                            Toast.makeText(getApplicationContext(), "Turno editado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se ingreso el turno",
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