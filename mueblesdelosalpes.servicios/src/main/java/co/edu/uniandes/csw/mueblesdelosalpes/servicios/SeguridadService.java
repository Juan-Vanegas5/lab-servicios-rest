/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ SeguridadService.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.Usuario;
import co.edu.uniandes.csw.mueblesdelosalpes.excepciones.AutenticacionException;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioSeguridadMockLocal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Servicio REST de autenticación (EJB ServicioSeguridadMock).
 * Base: /webresources/Seguridad
 *
 * El EJB solo tiene la operación ingresar. Se expone con POST porque las credenciales
 * deben viajar en el cuerpo de la petición y no en la URL.
 */
@Path("/Seguridad")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeguridadService
{
    /**
     * Referencia al EJB de seguridad.
     */
    @EJB
    private IServicioSeguridadMockLocal seguridadEjb;

    /**
     * POST /Seguridad/ingresar
     * Cuerpo: {"login": "client", "contrasena": "clientclient"}
     * @param credenciales login y contraseña
     * @return el usuario autenticado, o 401 si las credenciales no son válidas
     */
    @POST
    @Path("ingresar/")
    public Response ingresar(Credenciales credenciales)
    {
        if (credenciales == null || credenciales.getLogin() == null || credenciales.getContrasena() == null)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "Envíe login y contrasena");
        }
        try
        {
            Usuario usuario = seguridadEjb.ingresar(credenciales.getLogin(), credenciales.getContrasena());
            return Response.ok(usuario).build();
        }
        catch (AutenticacionException e)
        {
            return Mensaje.respuesta(Response.Status.UNAUTHORIZED, e.getMessage());
        }
    }
}
