/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ RegistroService.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.TipoUsuario;
import co.edu.uniandes.csw.mueblesdelosalpes.dto.Usuario;
import co.edu.uniandes.csw.mueblesdelosalpes.excepciones.OperacionInvalidaException;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioRegistroMockLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Servicios REST de registro de clientes (EJB ServicioRegistroMock).
 * Base: /webresources/Registro
 */
@Path("/Registro")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistroService
{
    /**
     * Referencia al EJB de registro.
     */
    @EJB
    private IServicioRegistroMockLocal registroEjb;

    /**
     * GET /Registro/clientes
     * @return todos los usuarios registrados
     */
    @GET
    @Path("clientes/")
    public List<Usuario> getClientes()
    {
        return registroEjb.darClientes();
    }

    /**
     * GET /Registro/clientes/{login}
     * @param login login del usuario
     * @return el usuario o 404 si no existe
     */
    @GET
    @Path("clientes/{login}")
    public Response getCliente(@PathParam("login") String login)
    {
        Usuario u = buscar(login);
        if (u == null)
        {
            return noExiste(login);
        }
        return Response.ok(u).build();
    }

    /**
     * POST /Registro/registrar
     * Registra un cliente. Obligatorios: login, contraseña, tipoDocumento y documento (distinto de 0).
     * @param usuario usuario en JSON
     * @return 201 con el usuario registrado, o 400 si los datos no son válidos o ya existe
     */
    @POST
    @Path("registrar/")
    public Response registrar(Usuario usuario)
    {
        Response invalido = validar(usuario);
        if (invalido != null)
        {
            return invalido;
        }
        if (usuario.getTipoUsuario() == null)
        {
            usuario.setTipoUsuario(TipoUsuario.Cliente);
        }
        try
        {
            registroEjb.registrar(usuario);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, e.getMessage());
        }
        return Response.status(Response.Status.CREATED).entity(usuario).build();
    }

    /**
     * PUT /Registro/actualizar/{login}
     * Actualiza los datos del cliente. Se conserva su historial de compras.
     * @param login login del cliente
     * @param usuario datos nuevos en JSON
     * @return el usuario actualizado
     */
    @PUT
    @Path("actualizar/{login}")
    public Response actualizar(@PathParam("login") String login, Usuario usuario)
    {
        if (buscar(login) == null)
        {
            return noExiste(login);
        }
        if (usuario == null)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "Envíe los datos del cliente");
        }
        usuario.setLogin(login);
        try
        {
            registroEjb.actualizarCliente(usuario);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, e.getMessage());
        }
        return Response.ok(buscar(login)).build();
    }

    /**
     * DELETE /Registro/eliminar/{login}
     * Solo se puede eliminar un cliente que no haya realizado compras.
     * @param login login del cliente
     * @return confirmación, 404 si no existe o 409 si tiene compras
     */
    @DELETE
    @Path("eliminar/{login}")
    public Response eliminar(@PathParam("login") String login)
    {
        if (buscar(login) == null)
        {
            return noExiste(login);
        }
        try
        {
            registroEjb.eliminarCliente(login);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.CONFLICT, e.getMessage());
        }
        return Mensaje.respuesta(Response.Status.OK, "Cliente '" + login + "' eliminado");
    }

    private Usuario buscar(String login)
    {
        for (Usuario u : registroEjb.darClientes())
        {
            if (u.getLogin().equals(login))
            {
                return u;
            }
        }
        return null;
    }

    private static Response noExiste(String login)
    {
        return Mensaje.respuesta(Response.Status.NOT_FOUND, "El cliente '" + login + "' no está registrado");
    }

    private static Response validar(Usuario u)
    {
        if (u == null || u.getLogin() == null || u.getLogin().trim().isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El login es obligatorio");
        }
        if (u.getContraseña() == null || u.getContraseña().isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (u.getTipoDocumento() == null)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El tipo de documento es obligatorio (CC o TarjetaIdentidad)");
        }
        return null;
    }
}
