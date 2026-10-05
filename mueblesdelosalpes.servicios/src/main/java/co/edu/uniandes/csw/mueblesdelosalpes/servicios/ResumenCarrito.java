/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ResumenCarrito.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.Mueble;
import java.util.List;

/**
 * Estado del carrito: muebles, total de unidades y precio total.
 */
public class ResumenCarrito
{
    private List<Mueble> muebles;

    private int totalUnidades;

    private double precioTotal;

    public ResumenCarrito()
    {
    }

    public ResumenCarrito(List<Mueble> muebles, int totalUnidades, double precioTotal)
    {
        this.muebles = muebles;
        this.totalUnidades = totalUnidades;
        this.precioTotal = precioTotal;
    }

    public List<Mueble> getMuebles()
    {
        return muebles;
    }

    public void setMuebles(List<Mueble> muebles)
    {
        this.muebles = muebles;
    }

    public int getTotalUnidades()
    {
        return totalUnidades;
    }

    public void setTotalUnidades(int totalUnidades)
    {
        this.totalUnidades = totalUnidades;
    }

    public double getPrecioTotal()
    {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal)
    {
        this.precioTotal = precioTotal;
    }
}
