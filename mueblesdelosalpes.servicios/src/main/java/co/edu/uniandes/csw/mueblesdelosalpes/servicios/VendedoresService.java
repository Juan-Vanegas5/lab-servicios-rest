/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ VendedoresService.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.Vendedor;
import co.edu.uniandes.csw.mueblesdelosalpes.excepciones.OperacionInvalidaException;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioVendedoresMockLocal;
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
 * Servicios REST de vendedores (EJB ServicioVendedoresMock).
 * Base: /webresources/Vendedores
 */
@Path("/Vendedores")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendedoresService
{
    /**
     * Referencia al EJB de vendedores.
     */
    @EJB
    private IServicioVendedoresMockLocal vendedoresEjb;

    /**
     * GET /Vendedores/vendedores
     * @return todos los vendedores
     */
    @GET
    @Path("vendedores/")
    public List<Vendedor> getVendedores()
    {
        return vendedoresEjb.getVendedores();
    }

    /**
     * GET /Vendedores/vendedores/{id}
     * @param id identificación del vendedor
     * @return el vendedor o 404 si no existe
     */
    @GET
    @Path("vendedores/{id}")
    public Response getVendedor(@PathParam("id") long id)
    {
        Vendedor v = buscar(id);
        if (v == null)
        {
            return noExiste(id);
        }
        return Response.ok(v).build();
    }

    /**
     * POST /Vendedores/agregar
     * Crea un vendedor. La identificación la asigna el sistema.
     * @param vendedor vendedor en JSON
     * @return 201 con el vendedor creado
     */
    @POST
    @Path("agregar/")
    public Response agregar(Vendedor vendedor)
    {
        Response invalido = validar(vendedor);
        if (invalido != null)
        {
            return invalido;
        }
        try
        {
            vendedoresEjb.agregarVendedor(vendedor);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, e.getMessage());
        }
        return Response.status(Response.Status.CREATED).entity(vendedor).build();
    }

    /**
     * PUT /Vendedores/actualizar/{id}
     * @param id identificación del vendedor
     * @param vendedor datos nuevos en JSON
     * @return el vendedor actualizado o 404 si no existe
     */
    @PUT
    @Path("actualizar/{id}")
    public Response actualizar(@PathParam("id") long id, Vendedor vendedor)
    {
        if (buscar(id) == null)
        {
            return noExiste(id);
        }
        Response invalido = validar(vendedor);
        if (invalido != null)
        {
            return invalido;
        }
        vendedor.setIdentificacion(id);
        try
        {
            vendedoresEjb.actualizarVendedor(vendedor);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.NOT_FOUND, e.getMessage());
        }
        return Response.ok(vendedor).build();
    }

    /**
     * DELETE /Vendedores/eliminar/{id}
     * @param id identificación del vendedor
     * @return confirmación o 404 si no existe
     */
    @DELETE
    @Path("eliminar/{id}")
    public Response eliminar(@PathParam("id") long id)
    {
        if (buscar(id) == null)
        {
            return noExiste(id);
        }
        try
        {
            vendedoresEjb.eliminarVendedor(id);
        }
        catch (OperacionInvalidaException e)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, e.getMessage());
        }
        return Mensaje.respuesta(Response.Status.OK, "Vendedor " + id + " eliminado");
    }

    private Vendedor buscar(long id)
    {
        for (Vendedor v : vendedoresEjb.getVendedores())
        {
            if (v.getIdentificacion() == id)
            {
                return v;
            }
        }
        return null;
    }

    private static Response noExiste(long id)
    {
        return Mensaje.respuesta(Response.Status.NOT_FOUND, "No existe un vendedor con identificación " + id);
    }

    private static Response validar(Vendedor v)
    {
        if (v == null || v.getNombres() == null || v.getNombres().trim().isEmpty()
                || v.getApellidos() == null || v.getApellidos().trim().isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "Los nombres y apellidos del vendedor son obligatorios");
        }
        if (v.getSalario() < 0 || v.getComisionVentas() < 0)
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El salario y la comisión no pueden ser negativos");
        }
        return null;
    }
}
