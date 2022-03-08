package com.davidbanda.peluqueria;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormularioServiciosActivity extends AppCompatActivity {

    int IMG_REQUEST = 21;

    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    EditText txtNombre, txtDescripcion, txtPrecio;

    ImageView imgServicio;
    Button btnFoto;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_servicios);

        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtPrecio = (EditText) findViewById(R.id.txtPrecio);

        btnFoto = (Button) findViewById(R.id.btnFoto);
        imgServicio = (ImageView) findViewById(R.id.imgServicio);

        btnFoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMG_REQUEST);
            }
        });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgServicio.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void guardarServicio(View view){
        String nombreIngresado = txtNombre.getText().toString();
        String descripcionIngresada = txtDescripcion.getText().toString();
        String precioIngresado = txtPrecio.getText().toString();
        String fotoIngresada = imgServicio.toString();

        Call llamadaHTTP=peticionesWeb.guardarServicios(nombreIngresado, descripcionIngresada, precioIngresado, fotoIngresada);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(FormularioServiciosActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Enviando datos...");
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