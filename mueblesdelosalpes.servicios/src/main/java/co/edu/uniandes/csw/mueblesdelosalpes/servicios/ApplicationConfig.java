/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ApplicationConfig.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Activa JAX-RS 2.0 (el Jersey que trae GlassFish 4.1) y publica todos los servicios
 * bajo /webresources, igual que lo hacía el servlet de Jersey 1 del web.xml original.
 * Ejemplo: http://localhost:8080/mueblesdelosalpes.servicios/webresources/Catalogo/muebles
 */
@ApplicationPath("webresources")
public class ApplicationConfig extends Application
{
    /**
     * Recursos REST y proveedores que forman la aplicación.
     */
    @Override
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> clases = new HashSet<>();
        // Servicios REST, uno por cada EJB de logica.ejb
        clases.add(CarroComprasService.class);
        clases.add(CatalogoService.class);
        clases.add(RegistroService.class);
        clases.add(SeguridadService.class);
        clases.add(VendedoresService.class);
        // Conversión JSON <-> objetos Java con Jackson
        clases.add(JacksonJsonProvider.class);
        clases.add(JacksonConfig.class);
        return clases;
    }

    /**
     * Se desactiva MOXy, el convertidor JSON por defecto de GlassFish 4.1, porque en esa
     * versión falla con "NoClassDefFoundError: ... BeanValidationHelper". En su lugar se usa Jackson.
     */
    @Override
    public Map<String, Object> getProperties()
    {
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jersey.config.disableMoxyJson", true);
        propiedades.put("jersey.config.server.disableMoxyJson", true);
        return propiedades;
    }
}
