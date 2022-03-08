package ServiciosWeb;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Servicios {
    @GET("index.php/usuarios/listarUsuarios")
    Call<Object> consultarUsuarios();

    @GET("index.php/disponibilidades/listarDisponibilidades")
    Call<Object> consultarDisponibilidad();

    @GET("index.php/reservas/listarReservas")
    Call<Object> consultarTurnos();

    @GET("index.php/servicios/listarServicios")
    Call<Object> consultarServi();

    @GET("index.php/paises/listarPaises")
    Call<Object> consultarPaises();

    @FormUrlEncoded
    @POST("index.php/usuarios/guardarUsuarios")
    Call<Object> guardarUsuarios(
            @Field("usuario_usu") String usuario,
            @Field("password_usu") String password,
            @Field("estado_usu") String estado,
            @Field("perfil_usu") String perfil,
            @Field("apellido_usu") String apellido,
            @Field("nombre_usu") String nombre
    );

    @FormUrlEncoded
    @POST("index.php/disponibilidades/guardarDisponibilidad")
    Call<Object> guardarDisponibilidad(
            @Field("fecha_dis") String fecha,
            @Field("hora_inicio_dis") String horainicio,
            @Field("hora_fin_dis") String horafin
    );

    @FormUrlEncoded
    @POST("index.php/reservas/guardarReserva")
    Call<Object> guardarTurno(
            @Field("fecha_hora_inicio_sol") String fecha,
            @Field("apellido_sol") String apellido,
            @Field("nombre_sol") String nombre,
            @Field("celular_sol") String celular,
            @Field("nombre_ser") String servicio
    );

    @FormUrlEncoded
    @POST("index.php/servicios/guardarServicios")
    Call<Object> guardarServicios(
            @Field("nombre_ser") String nombre,
            @Field("descripcion_ser") String descripcion,
            @Field("precio_ser") String precio,
            @Field("foto_ser") String foto
    );

    @FormUrlEncoded
    @POST("index.php/paises/guardarPaises")
    Call<Object> guardarPais(
            @Field("nombre_bd") String nombre,
            @Field("continente_bd") String continente
    );

    @FormUrlEncoded
    @POST("index.php/usuarios/editarUsuarios/{codigo_usu}")
    Call<Object> editarUsuarios(
            @Field("usuario_usu") String usuario,
            @Field("estado_usu") String estado,
            @Field("apellido_usu") String apellido,
            @Field("nombre_usu") String nombre,
            @Path("codigo_usu") String codigo
    );

    @GET("index.php/usuarios/eliminarUsuarios/{codigo_usu}")
    Call<Object> eliminarUsuarios(
            @Path("codigo_usu") String codigo
    );

    @FormUrlEncoded
    @POST("index.php/disponibilidades/editarDisponibilidad/{codigo_dis}")
    Call<Object> editarDisponibilidad(
            @Field("fecha_dis") String fecha,
            @Field("hora_inicio_dis") String horaInicio,
            @Field("hora_fin_dis") String horaFin,
            @Path("codigo_dis") String codigo
    );

    @GET("index.php/disponibilidades/eliminarDisponibilidad/{codigo_dis}")
    Call<Object> eliminarDisponibilidad(
            @Path("codigo_dis") String codigo
    );

    @FormUrlEncoded
    @POST("index.php/reservas/editarReserva/{codigo_res}")
    Call<Object> editarReserva(
            @Field("apellido_res") String apellido,
            @Field("nombre_res") String nombre,
            @Field("celular_res") String celular,
            @Field("estado_res") String estado,
            @Field("fk_codigo_ser") String servicio,
            @Path("codigo_res") String codigo
    );

    @GET("index.php/reservas/aliminarReservas/{codigo_res}")
    Call<Object> eliminarReserva(
            @Path("codigo_res") String codigo
    );

    @FormUrlEncoded
    @POST("index.php/servicios/editarServicios/{codigo_ser}")
    Call<Object> editarServicios(
            @Field("nombre_ser") String nombre,
            @Field("descripcion_ser") String descripcion,
            @Field("precio_ser") String precio,
            @Path("codigo_ser") String codigo
    );

    @GET("index.php/servicios/eliminarServicios/{codigo_ser}")
    Call<Object> eliminarServicio(
            @Path("codigo_ser") String codigo
    );
}
