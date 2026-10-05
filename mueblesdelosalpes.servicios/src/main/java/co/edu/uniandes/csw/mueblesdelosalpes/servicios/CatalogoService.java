/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ CatalogoService.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.Mueble;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioCatalogoMockLocal;
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
 * Servicios REST del catálogo de muebles (EJB ServicioCatalogoMock).
 * Base: /webresources/Catalogo
 */
@Path("/Catalogo")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CatalogoService
{
    /**
     * Referencia al EJB del catálogo.
     */
    @EJB
    private IServicioCatalogoMockLocal catalogoEjb;

    /**
     * GET /Catalogo/muebles
     * @return todos los muebles del catálogo
     */
    @GET
    @Path("muebles/")
    public List<Mueble> getTodosLosMuebles()
    {
        return catalogoEjb.darMuebles();
    }

    /**
     * GET /Catalogo/muebles/{id}
     * @param id referencia del mueble
     * @return el mueble o 404 si no existe
     */
    @GET
    @Path("muebles/{id}")
    public Response getMueble(@PathParam("id") long id)
    {
        Mueble mueble = buscar(id);
        if (mueble == null)
        {
            return noExiste(id);
        }
        return Response.ok(mueble).build();
    }

    /**
     * POST /Catalogo/agregar
     * Crea un mueble. La referencia la asigna el sistema.
     * @param mueble mueble en JSON
     * @return 201 con el mueble creado
     */
    @POST
    @Path("agregar/")
    public Response agregarMueble(Mueble mueble)
    {
        if (mueble == null || vacio(mueble.getNombre()) || mueble.getTipo() == null)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El nombre y el tipo (Interior o Exterior) del mueble son obligatorios");
        }
        if (mueble.getCantidad() < 0 || mueble.getPrecio() < 0)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "La cantidad y el precio no pueden ser negativos");
        }
        catalogoEjb.agregarMueble(mueble);
        return Response.status(Response.Status.CREATED).entity(mueble).build();
    }

    /**
     * PUT /Catalogo/actualizar/{id}
     * Reemplaza los datos del mueble con la referencia indicada.
     * @param id referencia del mueble
     * @param mueble datos nuevos en JSON
     * @return el mueble actualizado o 404 si no existe
     */
    @PUT
    @Path("actualizar/{id}")
    public Response actualizarMueble(@PathParam("id") long id, Mueble mueble)
    {
        if (buscar(id) == null)
        {
            return noExiste(id);
        }
        if (mueble == null || vacio(mueble.getNombre()) || mueble.getTipo() == null)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El nombre y el tipo (Interior o Exterior) del mueble son obligatorios");
        }
        if (mueble.getCantidad() < 0 || mueble.getPrecio() < 0)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "La cantidad y el precio no pueden ser negativos");
        }
        mueble.setReferencia(id);
        catalogoEjb.actualizarMueble(mueble);
        return Response.ok(mueble).build();
    }

    /**
     * PUT /Catalogo/removerEjemplar/{id}
     * Descuenta una unidad del inventario del mueble.
     * @param id referencia del mueble
     * @return el mueble con la cantidad actualizada
     */
    @PUT
    @Path("removerEjemplar/{id}")
    public Response removerEjemplar(@PathParam("id") long id)
    {
        Mueble mueble = buscar(id);
        if (mueble == null)
        {
            return noExiste(id);
        }
        if (mueble.getCantidad() <= 0)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "No quedan ejemplares del mueble " + id);
        }
        catalogoEjb.removerEjemplarMueble(id);
        return Response.ok(buscar(id)).build();
    }

    /**
     * DELETE /Catalogo/eliminar/{id}
     * @param id referencia del mueble
     * @return confirmación o 404 si no existe
     */
    @DELETE
    @Path("eliminar/{id}")
    public Response eliminarMueble(@PathParam("id") long id)
    {
        if (buscar(id) == null)
        {
            return noExiste(id);
        }
        catalogoEjb.eliminarMueble(id);
        return Mensaje.respuesta(Response.Status.OK, "Mueble " + id + " eliminado del catálogo");
    }

    private Mueble buscar(long id)
    {
        for (Mueble m : catalogoEjb.darMuebles())
        {
            if (m.getReferencia() == id)
            {
                return m;
            }
        }
        return null;
    }

    private static Response noExiste(long id)
    {
        return Mensaje.respuesta(Response.Status.NOT_FOUND, "No existe un mueble con referencia " + id);
    }

    private static boolean vacio(String s)
    {
        return s == null || s.trim().isEmpty();
    }
}
