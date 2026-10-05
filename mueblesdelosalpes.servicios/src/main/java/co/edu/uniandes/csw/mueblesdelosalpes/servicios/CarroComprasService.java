/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ CarroComprasService.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.Mueble;
import co.edu.uniandes.csw.mueblesdelosalpes.dto.Usuario;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioCarritoMockLocal;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioCatalogoMockLocal;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioRegistroMockLocal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Servicios REST del carrito de compras (EJB ServicioCarritoMock).
 * Base: /webresources/CarroCompras
 */
@Path("/CarroCompras")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CarroComprasService
{
    /**
     * Referencia al EJB de carritos encargada de realizar las operaciones del mismo.
     */
    @EJB
    private IServicioCarritoMockLocal carroEjb;

    /**
     * EJB del catálogo: de ahí se toman el nombre, el precio y las existencias de cada mueble.
     */
    @EJB
    private IServicioCatalogoMockLocal catalogoEjb;

    /**
     * EJB de registro: para encontrar al cliente que realiza la compra.
     */
    @EJB
    private IServicioRegistroMockLocal registroEjb;

    /**
     * GET /CarroCompras/muebles
     * @return la lista JSON con los muebles actuales del carrito
     */
    @GET
    @Path("muebles/")
    public List<Mueble> getTodosLosMuebles()
    {
        return carroEjb.getInventario();
    }

    /**
     * GET /CarroCompras/resumen
     * @return muebles del carrito, total de unidades y precio total
     */
    @GET
    @Path("resumen/")
    public ResumenCarrito getResumen()
    {
        carroEjb.recalcularInventarioTotal();
        return new ResumenCarrito(carroEjb.getInventario(), carroEjb.getTotalUnidades(), carroEjb.getPrecioTotalInventario());
    }

    /**
     * POST /CarroCompras/agregar
     * Agrega una unidad de cada mueble de la lista. Basta con enviar la referencia:
     * [{"referencia": 1}, {"referencia": 4}]
     * Nombre, precio y demás datos se toman del catálogo.
     * @param mb lista de muebles en JSON
     * @return el carrito actualizado
     */
    @POST
    @Path("agregar/")
    public Response agregarMuebles(List<Mueble> mb)
    {
        if (mb == null || mb.isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "Envíe una lista con al menos un mueble");
        }
        // Se valida toda la lista antes de agregar, para no dejar el carrito a medias
        Map<Long, Integer> pedidas = new HashMap<>();
        for (Mueble pedido : mb)
        {
            Mueble catalogo = buscarEnCatalogo(pedido.getReferencia());
            if (catalogo == null)
            {
                return Mensaje.respuesta(Response.Status.NOT_FOUND, "No existe un mueble con referencia " + pedido.getReferencia());
            }
            int total = enCarrito(pedido.getReferencia()) + valor(pedidas.get(pedido.getReferencia())) + 1;
            if (total > catalogo.getCantidad())
            {
                return Mensaje.respuesta(Response.Status.BAD_REQUEST,
                        "No hay unidades suficientes de '" + catalogo.getNombre() + "'. Unidades existentes: " + catalogo.getCantidad());
            }
            pedidas.put(pedido.getReferencia(), valor(pedidas.get(pedido.getReferencia())) + 1);
        }
        for (Mueble pedido : mb)
        {
            Mueble c = buscarEnCatalogo(pedido.getReferencia());
            // Copia con cantidad 0: el EJB le suma la unidad. No se toca el objeto del catálogo.
            carroEjb.agregarItem(new Mueble(c.getReferencia(), c.getNombre(), c.getDescripcion(), c.getTipo(), 0, c.getImagen(), c.getPrecio()));
        }
        return Response.ok(carroEjb.getInventario()).build();
    }

    /**
     * PUT /CarroCompras/comprar/{login}
     * Compra todo lo que hay en el carrito a nombre del cliente indicado: descuenta las
     * existencias del catálogo, guarda la compra en el historial del cliente y vacía el carrito.
     * @param login login del cliente
     * @return historial de compras del cliente
     */
    @PUT
    @Path("comprar/{login}")
    public Response comprar(@PathParam("login") String login)
    {
        Usuario usuario = null;
        for (Usuario u : registroEjb.darClientes())
        {
            if (u.getLogin().equals(login))
            {
                usuario = u;
                break;
            }
        }
        if (usuario == null)
        {
            return Mensaje.respuesta(Response.Status.NOT_FOUND, "El cliente '" + login + "' no está registrado");
        }
        if (carroEjb.getInventario().isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El carrito está vacío");
        }
        for (Mueble item : carroEjb.getInventario())
        {
            Mueble catalogo = buscarEnCatalogo(item.getReferencia());
            if (catalogo == null)
            {
                return Mensaje.respuesta(Response.Status.BAD_REQUEST, "El mueble " + item.getReferencia() + " ya no está en el catálogo");
            }
            if (item.getCantidad() > catalogo.getCantidad())
            {
                return Mensaje.respuesta(Response.Status.BAD_REQUEST,
                        "No hay unidades suficientes de '" + catalogo.getNombre() + "'. Unidades existentes: " + catalogo.getCantidad());
            }
        }
        carroEjb.comprar(usuario);
        carroEjb.recalcularInventarioTotal();
        return Response.ok(usuario.getCompras()).build();
    }

    /**
     * DELETE /CarroCompras/borrar
     * Quita una unidad de cada mueble de la lista (por referencia). Si llega a cero, sale del carrito.
     * @param mb lista de muebles en JSON
     * @return el carrito actualizado
     */
    @DELETE
    @Path("borrar/")
    public Response eliminarMuebles(List<Mueble> mb)
    {
        if (mb == null || mb.isEmpty())
        {
            return Mensaje.respuesta(Response.Status.BAD_REQUEST, "Envíe una lista con al menos un mueble");
        }
        for (Mueble mueble : mb)
        {
            carroEjb.removerItem(mueble, true);
        }
        return Response.ok(carroEjb.getInventario()).build();
    }

    /**
     * DELETE /CarroCompras/limpiar
     * Vacía el carrito.
     * @return confirmación
     */
    @DELETE
    @Path("limpiar/")
    public Response limpiar()
    {
        carroEjb.limpiarLista();
        carroEjb.recalcularInventarioTotal();
        return Mensaje.respuesta(Response.Status.OK, "Carrito vacío");
    }

    private Mueble buscarEnCatalogo(long referencia)
    {
        for (Mueble m : catalogoEjb.darMuebles())
        {
            if (m.getReferencia() == referencia)
            {
                return m;
            }
        }
        return null;
    }

    private int enCarrito(long referencia)
    {
        for (Mueble m : carroEjb.getInventario())
        {
            if (m.getReferencia() == referencia)
            {
                return m.getCantidad();
            }
        }
        return 0;
    }

    private static int valor(Integer i)
    {
        return i == null ? 0 : i;
    }
}
