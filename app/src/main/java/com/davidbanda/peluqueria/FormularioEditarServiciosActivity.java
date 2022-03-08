package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioEditarServiciosActivity extends AppCompatActivity {

    EditText txtCodigo, txtNombre, txtDescripcion, txtPrecio;

    ImageView imgServicio;

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_editar_servicios);

        txtCodigo = (EditText) findViewById(R.id.txtCodigo);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtPrecio = (EditText) findViewById(R.id.txtPrecio);

        imgServicio = (ImageView) findViewById(R.id.imgServicio);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);

        String codigo = getIntent().getStringExtra("codigo");
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String precio = getIntent().getStringExtra("precio");

        txtCodigo.setText(codigo);
        txtNombre.setText(nombre);
        txtDescripcion.setText(descripcion);
        txtPrecio.setText(precio);
    }


    public void editarServicios(View view){
        String codigoIngresado = txtCodigo.getText().toString();
        String nombreIngresado = txtNombre.getText().toString();
        String descripcionIngresada = txtDescripcion.getText().toString();
        String precioIngresado = txtPrecio.getText().toString();

        Call llamadaHTTP=peticionesWeb.editarServicios(nombreIngresado, descripcionIngresada, precioIngresado, codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarServiciosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Editando datos...");
        progressDialog.setTitle("Procesando Servicio");
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
                            abrirServicios();
                            Toast.makeText(getApplicationContext(), "Servicio editado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se edito el servicio",
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

    public void eliminarServicio(View view){
        String codigoIngresado = txtCodigo.getText().toString();

        Call llamadaHTTP=peticionesWeb.eliminarServicio(codigoIngresado);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioEditarServiciosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Eliminando datos...");
        progressDialog.setTitle("Procesando Servicio");
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
                            abrirServicios();
                            Toast.makeText(getApplicationContext(), "Servicio eliminado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se elimino el servicio",
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
        Intent ventanaServicios = new Intent(getApplicationContext(), ServiciosActivity.class);
        startActivity(ventanaServicios);
        finish();
    }

    public void abrirServicios(){
        Intent ventanaServicios = new Intent(getApplicationContext(), ServiciosActivity.class);
        startActivity(ventanaServicios);
        finish();
    }
}