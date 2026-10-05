/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ Mensaje.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import javax.ws.rs.core.Response;

/**
 * Respuesta JSON de una sola línea: {"mensaje": "..."}.
 * Se usa para confirmaciones y errores.
 */
public class Mensaje
{
    private String mensaje;

    public Mensaje()
    {
    }

    public Mensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public String getMensaje()
    {
        return mensaje;
    }

    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    /**
     * Construye una respuesta HTTP con el código indicado y el mensaje en JSON.
     * @param estado Código HTTP
     * @param texto Mensaje para el cliente
     * @return respuesta lista para devolver desde un servicio
     */
    public static Response respuesta(Response.Status estado, String texto)
    {
        return Response.status(estado).entity(new Mensaje(texto)).build();
    }
}
