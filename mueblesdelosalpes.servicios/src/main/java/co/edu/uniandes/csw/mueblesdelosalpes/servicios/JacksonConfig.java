/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ JacksonConfig.java
 * Laboratorio 5 - Servicios REST (Muebles de los Alpes)
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package co.edu.uniandes.csw.mueblesdelosalpes.servicios;

import co.edu.uniandes.csw.mueblesdelosalpes.dto.RegistroVenta;
import co.edu.uniandes.csw.mueblesdelosalpes.dto.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Configuración del convertidor JSON (Jackson) que usan todos los servicios.
 */
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper mapper;

    public JacksonConfig()
    {
        mapper = crearMapper();
    }

    /**
     * Crea el ObjectMapper de la aplicación:
     * - Ignora propiedades desconocidas en el JSON de entrada (por ejemplo, si se reenvía
     *   un objeto tal como lo devolvió un GET).
     * - No escribe el comprador dentro de cada RegistroVenta: el usuario contiene sus
     *   compras y cada compra apunta al usuario, lo que generaría un ciclo infinito.
     * @return mapper configurado
     */
    public static ObjectMapper crearMapper()
    {
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        m.addMixInAnnotations(RegistroVenta.class, RegistroVentaMixIn.class);
        return m;
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return mapper;
    }

    /**
     * Anotaciones que Jackson aplica a RegistroVenta sin modificar el DTO del backend.
     */
    abstract static class RegistroVentaMixIn
    {
        @JsonIgnore
        abstract Usuario getComprador();
    }
}
